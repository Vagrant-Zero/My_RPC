package vagrant.myrpc.server;

import vagrant.myrpc.serializer.CommonSerializer;

public interface Server {
    /**
     * 启动服务器
     */
    void start();

    /**
     * 向Nacos注册服务
     * @param service
     * @param serviceClass
     * @param <T>
     */
    <T> void publishService(Object service, Class<T> serviceClass);

    void setSerializer(CommonSerializer serializer);
}
