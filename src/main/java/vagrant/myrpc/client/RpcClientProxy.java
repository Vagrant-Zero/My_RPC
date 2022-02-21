package vagrant.myrpc.client;


import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端代理对象
 */
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private Integer port;


    public RpcClientProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    /**
     * 调用方法走这个逻辑
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 封装请求
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        // 发送请求
        RpcClient rpcClient = new RpcClient();
        Object response = ((RpcResponse) rpcClient.sendRequest(request, host, port)).getData();
        return response;
    }
}
