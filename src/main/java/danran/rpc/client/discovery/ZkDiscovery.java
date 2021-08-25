package danran.rpc.client.discovery;

import com.alibaba.fastjson.JSON;
import danran.rpc.common.serializer.ZookeeperSerializer;
import danran.rpc.common.service.Service;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Classname ZkDiscovery
 * @Description TODO
 * @Date 2021/8/23 15:25
 * @Created by ASUS
 */
public class ZkDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZkDiscovery.class);

    private final String ZkServerHostPath = "/rpc";

    private final ZkClient zkClient;

    public ZkDiscovery(String zkServerAddress) {
        /*zk客户端*/
        zkClient = new ZkClient(zkServerAddress);
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }

    /**
     * 使用Zookeeper客户端，通过服务名获取服务列表
     * 服务名格式：接口全路径
     *
     * @param name 服务名
     * @return 服务列表
     */
    @Override
    public List<Service> getServices(String name) {
        String servicePath = ZkServerHostPath + "/" + name + "/service";
        logger.info("寻找服务提供者: " + servicePath);
        // 获得服务具体的实现者，子节点的名称为具体服务的JSON序列化字符串
        List<String> serviceInstances = null;
        try {
            serviceInstances = zkClient.getChildren(servicePath);
        } catch (RuntimeException e) {
            logger.error("服务发现出错");
            e.printStackTrace();
        }
//        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(serviceInstances).orElse(new ArrayList<>()).stream().map(str -> {
            String deCh = null;
            try {
                deCh = URLDecoder.decode(str, "UTF-8");
                logger.info("发现服务提供者: {}", deCh);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSON.parseObject(deCh, Service.class);
        }).collect(Collectors.toList());
    }
}
