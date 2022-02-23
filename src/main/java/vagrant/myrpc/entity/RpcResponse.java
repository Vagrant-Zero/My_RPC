package vagrant.myrpc.entity;

import lombok.Data;
import vagrant.myrpc.enumeration.ResponseCode;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {

    /**
     * 响应状态码
     */
    private Integer status;

    /**
     * 响应状态补充信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatus(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatus(code.getCode());
        response.setMessage(code.getMsg());
        return response;
    }
}
