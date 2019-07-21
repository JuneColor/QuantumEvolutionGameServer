package evolution.components.stream.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import evolution.components.queue.MessageQueue;
import evolution.components.logger.EvolutionLogger;
import evolution.components.stream.StreamManager;
import evolution.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputStreamManager implements StreamManager {
    private volatile Map<String, InputStream> streamMap = new HashMap<>();
    private Injector injector = Guice.createInjector();
    private EvolutionLogger logger;

    public InputStreamManager() {
        this.logger = injector.getInstance(EvolutionLogger.class);
    }

    @Override
    public boolean addStream(String streamId, Object stream) {
        if (Util.isNullOrEmpty(streamId) || (null == stream)) {
            return false;
        }

        if (!(stream instanceof InputStream)) {
            logger.error("Stream type not match InputStream");
            return false;
        }

        logger.info("Add new input stream into InputStreamManager id " + streamId);
        this.streamMap.put(streamId, (InputStream) stream);
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
        logger.info("Remove all managed input stream");
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
            logger.error("Error in close input stream for " + streamId);
            return false;
        }
    }

    @Override
    public boolean closeAllStream() {
        try {
            for (Map.Entry<String, InputStream> entry : this.streamMap.entrySet()) {
                entry.getValue().close();
            }

            return true;
        } catch (Exception e) {
            logger.error("Close all input stream failed");
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

//        logger.info("Processing Input Stream part");

        for (Map.Entry<String, InputStream> entry : this.streamMap.entrySet()) {
            try {
                String messageBody = this.readInputStream(entry.getValue());
                if (Util.isNotNullOrEmpty(messageBody)) {
                    logger.info("Received message from " + entry.getKey() + " : " + messageBody);

                    // Put temp id into queue
                    messageQueue.pushTail(entry.getKey());
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error when receive message from clients, remove error stream " + entry.getKey());

                try {
                    entry.getValue().close();
                } catch (Exception err) {
                    err.printStackTrace();
                }

                this.streamMap.remove(entry.getKey());
            }
        }
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        byte [] inputBuf = new byte[inputStream.available()];
        int cnt = inputStream.read(inputBuf);
        if (0 >= cnt) {
            return null;
        }

        return new String(inputBuf);
    }
}
