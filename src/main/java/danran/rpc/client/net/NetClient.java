package danran.rpc.client.net;

import danran.rpc.common.service.Service;

/**
 * @Classname NetClient
 * @Description TODO
 * @Date 2021/8/23 16:35
 * @Created by ASUS
 * 网络请求客户端，定义网络的请求规范
 */
public interface NetClient {
    /**
     * 将数据发送到服务端
     *
     * @param data the data to pend to the service
     * @param service service
     * @return rsp
     * @throws InterruptedException interrupt
     */
    byte[] sendRequest(byte[] data, Service service) throws InterruptedException;
}
