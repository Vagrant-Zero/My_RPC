package vagrant.myrpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮盘策略：这个实现是在注册中心的，因此认为没有线程安全的问题
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    private int index = 0;


    @Override
    public Instance select(List<Instance> instances) {
        if(index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
