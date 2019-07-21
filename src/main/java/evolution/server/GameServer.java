package evolution.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.lang.Exception;
import evolution.components.logger.EvolutionLogger;
import evolution.server.controller.RequestController;


public class GameServer {
    private static GameServer ourInstance = new GameServer();

    public static GameServer getInstance() {
        return ourInstance;
    }

    private static Injector injector;

    private static EvolutionLogger logger;

    private static HttpServer httpServer;

    private static RequestController controller;

    private GameServer() {
        injector = Guice.createInjector();
        logger = injector.getInstance(EvolutionLogger.class);
        controller = injector.getInstance(RequestController.class);
    }

    public static void main(String [] args) {
        logger.info("Game Server Starting ...");
        logger.info("Quantum Evolution Game Server 1.0");

        final int gameServerPort = ServerConstants.GAME_SERVER_PORT;

        try {
            httpServer = HttpServer.create(new InetSocketAddress(gameServerPort), 0);
            HttpContext httpContext = httpServer.createContext("/");
            httpContext.setHandler(exchange -> httpRequestHandler(exchange));

            httpServer.start();
            logger.info("Http server started at " + httpServer.getAddress());
        } catch (Exception e) {
            logger.fatal("Game Server start failed");
            httpServer.stop(0);
            logger.info("Game Shutdown");
        }

        logger.info("Game Server Started at port " + gameServerPort);
    }

    private static void httpRequestHandler(HttpExchange exchange) {
        try {
            controller.proceedRequest(exchange);
        } catch (Exception e) {
            logger.warn("Proceed http request exception");
            e.printStackTrace();
        }
    }
}
