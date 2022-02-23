package vagrant.myrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.enumeration.PackageType;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.serializer.CommonSerializer;

/**
 * 协议：
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
@Slf4j
public class CommonEncoder extends MessageToByteEncoder {
    private final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
          byteBuf.writeInt(MAGIC_NUMBER);
          if(o instanceof RpcRequest) {
              byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
          }else if(o instanceof RpcResponse) {
              byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
          }else {
              log.error("协议包未识别！");
              throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
          }
          byteBuf.writeInt(serializer.getCode());
          byte[] bytes = serializer.serialize(o);
          byteBuf.writeInt(bytes.length);
          byteBuf.writeBytes(bytes);
    }
}
