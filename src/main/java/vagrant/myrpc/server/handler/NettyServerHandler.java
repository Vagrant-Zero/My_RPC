package vagrant.myrpc.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.enumeration.ResponseCode;
import vagrant.myrpc.server.RequestHandler;
import vagrant.myrpc.server.registry.DefaultServiceRegistry;
import vagrant.myrpc.server.registry.ServiceRegistry;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        try {
            log.debug("服务器接收到消息：{}", request);
            String interfaceName = request.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(request, service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
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
}
