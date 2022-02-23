package vagrant.myrpc.server.registry;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultServiceRegistry implements ServiceRegistry{
    private static Set<String> registeredService = new HashSet<>(); // 修改为全局
    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>(); // 修改为全局

    @Override
    public synchronized <T> void register(@NonNull T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) {
            return; // 重复添加不会报错，此处实现为直接返回
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service); // 在注册服务时，默认采用这个对象实现的接口的完整类名作为服务名
        }
        log.debug("向接口: {} 注册服务: {}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
