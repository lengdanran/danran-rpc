package danran.rpc.client;

import danran.rpc.client.discovery.ServiceDiscovery;
import danran.rpc.client.net.NetClient;
import danran.rpc.common.protocol.MessageProtocol;
import danran.rpc.common.protocol.RequestWarp;
import danran.rpc.common.protocol.ResponseWarp;
import danran.rpc.common.service.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.reflect.Proxy.newProxyInstance;


/***
 * 客户端代理工厂：用于创建远程服务代理类
 * 封装编组请求、请求发送、编组响应等操作。
 *
 * @Classname ClientProxyFactory
 * @Description TODO
 * @Date 2021/8/23 16:58
 * @Created by ASUS
 */
public class ClientProxyFactory {
    private ServiceDiscovery serviceDiscovery;

    private Map<String, MessageProtocol> supportMessageProtocols;

    private NetClient netClient;

    private Map<Class<?>, Object> objectCache = new HashMap<>();


    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public void setSupportMessageProtocols(Map<String, MessageProtocol> supportMessageProtocols) {
        this.supportMessageProtocols = supportMessageProtocols;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }

    public void setObjectCache(Map<Class<?>, Object> objectCache) {
        this.objectCache = objectCache;
    }

    /**
     * 通过Java动态代理获取服务代理类
     *
     * @param clazz 被代理类Class
     * @param <T>   泛型
     * @return 服务代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        //computeIfAbsent() 方法对 hashMap 中指定 key 的值进行重新计算，如果不存在这个 key，则添加到 hasMap 中。
        return (T) this.objectCache.computeIfAbsent(clazz, cls -> newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new ClientInvocationHandler(cls)));
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private final Class<?> clazz;

        private final Random random = new Random();

        public ClientInvocationHandler(Class<?> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            if (method.getName().equals("toString")) {
                return proxy.getClass().toString();
            }
            if (method.getName().equals("hashCode")) return 0;

            // 获得服务的信息, 根据服务类的类名去查询服务名称
            String serviceName = this.clazz.getName();
            List<Service> services = serviceDiscovery.getServices(serviceName);

            if (services == null || services.size() == 0) throw new RuntimeException("No service instance available!");

            // 软负载均衡，随机选择
            Service service = services.get(random.nextInt(services.size()));

            // 构建request对象
            RequestWarp req = new RequestWarp();
            req.setServiceName(service.getName());
            req.setMethod(method.getName());
            req.setParameterTypes(method.getParameterTypes());
            req.setParameters(args);

            // 协议层编组
            // 获得该方法对应的协议
            MessageProtocol protocol = supportMessageProtocols.get(service.getProtocol());
            // 编组请求
            byte[] data = protocol.marshallingRequest(req);

            // 调用网络层发送请求
            byte[] rsp = netClient.sendRequest(data, service);

            // 解组响应信息
            ResponseWarp response = protocol.unmarshallingResponse(rsp);

            // 结果处理
            if (response.getException() != null) throw response.getException();

            return response.getReturnValue();
        }
    }

}
