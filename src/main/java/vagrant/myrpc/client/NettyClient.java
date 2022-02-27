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
import vagrant.myrpc.factory.SingletonFactory;
import vagrant.myrpc.loadbalancer.LoadBalancer;
import vagrant.myrpc.loadbalancer.RandomLoadBalancer;
import vagrant.myrpc.register.NacosServiceDiscovery;
import vagrant.myrpc.register.NacosServiceRegistry;
import vagrant.myrpc.register.ServiceDiscovery;
import vagrant.myrpc.register.ServiceRegistry;
import vagrant.myrpc.serializer.CommonSerializer;
import vagrant.myrpc.serializer.JsonSerializer;
import vagrant.myrpc.serializer.KryoSerializer;
import vagrant.myrpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NettyClient implements Client{

    /**
     * 这两个变量是不需要的，因为channel是从ChannelProvider里面拿到的，用的并不是这里的bootstrap
     */
//    private static final EventLoopGroup group;
//    private static final Bootstrap bootstrap;

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private CommonSerializer serializer;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    /**
     * 初始化bootstarp
     */
    static {
        /**
         * 以下三行应该是可以不需要的，因为channel是从工厂（ChannelProvider）里面提供的。
         */
//        group = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class);

//        EventLoopGroup group = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        socketChannel.pipeline().addLast(new CommonDecoder());
////                        socketChannel.pipeline().addLast(new CommonEncoder(new JsonSerializer()));
//                        socketChannel.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
//                        socketChannel.pipeline().addLast(new NettyClientHandler());
//                    }
//                });
    }


    @Override
    public Object sendRequest(RpcRequest request) {
        if(serializer == null) {
            log.error("未设置序列化器！");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
//        AtomicReference<Object> result = new AtomicReference<>(null);
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {

                unprocessedRequests.put(request.getRequestId(), resultFuture);
                channel.writeAndFlush(request).addListener((ChannelFutureListener)future1 -> { // 异步接受结果
                    if(future1.isSuccess()) {
                        log.info(String.format("客户端成功发送消息: %s", request.toString()));
                    }else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        log.error("发送消息时有错误发生: {}", future1.cause());
                    }
                });

//                channel.closeFuture().sync();
//                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
//                RpcResponse response = channel.attr(key).get();
//                RpcMessageChecker.check(request, response);
//                result.set(response.getData());
            }else {
//                System.exit(0);
                /**
                 * channel是从ChannelProvider里面拿到的，用的并不是这里的group
                 */
//                group.shutdownGracefully();
                return null;
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(request.getRequestId());
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
//            e.printStackTrace();
//            log.error("发送消息时有错误发生！{}", e);
        }
        return resultFuture;
    }
}
