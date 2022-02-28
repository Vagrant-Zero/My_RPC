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
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.codec.CommonDecoder;
import vagrant.myrpc.codec.CommonEncoder;
import vagrant.myrpc.hook.ShutdownHook;
import vagrant.myrpc.register.NacosServiceRegistry;
import vagrant.myrpc.serializer.CommonSerializer;
import vagrant.myrpc.server.handler.NettyServerHandler;
import vagrant.myrpc.server.provider.ServiceProviderImpl;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer extends AbstractRpcServer{

    private final CommonSerializer serializer;

    public NettyRpcServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyRpcServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanService();
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
                            socketChannel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new CommonDecoder());
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
}
