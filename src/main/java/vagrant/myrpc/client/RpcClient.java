package vagrant.myrpc.client;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

@Slf4j
public class RpcClient {

    public Object sendRequest(RpcRequest request, String host, Integer port) {
        try(Socket socket = new Socket(host, port)) {
            OutputStream ops = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(ops);
            oos.writeObject(request);
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用时有错误发生：", e);
            e.printStackTrace();
            return null;
        }
    }
}
