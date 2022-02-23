package vagrant.myrpc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    SERIALIZE_FAILED("序列化时失败"),
    DESERIALIZE_FAIL("反序列化时失败"),
    UNKNOWN_PROTOCOL("未识别的协议包"),
    UNKNOWN_PACKAGE_TYPE("未识别的数据包"),
    UNKNOWN_SERIALIZER("未识别的反序列化器");

    private final String message;
}
