package vagrant.myrpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "调用方法成功！"),
    FAIL(500, "调用方法失败"),
    NOT_FOUND_METHOD(501, "未找到指定方法"),
    NOT_FOUND_CLASS(502, "没有找到指定类");

    private final int code; // 状态码
    private final String msg; // 状态信息
}
