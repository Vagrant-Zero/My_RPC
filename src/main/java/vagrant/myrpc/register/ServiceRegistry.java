package vagrant.myrpc.register;

import java.net.InetSocketAddress;

/**
 * 提供给服务端，用于服务的注册
 */
public interface ServiceRegistry {

    /**
     * 将服务注册到注册中心的注册表中
     * @param serviceName 需要注册的服务名
     * @param inetSocketAddress 提供服务的服务端Socket
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);




}
