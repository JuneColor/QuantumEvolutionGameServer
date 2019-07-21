package evolution.components.socket.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import evolution.components.logger.EvolutionLogger;
import evolution.components.socket.SocketManager;
import evolution.util.Util;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSocketManager implements SocketManager {
    private Injector injector = Guice.createInjector();
    private EvolutionLogger logger = injector.getInstance(EvolutionLogger.class);
    private volatile Map<String, Socket> socketMap;
    private volatile boolean enableHeartbeat;
    private volatile int keepAliveHeartBeatTime;

    public ClientSocketManager() {
        this.socketMap = new ConcurrentHashMap<>();
        enableHeartbeat = false;
        keepAliveHeartBeatTime = 0;

        logger.info("ClientSocketManager is initializing");
    }

    @Override
    public boolean addSocket(String socketId, Socket socket) {
        if (Util.isNotNullOrEmpty(socketId) && (null != socket)) {
            if (this.socketMap.containsKey(socketId)) {
                logger.warn("socket id " + socketId + " existed, will not add it to socket manager");
            } else {
                this.socketMap.put(socketId, socket);
            }
        }

        return false;
    }

    @Override
    public boolean socketIdExist(String socketId) {
        return this.socketMap.containsKey(socketId);
    }

    /**
     * Note: this function will not close socket, just remove it from manager only
     * @param socketId
     * @return true
     */
    @Override
    public boolean removeSocket(String socketId) {
        if (this.socketMap.containsKey(socketId)) {
            this.socketMap.remove(socketId);
        }

        return true;
    }

    /**
     * Note: this function will not close all socket that removed form map
     *        you should call close all first if you need disconnect all client
     * @return true
     */
    @Override
    public boolean removeAllSocket() {
        logger.info("Remove all socket items in socket manager");
        this.socketMap.clear();
        return true;
    }


    @Override
    public Socket getSocket(String socketId) {
        return this.socketMap.get(socketId);
    }

    @Override
    public int getManagedSocketCount() {
        return this.socketMap.size();
    }

    @Override
    public List<String> listAllSocketIds() {
        return new LinkedList<>(this.socketMap.keySet());
    }

    @Override
    public Iterator<Socket> getIterator() {
        return this.socketMap.values().iterator();
    }

    @Override
    public void setKeepAlive(boolean enableHeartbeat) {
        this.enableHeartbeat = enableHeartbeat;
    }

    @Override
    public boolean setKeepHeartbeatTime(int heartbeatSeconds) {
        if (0 <= heartbeatSeconds) {
            this.keepAliveHeartBeatTime = heartbeatSeconds;
            logger.info("Set heartbeat seconds to " + heartbeatSeconds);
            return true;
        }

        if (!enableHeartbeat) {
            logger.warn("Heartbeat is not enabled, set time have no effect");
        }

        return false;
    }

    @Override
    public boolean closeSocket(String socketId) {
        try {
            if (this.socketMap.containsKey(socketId)) {
                Socket socket = this.socketMap.get(socketId);
                socket.close();
                logger.info("Socket of " + socketId + " is closed");
                return true;
            }
        } catch (IOException e) {
            logger.warn("Close socket for " + socketId + " failed");
        }

        return false;
    }

    @Override
    public boolean closeAllSocket() {
        try {
            logger.info("Closing all sockets");

            for (Map.Entry<String, Socket> entry : this.socketMap.entrySet()) {
                entry.getValue().close();
                logger.info("Socket id " + entry.getValue() + " is closed");
            }

            return true;
        } catch (IOException e) {
            logger.warn("Close all socket failed");
        }

        return false;
    }
}
