package evolution.server.http;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public class HttpResponse {
    private static Gson gson = new Gson();

    private class ResponseMessage {
        private int errorCode;
        private Object messageBody;
        public ResponseMessage(int errorCode, Object response) {
            this.errorCode = errorCode;
            this.messageBody = response;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public Object getMessageBody() {
            return messageBody;
        }

        public void setMessageBody(Object messageBody) {
            this.messageBody = messageBody;
        }
    }

    public void OK(HttpExchange exchange, Object response) {
        try {
            byte[] ret = gson.toJson(new ResponseMessage(200, response)).getBytes(Charsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, ret.length);
            exchange.getResponseBody().write(ret);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            throw new RuntimeException("Response OK state failed");
        }
    }

    public void BadRequest(HttpExchange exchange) {
        try {
            byte[] ret = gson.toJson(new ResponseMessage(400, "Bad Request")).getBytes(Charsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, ret.length);
            exchange.getResponseBody().write(ret);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            throw new RuntimeException("Response Bad Request state failed");
        }
    }

    public void Forbidden(HttpExchange exchange) {
        try {
            byte[] ret = gson.toJson(new ResponseMessage(403, "Forbidden")).getBytes(Charsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(403, ret.length);
            exchange.getResponseBody().write(ret);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            throw new RuntimeException("Response Forbidden state failed");
        }
    }

    public void InternalError(HttpExchange exchange) {
        try {
            byte[] ret = gson.toJson(new ResponseMessage(500, "Internal Error")).getBytes(Charsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, ret.length);
            exchange.getResponseBody().write(ret);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            throw new RuntimeException("Response Internal Error state failed");
        }
    }

    public void NotFound(HttpExchange exchange) {
        try {
            byte[] ret = gson.toJson(new ResponseMessage(404, "Not Found")).getBytes(Charsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, ret.length);
            exchange.getResponseBody().write(ret);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            throw new RuntimeException("Response Not Found state failed");
        }
    }
}
