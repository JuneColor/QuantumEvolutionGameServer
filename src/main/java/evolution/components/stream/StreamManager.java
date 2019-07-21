package evolution.components.stream;

import evolution.components.queue.MessageQueue;

import java.util.List;

public interface StreamManager {
    boolean addStream(String streamId, Object stream);
    int getAllStreamCount();
    boolean removeStream(String streamId);
    boolean removeAllStream();
    boolean closeStream(String streamId);
    boolean closeAllStream();
    List<String> listAllStreamIds();

    abstract void processStream(MessageQueue messageQueue);
}
