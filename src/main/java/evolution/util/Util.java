package evolution.util;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class Util {
    private static Gson gson = new Gson();

    public static boolean isNullOrEmpty(String message) {
        if ((null == message) || ("".equals(message))) {
            return true;
        }

        return false;
    }

    public static boolean isNotNullOrEmpty(String message) {
        if ((null == message) || ("".equals(message))) {
            return false;
        }

        return true;
    }

    public static <T> T parseJsonObjectFromStream(InputStream stream, Type type) {
        return gson.fromJson(new InputStreamReader(stream), type);
    }

    public static <T> T parseJsonObjectFromStream(HttpExchange exchange, Type type) {
        return gson.fromJson(new InputStreamReader(exchange.getRequestBody()), type);
    }

    public static String toJsonString(Object in) {
        return gson.toJson(in);
    }
}
