package vagrant.myrpc.client;

import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.serializer.CommonSerializer;

public interface Client {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest request);

    void setSerializer(CommonSerializer serializer);
}
