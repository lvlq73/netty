# netty4.1.X Rpc框架
## 概述
> #### 开发内容
* 用到动态代理+反射去调用服务的业务方法（注：简单调用，可自行拓展）
* 在没用用对象池的前提下，请求成功率不到50%；运用对象池后，请求成功率达到99%（注：本地调试，请求超时设置6秒，如果是真是场景会低一些）
* 加入了 __metrics__ 监控指标，监控连接数
* 加入了idle检测和keepalive，保持连接，防止恶意占用资源
* 服务端引入FlushConsolidationHandler减少flush的次数，增强写，牺牲延迟增加吞吐量
* 服务端引入业务线程池，减少等待时间

> #### 性能优化
* 对象池应用
* 建立多个连接（比如5个），重复利用
* 多服务，客户通过轮询请求不同服务地址，减少服务的压力（用到自行开发的[注册中心](https://github.com/lvlq73/demo/tree/main/registry-center-service )）

> #### 本地自测情况
* 10000万并发请求，处理时间15~17秒左右，成功率99.9%~100%
* 20000万并发请求，处理时间35秒左右，成功率99.9%~100%（用到多服务）

## 应用整合
> #### demo项目
以下内容可参考[demo项目](https://github.com/lvlq73/demo )的client-demo和service-demo
> #### springboot整合
* ##### [服务端](https://github.com/lvlq73/demo/tree/main/service-demo)
    pom.xml引入netty服务端依赖
   ```xml
   <dependency>
      <groupId>com.llq.netty</groupId>
      <artifactId>netty-server</artifactId>
  </dependency>
    ```
    resource文件下新建netty-rpc.properties配置文件
    ```properties
  #扫描service包
  netty.rpc.scan=com.llq.web.service
  #rpc ip地址
  netty.rpc.host=127.0.0.1
  #rpc 端口
  netty.rpc.port=8888
  #服务id
  netty.rpc.serviceId=service_test
  #注册中心地址
  netty.rpc.registry.url=http://127.0.0.1:9000
    ```  
   service层代码添加注解@RpcService
   ```java
    @RpcService(IHelloService.class)
    @Service
    public class HelloServiceImpl implements IHelloService {
    
        private Logger logger = LoggerFactory.getLogger(this.getClass());
        @Override
        public String hello(String name) {
            logger.info("----------------接收："+name);
            return "Hello! " + name;
        }
    
        @Override
        public int sum(int one, int two) {
            logger.info("----------------接收："+ one + "+" + two);
            //Uninterruptibles.sleepUninterruptibly(3 , TimeUnit.SECONDS);
            return one + two;
        }
    }
  ```
   netty服务启动
   ```java
  package com.llq.web;
  
  import com.llq.netty.server.RpcServer;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  @SpringBootApplication
  public class ServiceWebApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(ServiceWebApplication.class, args);
  
          RpcServer rpcServer = new RpcServer();
          rpcServer.startAsync();
      }
  
  }
  ```
* ##### [客户端](https://github.com/lvlq73/demo/tree/main/client-demo)
    pom.xml引入netty客户端依赖和service-api依赖
   ```xml
  <dependencies>
   <dependency>
      <groupId>com.llq.netty</groupId>
      <artifactId>netty-client</artifactId>
  </dependency>
   <dependency>
      <groupId>com.llq</groupId>
      <artifactId>service-api</artifactId>
      <version>${project.parent.version}</version>
    </dependency>  
  </dependencies> 
    ```
  resource文件下新建netty-rpc.properties配置文件
     ```properties
      #注册中心地址
      netty.rpc.registry.url=http://127.0.0.1:9000
     ``` 
  service-api接口代码添加注解@RpcApi
  ```java
  @RpcApi(alias = "helloService", serviceId = "service_test")
  public interface IHelloService {
  
      String hello(String name);
  
      int sum(int one, int two);
  }
  ```
  客户端代码
  ```java
  @SpringBootApplication
  public class ClientWebApplication {
  
      @Bean
      public ApiSpringRegister apiSpringRegister() {
            //扫描service-api（扫描要调用的服务接口）
            return new ApiSpringRegister(Arrays.asList("com.llq.api"));
      }
  
      public static void main(String[] args) {
          SpringApplication.run(ClientWebApplication.class, args);
      }
  
  }
  
  ```


