package vagrant.myrpc.server;

import vagrant.myrpc.serializer.CommonSerializer;

public interface Server {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    /**
     * 启动服务器
     */
    void start();

    /**
     * 向Nacos注册服务
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void publishService(Object service, String serviceName);

//    void setSerializer(CommonSerializer serializer);
}
