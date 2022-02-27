package vagrant.myrpc.version2;

import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.serializer.KryoSerializer;
import vagrant.myrpc.server.NettyServer;
import vagrant.myrpc.server.provider.ServiceProviderImpl;
import vagrant.myrpc.server.provider.ServiceProvider;

public class TestNettyServer {
    public static void main(String[] args) {
        // 4.0
//        HelloService helloService = new HelloServiceImpl();
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
//        serviceProvider.addServiceProvider(helloService);
//        NettyServer server = new NettyServer();
//        server.start(9000);
        // 5. 0
//        HelloService helloService = new HelloServiceImpl();
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
//        NettyServer server = new NettyServer("127.0.0.1", 9999);
//        server.setSerializer(new KryoSerializer());
//        server.publishService(helloService, HelloService.class);
//        // 这个版本我的实现是需要单独启动的，便于可以注册多个服务
//        server.start();

    }
}
