package danran.rpc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/***
 * 参数配置类，实现用户自定义参数
 *
 * @Classname RpcProperties
 * @Description TODO
 * @Date 2021/8/24 13:59
 * @Created by ASUS
 */
@EnableConfigurationProperties(RpcProperties.class)
@ConfigurationProperties(prefix = "danran.rpc")
public class RpcProperties {
    /**
     * 服务注册中心
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务端暴露端口
     */
    private Integer serverPort = 6666;

    /**
     * 服务协议
     */
    private String protocol = "danran";

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
