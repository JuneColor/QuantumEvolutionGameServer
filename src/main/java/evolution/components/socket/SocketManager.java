package evolution.components.socket;

import com.google.inject.ImplementedBy;
import evolution.components.socket.impl.ClientSocketManager;

import java.net.Socket;
import java.util.Iterator;
import java.util.List;

@ImplementedBy(ClientSocketManager.class)
public interface SocketManager {
    boolean addSocket(String socketId, Socket socket);
    boolean socketIdExist(String socketId);
    boolean removeSocket(String socketId);
    boolean removeAllSocket();
    Socket getSocket(String socketId);
    int getManagedSocketCount();
    List<String> listAllSocketIds();
    Iterator<Socket> getIterator();
    void setKeepAlive(boolean enableHeartbeat);
    boolean setKeepHeartbeatTime(int heartbeatSeconds);
    boolean closeSocket(String socketId);
    boolean closeAllSocket();
}
