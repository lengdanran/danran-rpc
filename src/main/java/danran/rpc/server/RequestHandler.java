package danran.rpc.server;

import danran.rpc.common.protocol.MessageProtocol;
import danran.rpc.common.protocol.RequestWarp;
import danran.rpc.common.protocol.ResponseWarp;
import danran.rpc.common.protocol.Status;
import danran.rpc.server.register.ServiceObject;
import danran.rpc.server.register.ServiceRegister;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/***
 * 请求处理者，提供解组请求、编组响应等操作
 *
 * @Classname RequestHandler
 * @Description TODO
 * @Date 2021/8/24 13:10
 * @Created by ASUS
 */
public class RequestHandler {
    private MessageProtocol protocol;

    private ServiceRegister serviceRegister;

    public RequestHandler(MessageProtocol protocol, ServiceRegister serviceRegister) {
        this.protocol = protocol;
        this.serviceRegister = serviceRegister;
    }

    public byte[] handleRequest(byte[] data) throws Exception {
        // 1.解组消息
        RequestWarp request = this.protocol.unmarshallingRequest(data);
        // 2.查找服务对象
        ServiceObject serviceObject = this.serviceRegister.getServiceObject(request.getServiceName());
        ResponseWarp rsp;
        if (serviceObject == null) rsp = new ResponseWarp(Status.NOT_FOUND);
        else {
            // 3.反射调用对应的过程方法
            try {
                Method method = serviceObject.getClazz().getMethod(request.getMethod(), request.getParameterTypes());
                Object returnValue = method.invoke(serviceObject.getObj(), request.getParameters());
                rsp = new ResponseWarp(Status.SUCCESS);
                rsp.setReturnValue(returnValue);
            } catch (NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                rsp = new ResponseWarp(Status.ERROR);
                rsp.setException(e);
                e.printStackTrace();
            }
        }
        // 4.编组响应
        return this.protocol.marshallingResponse(rsp);
    }

    public MessageProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(MessageProtocol protocol) {
        this.protocol = protocol;
    }

    public ServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    public void setServiceRegister(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }
}
