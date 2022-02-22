package vagrant.myrpc.server;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.ResponseCode;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Slf4j
public class RequestHandler {

    /**
     * 反射执行真正的方法 有异常一直向上抛出，到最顶层才处理
     * @param rpcRequest
     * @param service
     * @return
     */
    public Object handle(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Object returnValue = invokeTargetMethod(rpcRequest, service);
        log.debug("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        return returnValue;
    }

    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            return method.invoke(service, rpcRequest.getParameters());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
        }
    }
}
