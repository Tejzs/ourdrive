package storage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryStoreHandler {
    private static InMemoryStore fileUploadMetaStore = new InMemoryStore();

    public static InMemoryStore getFileUploadMetaStore() {
        return fileUploadMetaStore;
    }
}