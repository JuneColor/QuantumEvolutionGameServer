package evolution.components.flowid;

import evolution.util.Util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlowIdManager {
    private String name;
    private volatile Map<String, Long> idMap;
    private final long MAX_FLOW_ID = 0xFFFFFFL;
    private final long MIN_FLOW_ID = 0x000001L;

    public FlowIdManager(String name) {
        this.name = name;
        this.idMap = new ConcurrentHashMap<>();
    }

    public FlowIdManager() {
        this.name = "UNNAMED_FLOW_ID_MANAGER";
        this.idMap = new ConcurrentHashMap<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean addKey(String key) {
        if (Util.isNullOrEmpty(key) || this.idMap.containsKey(key)) {
            return false;
        }

        this.idMap.put(key, MIN_FLOW_ID);
        return true;
    }

    public boolean removeKey(String key) {
        if (Util.isNullOrEmpty(key) || !this.idMap.containsKey(key)) {
            return false;
        }

        this.idMap.remove(key);
        return true;
    }

    public void removeAllKey() {
        this.idMap.clear();
    }

    public Long getFlowId(String key) {
        if (Util.isNullOrEmpty(key) || !this.idMap.containsKey(key)) {
            return null;
        }

        return this.idMap.get(key);
    }

    public Long nextFlowId(String key) {
        if (Util.isNullOrEmpty(key) || !this.idMap.containsKey(key)) {
            return null;
        }

        Long value = this.idMap.get(key);
        ++value;
        if (value > this.MAX_FLOW_ID) {
            value = MIN_FLOW_ID;
        }
        this.idMap.replace(key, value);

        return value;
    }

    public boolean isCorrectFlowId(String key, Long id) {
        return (this.idMap.get(key).equals(id));
    }
}
