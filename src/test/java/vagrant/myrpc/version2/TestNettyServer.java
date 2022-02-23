package vagrant.myrpc.version2;

import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.server.NettyServer;
import vagrant.myrpc.server.registry.DefaultServiceRegistry;
import vagrant.myrpc.server.registry.ServiceRegistry;

public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9000);
    }
}
