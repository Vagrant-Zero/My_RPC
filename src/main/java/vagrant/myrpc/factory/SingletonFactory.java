package vagrant.myrpc.factory;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 */
@Slf4j
public class SingletonFactory {

    private static Map<Class, Object> objectMap = new HashMap<>();

    public SingletonFactory() {

    }

    public static <T> T getInstance(Class<T> clazz) {
        Object obj = objectMap.get(clazz);
        if(obj == null) {
            synchronized (SingletonFactory.class) {
                if(obj == null) {
                    try {
                        obj = clazz.newInstance();
                        objectMap.put(clazz, obj);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
        return clazz.cast(obj);
    }


}
