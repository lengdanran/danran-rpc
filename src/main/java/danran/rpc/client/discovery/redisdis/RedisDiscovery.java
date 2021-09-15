package danran.rpc.client.discovery.redisdis;

import com.alibaba.fastjson.JSON;
import danran.rpc.client.discovery.ServiceDiscovery;
import danran.rpc.common.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname RedisDiscovery
 * @Description TODO
 * @Date 2021/9/6 22:44
 * @Created by ASUS
 */
public class RedisDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(RedisDiscovery.class);

    private static final int MAX_TOTAL = 10;

    private static final int IDLE = 4;

    private final JedisPool jedisPool;

    /**
     * Redis服务发现构造函数
     *
     * @param redisAddress redis的服务地址 ip:port, 如："127.0.0.1:6379"
     * @param MaxTotal 最大连接数
     * @param Idle 最大连接空闲数
     */
    public RedisDiscovery(String redisAddress, int MaxTotal, int Idle) {
        String[] split = redisAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大连接数
        config.setMaxTotal(MaxTotal);
        // 最大连接空闲数
        config.setMaxIdle(Idle);
        this.jedisPool = new JedisPool(config, host, port);
    }

    public RedisDiscovery(String redisAddress) {
        this(redisAddress, MAX_TOTAL, IDLE);
    }

    @Override
    public List<Service> getServices(String name) {
        logger.info("寻找服务提供者: {}", name);
        String servicePath = "/rpc/" + name + "/service";
        // get connection from redis pool
        Jedis conn = this.jedisPool.getResource();
        // get service instances from redis server
        return conn.smembers(servicePath).stream().map(str -> {
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
