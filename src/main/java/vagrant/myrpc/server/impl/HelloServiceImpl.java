package vagrant.myrpc.server.impl;

import lombok.extern.slf4j.Slf4j;
import vagrant.myrpc.annotation.Service;
import vagrant.myrpc.api.HelloObject;
import vagrant.myrpc.api.HelloService;

/**
 * 服务端的实现类
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(HelloObject helloObject) {
        log.debug("接收到{}", helloObject.getMsg());
        return "这是调用的返回值, id = " + helloObject.getId();
    }
}
