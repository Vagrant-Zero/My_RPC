package vagrant.myrpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.loadbalancer.LoadBalancer;
import vagrant.myrpc.loadbalancer.RoundRobinLoadBalancer;
import vagrant.myrpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{

    private LoadBalancer loadBalancer; // 负载均衡策略

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer == null) {
            this.loadBalancer = new RoundRobinLoadBalancer();
        }else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getInstance(serviceName);
            Instance instance = loadBalancer.select(instances); // 负载均衡选择
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生：{}", e);
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}
