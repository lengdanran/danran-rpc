package danran.rpc.common.protocol;

import java.io.*;

/**
 * @Classname JavaSerializeMessageProtocol
 * @Description TODO
 * @Date 2021/8/23 19:38
 * @Created by ASUS
 *
 * java 序列化消息协议实现类
 */
public class JavaSerializeMessageProtocol implements MessageProtocol {

    public byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(obj);
        return out.toByteArray();
    }

    /**
     * 编组请求
     *
     * @param req 请求信息
     * @return 请求字节数组
     * @throws Exception 编组请求异常
     */
    @Override
    public byte[] marshallingRequest(RequestWarp req) throws Exception {
        return this.serialize(req);
    }

    /**
     * 解组请求
     *
     * @param data 请求字节数组
     * @return 请求信息
     * @throws Exception 解组请求异常
     */
    @Override
    public RequestWarp unmarshallingRequest(byte[] data) throws Exception {
        ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(data));
        return (RequestWarp) objectIn.readObject();
    }

    /**
     * 编组响应
     *
     * @param rsp 响应信息
     * @return 响应字节数组
     * @throws Exception 编组响应异常
     */
    @Override
    public byte[] marshallingResponse(ResponseWarp rsp) throws Exception {
        return this.serialize(rsp);
    }

    /**
     * 解组响应
     *
     * @param data 响应字节数组
     * @return 响应信息
     * @throws Exception 解组响应异常
     */
    @Override
    public ResponseWarp unmarshallingResponse(byte[] data) throws Exception {
        return (ResponseWarp) new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
    }
}
