package vagrant.myrpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 提供Nacos的连接、服务注册、获取服务和清除注册（在服务端才有这个工具类）
 */
@Slf4j
public class NacosUtil {

    private static final NamingService namingService;
    private static Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address; // 在服务端才有这个工具类，因此此时的socket就是服务端的socket，并不是存储的全局的socket


    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNamingService();
    }

    public static NamingService getNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接Nacos注册中心失败！错误为：{}", e.getMessage());
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void register(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        NacosUtil.address = inetSocketAddress;
    }

    public static List<Instance> getInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry() {
        if(!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port); // 注销当前服务端上的所有服务
                } catch (NacosException e) {
                    log.error("注销服务{}失败：", serviceName, e);
                    e.printStackTrace();
                }
            }
        }
    }


}
