package vagrant.myrpc.version3;

import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.api.HelloServiceImpl;
import vagrant.myrpc.serializer.CommonSerializer;
import vagrant.myrpc.server.NettyServer;

public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9000, CommonSerializer.KRYO_SERIALIZER);
        server.publishService(helloService, HelloService.class);
        server.start();
    }


}
