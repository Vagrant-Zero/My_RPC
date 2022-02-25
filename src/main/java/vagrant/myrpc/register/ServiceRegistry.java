package vagrant.myrpc.register;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * 将服务注册到注册中心的注册表中
     * @param serviceName 需要注册的服务名
     * @param inetSocketAddress 提供服务的服务端Socket
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名查找提供服务的地址
     * @param serviceName 服务名称
     * @return 提供服务的地址
     */
    InetSocketAddress lookupService(String serviceName);


}
