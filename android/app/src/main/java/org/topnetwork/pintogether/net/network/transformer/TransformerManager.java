package org.topnetwork.pintogether.net.network.transformer;
import java.util.HashMap;
import java.util.Map;

public class TransformerManager {
    private Map<String, ITransformerManager> hashMap = new HashMap<>();

    public Map<String, ITransformerManager> getHashMap() {
        return hashMap;
    }

    private static volatile TransformerManager instance = new TransformerManager();

    public void setTransformerManager(String key, ITransformerManager transformerManager) {
        if (!hashMap.containsKey(key)) {
            hashMap.put(key, transformerManager);
        }
    }

    public static synchronized TransformerManager getInstance() {
        return instance;
    }


}
