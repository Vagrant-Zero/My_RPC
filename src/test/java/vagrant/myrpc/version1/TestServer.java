package vagrant.myrpc.version1;

import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();
        RpcServer server = new RpcServer();
        server.register(service, 9000);
    }
}
