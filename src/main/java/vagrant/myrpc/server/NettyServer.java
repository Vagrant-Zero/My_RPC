package vagrant.myrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.codec.CommonDecoder;
import vagrant.myrpc.codec.CommonEncoder;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.hook.ShutdownHook;
import vagrant.myrpc.register.NacosServiceRegistry;
import vagrant.myrpc.register.ServiceRegistry;
import vagrant.myrpc.serializer.CommonSerializer;
import vagrant.myrpc.serializer.JsonSerializer;
import vagrant.myrpc.serializer.KryoSerializer;
import vagrant.myrpc.server.handler.NettyServerHandler;
import vagrant.myrpc.server.provider.ServiceProvider;
import vagrant.myrpc.server.provider.ServiceProviderImpl;

import java.net.InetSocketAddress;

@Slf4j
public class NettyServer implements Server{

    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry; // 注册中心
    private final ServiceProvider serviceProvider; // 本地注册表

    private CommonSerializer serializer; // 序列化器

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    // 没有像作者一样在这个方法里面调用start（）方法，而是单独 启动！
    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            log.error("未设置序列化器！");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
    }


    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearAllHook(); // 添加回调事件：即在服务端关闭时，自动注销Nacos的所有服务
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new CommonDecoder());
//                            socketChannel.pipeline().addLast(new CommonEncoder(new JsonSerializer()));
                            socketChannel.pipeline().addLast(new CommonEncoder(serializer)); // 这里可以优化为配置文件的配置，见聊天室项目
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时有异常！" + e.getMessage());
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }

//    @Override
//    public void setSerializer(CommonSerializer serializer) {
//        this.serializer = serializer;
//    }
}
