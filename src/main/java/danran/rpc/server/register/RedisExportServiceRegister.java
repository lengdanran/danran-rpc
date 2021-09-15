package danran.rpc.server.register;
import com.alibaba.fastjson.JSON;
import danran.rpc.common.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;


/**
 * Redis服务注册器，提供服务注册、服务暴露的能力
 *
 * @Classname ZookeeperExportServiceRegister
 * @Description TODO
 * @Date 2021/9/6 23:25
 * @Created by ASUS
 */
public class RedisExportServiceRegister extends DefaultServiceRegister implements ServiceRegister {

    private static final Logger logger = LoggerFactory.getLogger(RedisExportServiceRegister.class);

    private static final int MAX_TOTAL = 10;

    private static final int IDLE = 4;

    private final JedisPool jedisPool;

    public RedisExportServiceRegister(String redisAddress, int port, String protocol) {
        this(redisAddress, MAX_TOTAL, IDLE, port, protocol);
    }

    public RedisExportServiceRegister(String redisAddress, int MaxTotal, int Idle, int port, String protocol) {
        String[] split = redisAddress.split(":");
        String host = split[0];
        int redisPort = Integer.parseInt(split[1]);
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大连接数
        config.setMaxTotal(MaxTotal);
        // 最大连接空闲数
        config.setMaxIdle(Idle);
        this.jedisPool = new JedisPool(config, host, redisPort);
        this.port = port;
        this.protocol = protocol;
    }

    /***
     * 服务注册-redis
     *
     * @param serviceObject 服务持有者
     * @throws Exception 注册异常
     */
    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        super.register(serviceObject);// 父类注册
        Service service = new Service();

        String host = InetAddress.getLocalHost().getHostAddress();// 获得本机的host地址ip
        String address = host + ":" + port;
        logger.info("服务：" + serviceObject.getName() + "=>在源地址：" + address + " 提供服务");

        service.setAddress(address);
        service.setName(serviceObject.getClazz().getName());
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
        // 抽象的服务定位节点
        String servicePath = "/rpc/" + serviceName + "/service";
        logger.info("抽象服务[" + serviceName + "]注册地址 == " + servicePath);

        // servicePath 作为redis中key存入到缓存中, 使用redis的list数据结构
        Jedis conn = jedisPool.getResource();
        conn.sadd(servicePath, uri);
    }
}
