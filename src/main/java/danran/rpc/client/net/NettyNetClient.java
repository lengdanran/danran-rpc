package danran.rpc.client.net;

import danran.rpc.client.net.handler.SendHandler;
import danran.rpc.common.service.Service;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Netty网络请求客户端，定义通过Netty实现网络请求的细则。
 *
 * @Classname NettyNetClient
 * @Description TODO
 * @Date 2021/8/23 16:38
 * @Created by ASUS
 */
public class NettyNetClient implements NetClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyNetClient.class);

    /**
     * 将数据发送到服务端
     *
     * @param data    the data to pend to the service
     * @param service service
     * @return rsp
     * @throws InterruptedException interrupt
     */
    @Override
    public byte[] sendRequest(byte[] data, Service service) throws InterruptedException {
        String[] ip_port = service.getAddress().split(":");
        String ip = ip_port[0];
        int port = Integer.parseInt(ip_port[1]);

        SendHandler sendHandler = new SendHandler(data);
        byte[] rsp;
        // 配置客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            // 初始化通道
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(sendHandler);
                }
            });
            // 启动连接
            bootstrap.connect(ip, port).sync();
            rsp = (byte[]) sendHandler.rspData();
            logger.info("Send Request and get the response: {}", rsp);
        } finally {
            // 释放线程组资源
            group.shutdownGracefully();
        }
        return rsp;
    }
}
