package evolution.server.handler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.net.httpserver.HttpExchange;
import evolution.components.logger.EvolutionLogger;
import evolution.server.ServerConstants;
import evolution.server.http.HttpResponse;
import evolution.service.GameService;
import evolution.service.impl.ChessGameService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestHandler {
    private Injector injector = Guice.createInjector();
    private EvolutionLogger logger;
    private HttpResponse response;
    private volatile Set<GameService> gameServiceSet;

    public RequestHandler() {
        logger = injector.getInstance(EvolutionLogger.class);
        response = injector.getInstance(HttpResponse.class);
        gameServiceSet = new HashSet<>();
    }

    public void testMethod(HttpExchange exchange) {
        try {
            logger.info("Inside of method testMethod of data from " + exchange.getRequestURI());
            response.OK(exchange, "This is the response body in testMethod()");
        } catch (Exception e) {
            e.printStackTrace();
            response.InternalError(exchange);
        }
    }

    public void createNewGameService(HttpExchange exchange) {
        if (gameServiceSet.size() >= ServerConstants.MAX_SERVER_SERVICE_COUNT) {
            response.OK(exchange, "game service count exceed, will not create new service");
            return;
        }

        logger.info("Start new game request");

        int port = (int) (Math.random() * 10000 + 10000);

        ChessGameService chessGameService = injector.getInstance(ChessGameService.class);
        chessGameService.createGameService("ChessGameService_" + port, port);
        gameServiceSet.add(chessGameService);

        logger.info("New game started on port " + port + " service name " + chessGameService.getServiceName());
        response.OK(exchange, "new Game service started on " + port);
    }

    public void closeAllGameService(HttpExchange exchange) {
        if ((null == gameServiceSet) || (gameServiceSet.isEmpty())) {
            logger.info("Empty service list");
            response.OK(exchange, "No game service running");
            return;
        }

        logger.info("There are " + gameServiceSet.size() + " game service running");

        for (GameService service : gameServiceSet) {
            logger.info("Shutdown service " + service.getServiceName() + " on " + service.getServicePort());
            service.shutdownService();
        }

        gameServiceSet.clear();

        logger.info("Close all game service complete");
        response.OK(exchange, "Shutdown game service complete");
    }

    public void listAllGameService(HttpExchange exchange) {
        if ((null == gameServiceSet) || (gameServiceSet.isEmpty())) {
            logger.info("Empty service list");
            response.OK(exchange, "No game service running");
            return;
        }

        logger.info("There are " + gameServiceSet.size() + " game service running");

        List<String> gameInfoList = new ArrayList<>();
        for (GameService service : gameServiceSet) {
            logger.info("Query service " + service.getServiceName() + " on Port " + service.getServicePort());
            gameInfoList.add(
                    "Service Name:"
                            + service.getServiceName()
                            + " Port:"
                            + service.getServicePort()
                            + " Address:"
                            + service.getServiceHostAddress());
        }

        response.OK(exchange, gameInfoList);
    }
}
