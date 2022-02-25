package vagrant.myrpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos服务注册中心
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

    /**
     * 初始化时连接nacos的服务器
     */
    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接Nacos注册中心失败！错误为：{}", e.getMessage());
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("注册服务到Nacos失败，错误为：{}", e.getMessage());
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0); // 这一版本始终选择第一个服务器，没有进行负载均衡的配置
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生：{}", e);
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}
