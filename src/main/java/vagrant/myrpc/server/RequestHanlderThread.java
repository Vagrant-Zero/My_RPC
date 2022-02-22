package vagrant.myrpc.server;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.server.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@Slf4j
public class RequestHanlderThread implements Runnable{
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequestHanlderThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ) {
            RpcRequest request = (RpcRequest)ois.readObject();
            String interfaceName = request.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object returnValue = requestHandler.handle(request, service); // 调用handleer去处理任务
            oos.writeObject(RpcResponse.success(returnValue));
            oos.flush();
        } catch (IOException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用或发送时有错误发生！");
            e.printStackTrace();
        }
    }
}
