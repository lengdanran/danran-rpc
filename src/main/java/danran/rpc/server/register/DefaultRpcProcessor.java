package danran.rpc.server.register;

import danran.rpc.annotation.InjectService;
import danran.rpc.annotation.RPCService;
import danran.rpc.client.ClientProxyFactory;
import danran.rpc.server.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;


/**
 * @Classname DefaultRpcProcessor
 * @Description TODO
 * @Date 2021/8/23 19:57
 * @Created by ASUS
 * <p>
 * RPC处理者，支持服务启动暴露。自动注入Service
 */
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ClientProxyFactory clientProxyFactory;

    @Resource
    private ServiceRegister serviceRegister;

    @Resource
    private RpcServer rpcServer;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            this.startRpcServer(context);
            this.injectService(context);
        }
    }

    private void startRpcServer(ApplicationContext context) {
        Map<String, Object> beans = context.getBeansWithAnnotation(RPCService.class);
        // 当有需要注册的服务时，进行注册
        if (beans.size() != 0) {
            boolean startedFlag = true;
            // 遍历map中的所有需要暴露的服务
            for (Object obj : beans.values()) {
                try {
                    Class<?> clazz = obj.getClass();
                    Class<?>[] interfaces = clazz.getInterfaces();// 获得该类实现的接口列表
                    ServiceObject serviceObject;
                    if (interfaces.length != 1) {
                        RPCService rpcService = clazz.getAnnotation(RPCService.class);
                        String serviceName = rpcService.value();
                        if ("".equals(serviceName)) {
                            startedFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '" + obj.getClass().getName() + "'");
                        }
                        serviceObject = new ServiceObject(serviceName, Class.forName(serviceName), obj);
                    } else {
                        Class<?> superClass = interfaces[0];
                        serviceObject = new ServiceObject(superClass.getName(), superClass, obj);
                    }
                    serviceRegister.register(serviceObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (startedFlag) rpcServer.start();
        }
    }

    private void injectService(ApplicationContext context) {
        String[] names = context.getBeanDefinitionNames();// Return the names of all beans defined in this factory.
        for (String name : names) {
            // 获得该类的类名
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) continue;
            // 获得该类到的属性列表
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 判断是否添加了InjectService注解
                InjectService inject = field.getAnnotation(InjectService.class);
                if (Objects.isNull(inject)) continue;
                // 获取当前属性的类型
                Class<?> fieldClazz = field.getType();
                // 为当前的bean的当前属性赋值
                Object bean = context.getBean(name);// 获得当前bean
                field.setAccessible(true);// 打开修改权限
                try {
                    // 为当前属性赋值代理对象
                    field.set(bean, clientProxyFactory.getProxy(fieldClazz));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
