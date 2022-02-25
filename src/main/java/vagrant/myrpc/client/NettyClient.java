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
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.register.NacosServiceRegistry;
import vagrant.myrpc.register.ServiceRegistry;
import vagrant.myrpc.serializer.CommonSerializer;
import vagrant.myrpc.serializer.JsonSerializer;
import vagrant.myrpc.serializer.KryoSerializer;
import vagrant.myrpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NettyClient implements Client{

    private static final Bootstrap bootstrap;

    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public NettyClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
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
//                        socketChannel.pipeline().addLast(new CommonEncoder(new JsonSerializer()));
                        socketChannel.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public Object sendRequest(RpcRequest request) {
        if(serializer == null) {
            log.error("未设置序列化器！");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                channel.writeAndFlush(request).addListener(future1 -> { // 异步接受结果
                    if(future1.isSuccess()) {
                        log.info(String.format("客户端成功发送消息: %s", request.toString()));
                    }else {
                        log.error("发送消息时有错误发生: {}", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse response = channel.attr(key).get();
                RpcMessageChecker.check(request, response);
                result.set(response.getData());
            }else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("发送消息时有错误发生！{}", e);
        }
        return result.get();
    }
}
