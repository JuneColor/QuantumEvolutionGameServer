package evolution.service;

public interface GameService {
    void createGameService(String name, int port);
    String getServiceId();
    String getServiceName();
    String getServiceHostAddress();
    int getServicePort();
    void shutdownService();
}
