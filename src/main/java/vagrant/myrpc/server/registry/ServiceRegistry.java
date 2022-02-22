package vagrant.myrpc.server.registry;

public interface ServiceRegistry {
    /**
     * 注册实现类，即：将实现类添加到注册表中
     * @param service
     * @param <T>
     */
    <T> void register(T service);

    /**
     * 根据服务名称获取服务实体
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);
}
