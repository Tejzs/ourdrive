package storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataStorage {
    private static AbstractStorage fileUploadMetaStore = new InMemoryStore();
    private static AbstractStorage fileOperationMetaStore = new InMemoryStore();

    public static AbstractStorage getFileUploadMetaStore() {
        return fileUploadMetaStore;
    }

    public static AbstractStorage getFileOperationsMetaStore() {
        return fileOperationMetaStore;
    }
}