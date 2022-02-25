package vagrant.myrpc.server.provider;

/**
 * 保存和提供服务实现类
 */
public interface ServiceProvider {
    /**
     * 注册实现类，即：将实现类添加到注册表中
     * @param service
     * @param <T>
     */
    <T> void addServiceProvider(T service);

    /**
     * 根据服务名称获取服务实体
     * @param serviceName
     * @return
     */
    Object getServiceProvider(String serviceName);
}
