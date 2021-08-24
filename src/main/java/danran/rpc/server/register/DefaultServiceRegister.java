package danran.rpc.server.register;

import java.util.HashMap;
import java.util.Map;

/***
 * 默认服务注册器
 *
 * @Classname DefaultServiceRegister
 * @Description TODO
 * @Date 2021/8/24 11:06
 * @Created by ASUS
 */
public class DefaultServiceRegister implements ServiceRegister{
    private Map<String, ServiceObject> serviceObjectMap = new HashMap<>();

    protected String protocol;

    protected int port;

    @Override
    public void register(ServiceObject so) throws Exception {
        if (so == null) throw new IllegalArgumentException("等待注册的服务不能为 null !");
        this.serviceObjectMap.put(so.getName(), so);
    }

    @Override
    public ServiceObject getServiceObject(String name) throws Exception {
        return this.serviceObjectMap.get(name);
    }
}
