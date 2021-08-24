package danran.rpc.server.register;

/***
 * 服务注册器，定义服务注册规范
 *
 * @Classname ServiceRegister
 * @Description TODO
 * @Date 2021/8/23 20:00
 * @Created by ASUS
 */
public interface ServiceRegister {

    void register(ServiceObject so) throws Exception;

    ServiceObject getServiceObject(String name) throws Exception;
}
