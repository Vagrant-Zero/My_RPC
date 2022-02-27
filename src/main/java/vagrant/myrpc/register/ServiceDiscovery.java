package vagrant.myrpc.register;

import java.net.InetSocketAddress;

/**
 * 提供给客户端，用于服务的发现
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名查找提供服务的地址
     * @param serviceName 服务名称
     * @return 提供服务的地址
     */
    InetSocketAddress lookupService(String serviceName);
}
