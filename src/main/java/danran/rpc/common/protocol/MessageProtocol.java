package danran.rpc.common.protocol;

/**
 * @Classname MessageProtocol
 * @Description TODO
 * @Date 2021/8/23 17:00
 * @Created by ASUS
 *
 * 消息协议，定义编组请求、解组请求、编组响应、解组响应规范
 *
 */
public interface MessageProtocol {

    /**
     * 编组请求
     *
     * @param req 请求信息
     * @return 请求字节数组
     * @throws Exception 编组请求异常
     */
    byte[] marshallingRequest(RequestWarp req) throws Exception;

    /**
     * 解组请求
     *
     * @param data 请求字节数组
     * @return 请求信息
     * @throws Exception 解组请求异常
     */
    RequestWarp unmarshallingRequest(byte[] data) throws Exception;

    /**
     * 编组响应
     *
     * @param rsp 响应信息
     * @return 响应字节数组
     * @throws Exception 编组响应异常
     */
    byte[] marshallingResponse(ResponseWarp rsp) throws Exception;

    /**
     * 解组响应
     *
     * @param data 响应字节数组
     * @return 响应信息
     * @throws Exception 解组响应异常
     */
    ResponseWarp unmarshallingResponse(byte[] data) throws Exception;
}

