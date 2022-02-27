package vagrant.myrpc.serializer;

public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

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
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
