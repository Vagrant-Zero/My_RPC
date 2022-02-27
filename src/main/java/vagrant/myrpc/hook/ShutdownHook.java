package vagrant.myrpc.hook;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.factory.ThreadPoolFactory;
import vagrant.myrpc.util.NacosUtil;

@Slf4j
public class ShutdownHook {

    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    public void addClearAllHook() {
        log.debug("关闭后将自动清除所有服务！");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
