package vagrant.myrpc.version3;

import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.client.Client;
import vagrant.myrpc.client.NettyClient;
import vagrant.myrpc.client.RpcClientProxy;
import vagrant.myrpc.serializer.CommonSerializer;

public class TestNettyClient {
    public static void main(String[] args) {
        Client client = new NettyClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        String res = helloService.sayHello(object);
        System.out.println(res);
    }
}
