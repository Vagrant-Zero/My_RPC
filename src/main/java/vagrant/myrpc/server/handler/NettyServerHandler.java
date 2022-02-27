package vagrant.myrpc.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.factory.SingletonFactory;
import vagrant.myrpc.server.RequestHandler;
import vagrant.myrpc.server.provider.ServiceProviderImpl;
import vagrant.myrpc.server.provider.ServiceProvider;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final RequestHandler requestHandler;
//    private static ServiceProvider serviceProvider;


    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
//        try {
//            log.debug("服务器接收到消息：{}", request);
////            String interfaceName = request.getInterfaceName();
////            Object service = serviceProvider.getServiceProvider(interfaceName);
//            Object result = requestHandler.handle(request/*, service*/);
//            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, request.getRequestId()));
//            future.addListener(ChannelFutureListener.CLOSE);
//        } finally {
//            ReferenceCountUtil.release(request);
//        }
        try {
            if(request.getHeartBeat()) {
                log.info("接收到客户端心跳包...");
                return;
            }
            log.info("服务器接收到请求: {}", request);
            Object result = requestHandler.handle(request); // 调用服务端的实现类，真正执行方法
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(RpcResponse.success(result, request.getRequestId()));
            } else {
                log.error("通道不可写");
            }
        } finally {
            ReferenceCountUtil.release(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
