package simperf.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Simperfȫ��������Ϣ
 * @author imbugs
 */
public class SimperfConfig {
    /**
     * Ϊ�˼�С����Ҫ��Ч����ʧ�����config����
     */
    private static boolean             useConfig  = false;
    public static final String         JTL_RESULT = "JTL.RESULT";
    private static Map<String, Object> attributes = new HashMap<String, Object>();

    public static boolean hasConfig(String key) {
        if (key == null) {
            return false;
        }
        return attributes.containsKey(key);
    }

    public static Object getConfig(String key) {
        if (hasConfig(key)) {
            return attributes.get(key);
        }
        return null;
    }

    public static void setConfig(String key, Object obj) {
        attributes.put(key, obj);
        setUseConfig(true);
    }

    public static boolean isUseConfig() {
        return useConfig;
    }

    public static void setUseConfig(boolean useConfig) {
        SimperfConfig.useConfig = useConfig;
    }
}
