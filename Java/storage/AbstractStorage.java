package storage;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public interface AbstractStorage {
    void offer(Object key, Serializable value);

    Serializable get(Object key);

	Set<Entry<Object, Serializable>> getEntrySet();

	Collection<Serializable> values();
    
    void delete(Object key);
}