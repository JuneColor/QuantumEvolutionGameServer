package evolution.components.lifetime;

import evolution.util.Util;

import java.util.HashMap;
import java.util.Map;

public class LifetimeManager {
    private volatile Map<String, InnerLifetimeObject> lifetimeMap = new HashMap<>();
    private String managerName;
    private long aliveTimeMilliseconds;

    class InnerLifetimeObject {
        private String objectId;
        private long lastUpdateTimestamp;
        private long expireTimestamp;

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public long getLastUpdateTimestamp() {
            return lastUpdateTimestamp;
        }

        public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
            this.lastUpdateTimestamp = lastUpdateTimestamp;
        }

        public long getExpireTimestamp() {
            return expireTimestamp;
        }

        public void setExpireTimestamp(long expireTimestamp) {
            this.expireTimestamp = expireTimestamp;
        }
    }

    public LifetimeManager() {
        this.managerName = "UNNAMED_LIFETIME_MANAGER_NAME";
        this.aliveTimeMilliseconds = 10 * 1000;
    }

    public LifetimeManager(String name) {
        this.managerName = name;
        this.aliveTimeMilliseconds = 10 * 1000;
    }

    public void setManagerName(String name) {
        this.managerName = name;
    }

    public String getManagerName() {
        return this.managerName;
    }

    /**
     * check if id is alive within it's lifetime
     * Note: not exist item will return true
     * @param objectId
     * @return true - expired or not exist, false - if still available
     */
    public boolean isExpired(String objectId) {
        if (!lifetimeMap.containsKey(objectId)) {
            return true;
        }

        if (lifetimeMap.get(objectId).getExpireTimestamp() < System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    /**
     * get expire time of id (milliseconds)
     * return -1 if object not exist
     * @param objectId
     * @return long value >= 0; -1 when not exist
     */
    public long getExpireTimestamp(String objectId) {
        if (!lifetimeMap.containsKey(objectId)) {
            return -1;
        }

        return lifetimeMap.get(objectId).getExpireTimestamp();
    }

    /**
     * get last visit timestamp of id (milliseconds)
     * return -1 if object not exist
     * @param objectId
     * @return long value >= 0; -1 when not exist
     */
    public long getLastVisitTimestamp(String objectId) {
        if (!lifetimeMap.containsKey(objectId)) {
            return -1;
        }

        return lifetimeMap.get(objectId).getLastUpdateTimestamp();
    }

    public boolean addKey(String objectId) {
        if (Util.isNullOrEmpty(objectId)) {
            return false;
        }

        InnerLifetimeObject lifetimeObject = new InnerLifetimeObject();
        lifetimeObject.setObjectId(objectId);
        long nowTimestamp = System.currentTimeMillis();
        lifetimeObject.setLastUpdateTimestamp(nowTimestamp);
        lifetimeObject.setExpireTimestamp(nowTimestamp + this.aliveTimeMilliseconds);
        this.lifetimeMap.put(objectId, lifetimeObject);
        return true;
    }

    /**
     * refresh visit timestamp of key
     * @param objectId
     * @return true - success; false - key not exist or failed or empty key id
     */
    public boolean refreshKey(String objectId) {
        if (Util.isNullOrEmpty(objectId) || !this.lifetimeMap.containsKey(objectId)) {
            return false;
        }

        InnerLifetimeObject lifetimeObject = this.lifetimeMap.get(objectId);
        long nowTimestamp = System.currentTimeMillis();
        lifetimeObject.setLastUpdateTimestamp(nowTimestamp);
        lifetimeObject.setExpireTimestamp(nowTimestamp + this.aliveTimeMilliseconds);
        return true;
    }

    /**
     * refresh all keys managed
     * Note: this action may involved with performance problem, use it carefully
     * @return
     */
    public boolean refreshAllKeys() {
        for (String key : this.lifetimeMap.keySet()) {
            this.refreshKey(key);
        }

        return true;
    }

    /**
     * remove key from managed map
     * @param objectId
     * @return true - success; false - empty key id or not exist
     */
    public boolean removeKey(String objectId) {
        if (Util.isNotNullOrEmpty(objectId) || !this.lifetimeMap.containsKey(objectId)) {
            return false;
        }

        this.lifetimeMap.remove(objectId);
        return true;
    }

    /**
     * remove all item managed
     * @return true
     */
    public boolean removeAllKey() {
        this.lifetimeMap.clear();
        return true;
    }

    /**
     * set lifetime for all elements in this manager
     * @param lifetimeMilliseconds
     * @return true - success; false - given value <= 0
     */
    public boolean setKeyLifetimeMilliseconds(long lifetimeMilliseconds) {
        if (0 >= lifetimeMilliseconds) {
            return false;
        }

        this.aliveTimeMilliseconds = lifetimeMilliseconds;
        return true;
    }

    public long getKeyLifetimeMilliseconds() {
        return this.aliveTimeMilliseconds;
    }
}
