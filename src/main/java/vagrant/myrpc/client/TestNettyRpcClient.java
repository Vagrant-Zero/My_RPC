package vagrant.myrpc.client;

import vagrant.myrpc.api.ByeService;
import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.serializer.CommonSerializer;

public class TestNettyRpcClient {
    public static void main(String[] args) throws InterruptedException {
        Client client = new NettyClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        String res = helloService.sayHello(object);
        System.out.println(res);

        Thread.sleep(1000);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        String result = byeService.bye("zhangsan");
        System.out.println(result);

    }
}
