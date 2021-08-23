package danran.rpc.client.discovery;

import danran.rpc.common.service.Service;

import java.util.List;

/**
 * @Classname ServiceDiscovery
 * @Description TODO
 * @Date 2021/8/23 15:22
 * @Created by LengDanran
 *
 * 定义服务发现抽象类
 */
public interface ServiceDiscovery {
    List<Service> getServices(String name);
}
