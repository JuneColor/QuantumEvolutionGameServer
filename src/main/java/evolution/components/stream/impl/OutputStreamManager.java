package evolution.components.stream.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import evolution.components.queue.MessageQueue;
import evolution.components.stream.StreamManager;
import evolution.components.logger.EvolutionLogger;
import evolution.util.Util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputStreamManager implements StreamManager {

    private volatile Map<String, OutputStream> streamMap = new HashMap<>();
    private Injector injector = Guice.createInjector();
    private EvolutionLogger logger;

    public OutputStreamManager() {
        this.logger = injector.getInstance(EvolutionLogger.class);
    }

    @Override
    public boolean addStream(String streamId, Object stream) {
        if (Util.isNullOrEmpty(streamId) || (null == stream)) {
            return false;
        }

        if (!(stream instanceof OutputStream)) {
            logger.error("Stream type not match OutputStream");
            return false;
        }

        logger.info("Add new output stream into OutputStreamManager id " + streamId);
        this.streamMap.put(streamId, (OutputStream) stream);
        return true;
    }

    @Override
    public int getAllStreamCount() {
        return this.streamMap.size();
    }

    @Override
    public boolean removeStream(String streamId) {
        if (Util.isNullOrEmpty(streamId) || !this.streamMap.containsKey(streamId)) {
            return false;
        }

        this.streamMap.remove(streamId);

        return true;
    }

    @Override
    public boolean removeAllStream() {
        logger.info("Remove all managed output stream");
        this.streamMap.clear();
        return true;
    }

    @Override
    public boolean closeStream(String streamId) {
        if (Util.isNullOrEmpty(streamId) || !this.streamMap.containsKey(streamId)) {
            return false;
        }

        try {
            this.streamMap.get(streamId).close();
            return true;
        } catch (Exception e) {
            logger.error("Error in close output stream for " + streamId);
            return false;
        }
    }

    @Override
    public boolean closeAllStream() {
        try {
            for (Map.Entry<String, OutputStream> entry : this.streamMap.entrySet()) {
                entry.getValue().close();
            }

            return true;
        } catch (Exception e) {
            logger.error("Close all output stream failed");
            return false;
        }
    }

    @Override
    public List<String> listAllStreamIds() {
        return new ArrayList<>(this.streamMap.keySet());
    }

    @Override
    public void processStream(MessageQueue messageQueue) {
        // TODO: need some stream processing hooks here

//        logger.info("Processing Output Stream");

        while (!messageQueue.isEmpty()) {
            String id = (String) messageQueue.popHead();
            OutputStream outputStream = this.streamMap.get(id);
            try {
                String outMsg = (System.currentTimeMillis() + " This is the respond message from server to " + id);

                logger.info("Sending info " + outMsg);
                outputStream.write(outMsg.getBytes());
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error happened when sending message to client, remove error stream " + id);
                this.streamMap.remove(id);

                try {
                    outputStream.close();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        }
    }
}
