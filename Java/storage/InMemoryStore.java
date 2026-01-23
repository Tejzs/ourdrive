package storage;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryStore {
    private Map<Object, Serializable> storageMap = new HashMap();

    public void offer(Object key, Serializable value) {
        storageMap.put(key, value);
    }

    public Serializable get(Object key) {
        return storageMap.getOrDefault(key, null);
    }

    public Set<Entry<Object, Serializable>> getEntrySet() {
        return storageMap.entrySet();
    }

    public void delete(Object key) {
        storageMap.put(key, null);
    }
}