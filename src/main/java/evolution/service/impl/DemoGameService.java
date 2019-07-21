package evolution.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import evolution.game.Game;
import evolution.components.logger.EvolutionLogger;
import evolution.service.GameService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DemoGameService implements GameService {
    private Injector injector = Guice.createInjector();
    private ServerSocket serverSocket;
    private Game game;
    private EvolutionLogger logger;
    private String serviceId;
    private String serviceName;
    private int servicePort;
    private int serviceIdleTimeoutSeconds;
    private int clientExpireTimeoutSeconds;

    public DemoGameService() {
        this.logger = injector.getInstance(EvolutionLogger.class);
        this.serviceIdleTimeoutSeconds = 0;
        this.clientExpireTimeoutSeconds = 30;
        this.serviceId = UUID.randomUUID().toString();
        this.game = injector.getInstance(Game.class);

        logger.info("Initializing DemoGameService id = " + this.serviceId);
    }

    @Override
    public void createGameService(String serviceName, int port) {
        try {
            logger.info("Creating DemoGameService of " + serviceName + " on port " + port);
            this.serviceName = serviceName;
            this.servicePort = port;

            this.serverSocket = new ServerSocket(port);
            this.serverSocket.setSoTimeout(this.serviceIdleTimeoutSeconds * 1000);
            this.serverSocket.setReuseAddress(true);
            logger.info("Service " + serviceName + "started");

            this.initGame();
            this.listenerStart();
        } catch (Exception e) {
            logger.error("Create game service " + serviceName + " on port " + port + " failed");
            e.printStackTrace();
        }
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public int getServicePort() {
        return servicePort;
    }

    @Override
    public String getServiceHostAddress() {
         return serverSocket.getInetAddress().getHostAddress();
    }

    @Override
    public void shutdownService() {
        try {
            logger.info("Close socket service for " + this.serviceName);

            if (null != serverSocket) {
                this.serverSocket.close();

                // close server and destroy created game
                this.game.destroyGame();
            } else {
                logger.info("Server socket is already closed");
            }
        } catch (Exception e) {
            logger.error("Shutdown server socket failed");
        }
    }

    private boolean initGame() {
        logger.info("Creating game instance");
        String gameId = UUID.randomUUID().toString();

        if (this.game.createGame(gameId, "Game Created By DemoGameService")) {
            logger.info("Game instance created " + gameId);
            return true;
        } else {
            logger.error("Game instance create failed");
            return false;
        }
    }

    private void listenerStart() {
        if (null == serverSocket) {
            logger.error("Server socket is not initialized");
            throw new RuntimeException("Server socket is not initialized");
        }

        logger.info("Starting listener");

        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();

                    logger.info("SERVER "
                            + this.serviceId + this.serviceName
                            + " Accepted socket connection from "
                            + client.getRemoteSocketAddress());
                    
                    String clientId = UUID.randomUUID().toString();

                    if (this.game.addPlayer(clientId, client)) {
                        logger.info("Added new player info " + clientId + " into game");
                    } else {
                        logger.warn("Add new player into game failed, id " + clientId);
                        logger.info("Close socket connection for id " + clientId);
                        client.close();
                        continue;
                    }

                    if (this.game.isGameReady()) {
                        logger.info("Game state is ready, start game");
                        if (this.game.startGame()) {
                            logger.info("Game started");
                        } else {
                            logger.error("Start game failed");
                        }
                    }
                }

                logger.info("Listener stop");
            } catch (Exception e) {
                logger.error("Error happened in socket listening, did you closed game server?");
            }
        }).start();

        logger.info("Listener service started");
    }
}
