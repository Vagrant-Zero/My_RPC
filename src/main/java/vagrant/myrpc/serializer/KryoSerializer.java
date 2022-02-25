package vagrant.myrpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.entity.RpcRequest;
import vagrant.myrpc.entity.RpcResponse;
import vagrant.myrpc.enumeration.SerializerCode;
import vagrant.myrpc.exception.RpcError;
import vagrant.myrpc.exception.RpcException;
import vagrant.myrpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo序列化的解析：https://blog.csdn.net/ailiandeziwei/article/details/106674477
 * 注册行为，见：https://blog.csdn.net/xiaomin1991222/article/details/84913082
 */
@Slf4j
public class KryoSerializer implements CommonSerializer{
    private ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true); // 支持序列化循环依赖
        /**
         * Kryo 支持对类进行注册。注册行为会给每一个 Class 编一个号码，从 0 开始；但是，Kryo 并不保证同一个 Class 每一次的注册的号码都相同（比如重启 JVM 后，用户访问资源的顺序不同，就会导致类注册的先后顺序不同）。
         * 也就是说，同样的代码、同一个 Class ，在两台机器上的注册编号可能不一致；那么，一台机器序列化之后的结果，可能就无法在另一台机器上反序列化。
         * 因此，对于多机器部署的情况，建议关闭注册，让 Kryo 记录每个类型的真实的名称。
         * 而且，注册行为需要用户对每一个类进行手动注册：即便使用者注册了 A 类型，而 A 类型内部使用了 B 类型，使用者也必须手动注册 B 类型；（甚至，即便某一个类型是 JDK 内部的类型，比如 ArrayList ，也是需要手动注册的）一个普通的业务对象，往往需要注册十几个 Class，这是十分麻烦、甚至是寸步难行的。
         * 关闭注册行为，需要保证没有进行过这样的设置：
         * kryo.setRegistrationRequired(true);
         * 并且要保证没有显式地注册任何一个类，例如：
         * kryo.addServiceProvider(ArrayList.class);
         * 同时保证以上二者，才真正地关闭了注册行为。
         */
        kryo.setRegistrationRequired(false); // 虽然这里的版本是单机，但也关闭了注册。
        return kryo;
    });


    @Override
    public byte[] serialize(Object object) {
        try(
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream);
        ) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove(); // 避免内存泄漏，需要remove
            return output.toBytes();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("序列化时出错！错误为：{}", e.getMessage());
            throw new SerializeException("序列化时出错！");
        }

    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream);
        ) {
            Kryo kryo = kryoThreadLocal.get();
            Object object = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("反序列化时出错！异常为：{}", e.getMessage());
            throw new SerializeException("反序列化时出错！");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KYRO.getCode();
    }
}
