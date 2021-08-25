package danran.rpc.client.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @Classname SendHandler
 * @Description TODO
 * @Date 2021/8/23 16:11
 * @Created by ASUS
 * 发送处理类，定义Netty入站处理细则
 */
public class SendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SendHandler.class);
    private final CountDownLatch latch;
    private Object readMsg = null;
    private final byte[] data;

    public SendHandler(byte[] data) {
        latch = new CountDownLatch(1);
        this.data = data;
    }

    /**
     * 当连接服务端成功后，发送请求数据
     *
     * @param context 通道上下文
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        logger.info("Successful connect to the server: {}", context);
        ByteBuf requestBuf = Unpooled.buffer(data.length);
        requestBuf.writeBytes(data);
        logger.info("Client send the message: {}", requestBuf);
        context.writeAndFlush(requestBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        logger.info("Client read the message:{}", msg);
        ByteBuf msgBuf = (ByteBuf) msg;
        byte[] rsp = new byte[msgBuf.readableBytes()];
        msgBuf.readBytes(rsp);
        readMsg = rsp;
        latch.countDown();
    }

    /**
     * 等待读取数据完成
     *
     * @return 响应数据
     * @throws InterruptedException 异常
     */
    public Object rspData() throws InterruptedException {
        latch.await();
        return readMsg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        logger.error("Exception occurred：{}", cause.getMessage());
        ctx.close();
    }
}
