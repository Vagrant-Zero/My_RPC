package vagrant.myrpc.server;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@Slf4j
public class WorkerThread implements Runnable{
    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ) {
            RpcRequest request = (RpcRequest)ois.readObject();
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Object returnValue = method.invoke(service, request.getParameters());
            oos.writeObject(RpcResponse.success(returnValue, request.getRequestId()));
            oos.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("调用时有错误发生！");
            e.printStackTrace();
        }
    }
}
