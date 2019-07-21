package evolution.server.controller;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpExchange;
import evolution.components.logger.EvolutionLogger;
import evolution.server.handler.RequestHandler;
import evolution.server.http.HttpResponse;
import evolution.util.Util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RequestController {
    private Injector injector = Guice.createInjector();
    private EvolutionLogger logger = injector.getInstance(EvolutionLogger.class);
    private volatile Map<String, Method> routeMappingGET = new HashMap<>();
    private volatile Map<String, Method> routeMappingPOST = new HashMap<>();
    private RequestHandler requestHandler = injector.getInstance(RequestHandler.class);


    final static String GET_REQUEST = "GET";
    final static String POST_REQUEST = "POST";

    private class InnerRouterObject {
        String method;
        String routePath;
        String handlerMethod;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getRoutePath() {
            return routePath;
        }

        public void setRoutePath(String routePath) {
            this.routePath = routePath;
        }

        public String getHandlerMethod() {
            return handlerMethod;
        }

        public void setHandlerMethod(String handlerMethod) {
            this.handlerMethod = handlerMethod;
        }
    }

    public RequestController() throws IOException, NoSuchMethodException {
        logger.info("Initializing request controller");

        final String ROUTE_FILE_PATH = "src/main/java/conf/";
        File routeFile = new File(ROUTE_FILE_PATH + "evolution.routes");
        if(routeFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(routeFile));
            String line = reader.readLine();
            while (null != line) {
                this.updateRouteMapping(this.parseRouterLine(line));
                line = reader.readLine();
            }
            reader.close();
            logger.info("Router init done");
        } else {
            logger.warn("No evolution.routes file found, init failed");
            throw new FileNotFoundException("No evolution.routes file found, init failed");
        }
    }

    public void proceedRequest(HttpExchange exchange)  {
        Method method = this.getProceedMethod(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        if (null != method) {
            logger.info("Process method " + method.toString());

            try {
                method.invoke(requestHandler, exchange);
            } catch (Exception e) {
                logger.error("Request method failed when proceed request in controller");
            }
        } else {
            logger.info("Wrong request location, return bad request");
            HttpResponse response = injector.getInstance(HttpResponse.class);
            response.BadRequest(exchange);
        }
    }

    private Method getProceedMethod(String requestMethod, String path) {
        if (Util.isNullOrEmpty(requestMethod) || Util.isNullOrEmpty(path)) {
            logger.warn("Get empty request url, will not proceed");
            return null;
        }

        if (GET_REQUEST.equalsIgnoreCase(requestMethod)) {
            return routeMappingGET.get(path.toUpperCase());
        } else if (POST_REQUEST.equalsIgnoreCase(requestMethod)) {
            return routeMappingPOST.get(path);
        } else {
            logger.warn("Unsupported request method " + requestMethod);
            return null;
        }
    }

    private void updateRouteMapping(InnerRouterObject routerObject) throws NoSuchMethodException {
        if (null == routerObject) {
            return;
        }

        if (GET_REQUEST.equalsIgnoreCase(routerObject.getMethod())) {
            routeMappingGET.put(
                    routerObject.getRoutePath().toUpperCase(),
                    RequestHandler.class.getMethod(
                            routerObject.getHandlerMethod(),
                            HttpExchange.class));
        } else if (POST_REQUEST.equalsIgnoreCase(routerObject.getMethod())) {
            routeMappingPOST.put(
                    routerObject.getRoutePath().toUpperCase(),
                    RequestHandler.class.getMethod(
                            routerObject.getHandlerMethod(),
                            HttpExchange.class));
        } else {
            logger.warn("Unknown evolution.routes method found " + routerObject.getMethod());
        }
    }

    private InnerRouterObject parseRouterLine(String inputLine) {
        if (Util.isNullOrEmpty(inputLine) || inputLine.startsWith("#")) {
            return null;
        }

        String [] inputConditions = inputLine.split("((\\ )|(\\t))+");
        if(3 > inputConditions.length) {
            logger.warn("Routes file found not matched line " + inputLine);
            return null;
        } else if (3 < inputConditions.length) {
            logger.warn("More than 3 params found in request body, will use previous 3 only");
        }

        InnerRouterObject routerObject = new InnerRouterObject();
        routerObject.setMethod(inputConditions[0]);
        routerObject.setRoutePath(inputConditions[1]);
        routerObject.setHandlerMethod(inputConditions[2]);

        return routerObject;
    }
}
