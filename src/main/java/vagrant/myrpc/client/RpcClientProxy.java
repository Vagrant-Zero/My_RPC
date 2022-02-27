package vagrant.myrpc.client;


import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.util.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 客户端代理对象
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
//    private String host;
//    private Integer port;

    private Client client;


    public RpcClientProxy(Client client) {
//        this.host = host;
//        this.port = port;
        this.client = client;
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
//        log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
//        // 封装请求
//        RpcRequest request = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
//                method.getName(), method.getParameterTypes(), args);
//        // 发送请求
//        return client.sendRequest(request);
//        RpcClient rpcClient = new RpcClient();
//        Object response = ((RpcResponse) rpcClient.sendRequest(request, host, port)).getData();
//        return response;
        log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), method.getParameterTypes(), args, false);
        RpcResponse rpcResponse = null;
        if (client instanceof NettyClient) { //只有一种实现
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                rpcResponse = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("方法调用请求发送失败", e);
                return null;
            }
        }
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
