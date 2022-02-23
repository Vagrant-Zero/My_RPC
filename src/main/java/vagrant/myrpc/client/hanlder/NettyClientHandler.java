package vagrant.myrpc.client.hanlder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcResponse;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        try{
            log.debug("客户端接收到消息：{}", response);
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(response); // 将响应消息与key绑定，便于后续异步获得响应消息
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(response);
        }
    }
}
