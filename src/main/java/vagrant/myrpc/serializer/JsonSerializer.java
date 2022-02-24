package vagrant.myrpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.enumeration.SerializerCode;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.exception.SerializeException;

import java.io.IOException;


@Slf4j
public class JsonSerializer implements CommonSerializer{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("序列化时发生错误！异常为：" + e.getMessage());
            e.printStackTrace();
            throw new SerializeException("序列化时发生错误！");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            log.error("反序列化时出错！异常为：" + e.getMessage());
            e.printStackTrace();
            throw new SerializeException("反序列化时出错！");
        }
    }

    /*
        这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
        需要重新判断处理
     */
    public Object handleRequest(Object obj) throws IOException {
        RpcRequest request = (RpcRequest) obj;
        for(int i = 0; i < request.getParameterTypes().length; i++) {
            Class<?> clazz = request.getParameterTypes()[i];
            if(!clazz.isAssignableFrom(request.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParameters()[i]);
                request.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return request;
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }


}
