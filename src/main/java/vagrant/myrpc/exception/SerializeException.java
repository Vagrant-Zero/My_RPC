package vagrant.myrpc.exception;

/**
 * 序列化时异常
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg) {
        super(msg);
    }
}
