package danran.rpc.common.serializer;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @Classname ZkSerializer
 * @Description TODO
 * @Date 2021/8/23 15:30
 * @Created by ASUS
 */
public class ZookeeperSerializer implements ZkSerializer {
    /**
     * 序列化
     *
     * @param data 对象
     * @return 字节数组
     */
    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        return String.valueOf(data).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @return 对象
     */
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
