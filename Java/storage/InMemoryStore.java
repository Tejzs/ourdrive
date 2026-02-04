package storage;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryStore implements AbstractStorage {
    private Map<Object, Serializable> storageMap = new LinkedHashMap<>();

    @Override
    public void offer(Object key, Serializable value) {
        storageMap.put(key, value);
    }

    @Override
    public Serializable get(Object key) {
        return storageMap.getOrDefault(key, null);
    }

    @Override
    public Set<Entry<Object, Serializable>> getEntrySet() {
        return storageMap.entrySet();
    }

    @Override
    public void delete(Object key) {
        storageMap.put(key, null);
    }

	@Override
	public Collection<Serializable> values() {
        return storageMap.values();
	}
}