package vagrant.myrpc.server.impl;

import vagrant.myrpc.annotation.Service;
import vagrant.myrpc.api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "byebye, " + name;
    }
}
