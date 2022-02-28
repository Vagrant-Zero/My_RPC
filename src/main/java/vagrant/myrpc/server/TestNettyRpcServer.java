package vagrant.myrpc.server;

import vagrant.myrpc.annotation.ServiceScan;

@ServiceScan
public class TestNettyRpcServer {
    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer("127.0.0.1", 9999);
        server.start();
    }

}
