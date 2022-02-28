package vagrant.myrpc.server;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.annotation.Service;
import vagrant.myrpc.annotation.ServiceScan;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.register.ServiceRegistry;
import vagrant.myrpc.server.provider.ServiceProvider;
import vagrant.myrpc.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public abstract class AbstractRpcServer implements Server{

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 扫描服务包
     */
    public void scanService() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;

        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                log.error("启动类缺少 @ServiceScan 注解！");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误！");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value(); // 获取启动类所在的基础包所标注的注解值
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                String name = clazz.getAnnotation(Service.class).name();
                if("".equals(name)) {
                    for (Class<?> oneInterface : clazz.getInterfaces()) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                }
            }

        }


    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
