package vagrant.myrpc.version1;

import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.server.RpcServer;
import vagrant.myrpc.server.provider.ServiceProviderImpl;
import vagrant.myrpc.server.provider.ServiceProvider;

public class TestServer {
    public static void main(String[] args) {
        /**
         * 1.0
         */
//        HelloService service = new HelloServiceImpl();
//        RpcServer server = new RpcServer();
//        server.addServiceProvider(service, 9000);

        /**
         * 2.0
         */
        HelloService service = new HelloServiceImpl();
        ServiceProvider serviceProvider = new ServiceProviderImpl();
        serviceProvider.addServiceProvider(service);
        RpcServer server = new RpcServer(serviceProvider);
        server.start(9000);
    }
}
