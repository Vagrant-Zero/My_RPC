package vagrant.myrpc.serializer;

public interface CommonSerializer {

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     * @param bytes
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 获取序列化器的编号
     * @return
     */
    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
