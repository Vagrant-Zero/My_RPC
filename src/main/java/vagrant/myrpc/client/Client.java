package vagrant.myrpc.client;

import vagrant.myrpc.entity.RpcRequest;

public interface Client {
    Object sendRequest(RpcRequest request);
}
