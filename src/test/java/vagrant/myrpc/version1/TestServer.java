package vagrant.myrpc.version1;

import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.server.RpcServer;
import vagrant.myrpc.server.registry.DefaultServiceRegistry;
import vagrant.myrpc.server.registry.ServiceRegistry;

public class TestServer {
    public static void main(String[] args) {
        /**
         * 1.0
         */
//        HelloService service = new HelloServiceImpl();
//        RpcServer server = new RpcServer();
//        server.register(service, 9000);

        /**
         * 2.0
         */
        HelloService service = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(service);
        RpcServer server = new RpcServer(serviceRegistry);
        server.start(9000);
    }
}
