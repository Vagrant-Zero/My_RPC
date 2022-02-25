package vagrant.myrpc.version2;

import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.client.Client;
import vagrant.myrpc.client.NettyClient;
import vagrant.myrpc.client.RpcClientProxy;
import vagrant.myrpc.serializer.KryoSerializer;

public class TestNettyClient {
    public static void main(String[] args) {
        // 4.0
//        Client client = new NettyClient("localhost", 9000);
//        RpcClientProxy proxy = new RpcClientProxy(client);
//        HelloService helloService = proxy.getProxy(HelloService.class);
//        HelloObject object = new HelloObject(12, "This is a message");
//        String res = helloService.sayHello(object);
//        System.out.println(res);

        // 5.0
        Client client = new NettyClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.sayHello(object);
        System.out.println(res);

    }
}
