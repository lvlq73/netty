package com.llq.netty.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvlianqi
 * @description 服务发现，模拟注册中心（可用zookeeper替换）
 * @createDate 2020/5/21
 */
public class ServiceDiscovery {

    private ServiceDiscovery() throws Exception {
        throw new Exception("static class , can't new class");
    }

    private static final List<Address> serviceAddress = new ArrayList<>(5);
    private static int currentIndex;
    private static int totalServer;

    /**
     * 添加服务地址
     * @param address
     */
    public static void addServiceAddress(Address address) {
        serviceAddress.add(address);
        totalServer = serviceAddress.size();
        currentIndex = totalServer - 1;
    }

    /**
     * 移除服务地址
     * @param address
     */
    public static void removeServiceAddress(Address address){
        serviceAddress.remove(address);
    }

    /**
     * 获取服务地址
     * @return
     */
    public static Address discovery() {
        currentIndex = (currentIndex + 1) % totalServer;
        return serviceAddress.get(currentIndex);
    }

    /**
     * 获取所有地址信息
     * @return
     */
    public static List<Address> getServiceAddress() {
        return serviceAddress;
    }
}
