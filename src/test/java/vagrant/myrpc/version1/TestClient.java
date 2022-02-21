package vagrant.myrpc.version1;

import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;
import vagrant.myrpc.client.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12, "This is a test message");
        String res = helloService.sayHello(helloObject);
        System.out.println(res);
    }
}
