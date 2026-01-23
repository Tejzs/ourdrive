package utility;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalHandler {

    private static final ThreadLocal<ThreadLocalDetails> TL = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> PROPERTIES_TL = new ThreadLocal<>();

    public static void setDetails(ThreadLocalDetails tcld) {
        TL.set(tcld);
    }

    public static ThreadLocalDetails getDetails() {
        return TL.get();
    }

    public static void removeThreadLocal() {
        TL.remove();
    }

    public static Object setTLProperty(String key, Object value) {
        Map<String, Object> properties = PROPERTIES_TL.get();
        if (value != null) {
            if (properties == null) {
                properties = new HashMap<>();
                PROPERTIES_TL.set(properties);
            }
            return properties.put(key, value);
        } else if (properties != null) {
            return properties.remove(key);
        }
        return null;
    }

    public static Object getTLProperty(String key) {
        Map<String, Object> properties = PROPERTIES_TL.get();
        if (properties == null) {
            return null;
        }
        return properties.get(key);
    }

    public static interface ThreadLocalDetails {
    }
}