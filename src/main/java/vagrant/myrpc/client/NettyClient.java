package vagrant.myrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.client.hanlder.NettyClientHandler;
import vagrant.myrpc.codec.CommonDecoder;
import vagrant.myrpc.codec.CommonEncoder;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.serializer.JsonSerializer;

@Slf4j
public class NettyClient implements Client{
    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 初始化bootstarp
     */
    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new CommonDecoder());
                        socketChannel.pipeline().addLast(new CommonEncoder(new JsonSerializer()));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public Object sendRequest(RpcRequest request) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.debug("连接到服务器{}：{}", host, port);
            Channel channel = future.channel();
            if(channel != null) {
                channel.writeAndFlush(request).addListener(future1 -> { // 异步接受结果
                    if(future1.isSuccess()) {
                        log.info(String.format("客户端成功发送消息: %s", request.toString()));
                    }else {
                        log.error("发送消息时有错误发生: {}", future1.cause());
                    }
                });
            }
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse response = channel.attr(key).get();
            return response.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
