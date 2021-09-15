package danran.rpc.config;

import danran.rpc.client.ClientProxyFactory;
import danran.rpc.client.discovery.redisdis.RedisDiscovery;
import danran.rpc.client.discovery.zkdis.ZkDiscovery;
import danran.rpc.client.net.NettyNetClient;
import danran.rpc.common.constant.RegisterType;
import danran.rpc.common.protocol.JavaSerializeMessageProtocol;
import danran.rpc.common.protocol.MessageProtocol;
import danran.rpc.properties.RpcProperties;
import danran.rpc.server.NettyRpcServer;
import danran.rpc.server.RequestHandler;
import danran.rpc.server.RpcServer;
import danran.rpc.server.register.DefaultRpcProcessor;
import danran.rpc.server.register.RedisExportServiceRegister;
import danran.rpc.server.register.ServiceRegister;
import danran.rpc.server.register.ZookeeperExportServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Spring boot 自动配置类
 *
 * @Classname AutoConfiguration
 * @Description TODO
 * @Date 2021/8/23 19:47
 * @Created by ASUS
 */
@Configuration
public class AutoConfiguration {

    @Bean
    public DefaultRpcProcessor defaultRpcProcessor() {
        return new DefaultRpcProcessor();
    }

    @Bean
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }

    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired RpcProperties rpcProperties) {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();
        // 设置服务发现者
        String registerType = rpcProperties.getRegisterType();
        switch (registerType) {
            case RegisterType.REDIS: {
                clientProxyFactory.setServiceDiscovery(new RedisDiscovery(rpcProperties.getRegisterAddress()));
                break;
            }
            case RegisterType.ZOOKEEPER: {
                clientProxyFactory.setServiceDiscovery(new ZkDiscovery(rpcProperties.getRegisterAddress()));
                break;
            }
        }

        // 设置支持的协议
        Map<String, MessageProtocol> supportedProtocols = new HashMap<>();
        supportedProtocols.put(rpcProperties.getProtocol(), new JavaSerializeMessageProtocol());
        clientProxyFactory.setSupportMessageProtocols(supportedProtocols);
        // 设置网络层的实现
        clientProxyFactory.setNetClient(new NettyNetClient());
        return clientProxyFactory;
    }

    @Bean
    public ServiceRegister serviceRegister(@Autowired RpcProperties rpcProperties) {
        String registerType = rpcProperties.getRegisterType();
        switch (registerType) {
            case RegisterType.REDIS: {
                return new RedisExportServiceRegister(
                        rpcProperties.getRegisterAddress(),
                        rpcProperties.getServerPort(),
                        rpcProperties.getProtocol()
                );
            }
            case RegisterType.ZOOKEEPER: {
                return new ZookeeperExportServiceRegister(
                        rpcProperties.getRegisterAddress(),
                        rpcProperties.getServerPort(),
                        rpcProperties.getProtocol()
                );
            }
            default: {
                return new ZookeeperExportServiceRegister(
                        rpcProperties.getRegisterAddress(),
                        rpcProperties.getServerPort(),
                        rpcProperties.getProtocol()
                );
            }
        }
    }


    @Bean
    public RequestHandler requestHandler(@Autowired ServiceRegister serviceRegister) {
        return new RequestHandler(new JavaSerializeMessageProtocol(), serviceRegister);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler handler, @Autowired RpcProperties rpcProperties) {
        return new NettyRpcServer(rpcProperties.getServerPort(), rpcProperties.getProtocol(), handler);
    }
}
