package danran.rpc.server.register;

import com.alibaba.fastjson.JSON;
import danran.rpc.common.serializer.ZookeeperSerializer;
import danran.rpc.common.service.Service;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

/***
 * Zookeeper服务注册器，提供服务注册、服务暴露的能力
 *
 * @Classname ZookeeperExportServiceRegister
 * @Description TODO
 * @Date 2021/8/24 11:25
 * @Created by ASUS
 */
public class ZookeeperExportServiceRegister extends DefaultServiceRegister implements ServiceRegister {
    /*zk客户端*/
    private ZkClient zkClient;

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperExportServiceRegister.class);

    public ZookeeperExportServiceRegister(String zkAddress, int port, String protocol) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
    }

    /***
     * 服务注册-zk
     *
     * @param serviceObject 服务持有者
     * @throws Exception 注册异常
     */
    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        super.register(serviceObject);
        Service service = new Service();

        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        logger.info("服务：" + serviceObject.getName() + "=>源地址：" + address);

        service.setAddress(address);
        service.setName(serviceObject.getClass().getName());
        service.setProtocol(protocol);
        // 暴露服务
        this.exportService(service);
    }

    /***
     * 服务暴露
     *
     * @param service 需要暴露的服务
     */
    private void exportService(Service service) {
        String serviceName = service.getName();
        String uri = JSON.toJSONString(service);
        try {
            uri = URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = "/rpc/" + serviceName + "/service";
        logger.info("servicePath == " + servicePath);

        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }
        String uriPath = servicePath + "/" + uri;
        logger.info("uriPath == " + uriPath);

        if (!zkClient.exists(uriPath)) zkClient.delete(uriPath);
        zkClient.createEphemeral(uriPath);// 创建一个短暂的节点
    }

}

