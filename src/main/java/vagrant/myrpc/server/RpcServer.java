package vagrant.myrpc.server;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.server.provider.ServiceProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class RpcServer {
    private final int CORE_POOlSIZE = 5;
    private final int MAXIMUMPOOLSIZE = 50;
    private final long KEEPALIVETIME = 60;
    private final int BLOCKING_QUEUE_CAPACITY = 100;
    private ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceProvider serviceProvider;

    public RpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOlSIZE, MAXIMUMPOOLSIZE, KEEPALIVETIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            log.debug("服务器启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                log.debug("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHanlderThread(socket, requestHandler, serviceProvider));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生！");
            e.printStackTrace();
        }
    }
}
