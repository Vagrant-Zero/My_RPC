package vagrant.myrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.enumeration.PackageType;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.serializer.CommonSerializer;

import java.util.List;

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
public class CommonDecoder extends ReplayingDecoder {
    private final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNumber = byteBuf.readInt();
        if(magicNumber != MAGIC_NUMBER) {
            log.error("未识别的协议包！{}", magicNumber);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageType = byteBuf.readInt();
        Class<?> packageClass;
        if(packageType == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        }else if(packageType == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        }else {
            log.error("未识别的数据包！");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerType = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerType);
        if(serializer == null) {
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int dataLength = byteBuf.readInt();
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        Object message = serializer.deserialize(bytes, packageClass);
        list.add(message);
    }
}
