![](https://img.shields.io/badge/JDK-1.8+-success.svg)
![](https://maven-badges.herokuapp.com/maven-central/com.lzhpo/logger/badge.svg?color=blueviolet)
![](https://img.shields.io/:license-Apache2-orange.svg)
[![Style check](https://github.com/lzhpo/logger/actions/workflows/style-check.yml/badge.svg)](https://github.com/lzhpo/logger/actions/workflows/style-check.yml)

## 开源地址

- GitHub：[https://github.com/lzhpo/logger](https://github.com/lzhpo/logger)
- Gitee：[https://gitee.com/lzhpo/logger](https://gitee.com/lzhpo/logger)

## 如何使用？

*logger同时支持SpringBoot2和SpringBoot3*

### 1.导入依赖

> 依赖已发布至Maven中央仓库，可直接引入依赖。

- Maven：

  ```xml
  <dependency>
    <groupId>com.lzhpo</groupId>
    <artifactId>logger</artifactId>
    <version>${latest-version}</version>
  </dependency>
  ```
- Gradle:
  ```groovy
  implementation 'com.lzhpo:logger:${latest-version}'
  ```

### 2.使用注解

`@Logger`注解:
- condition: 生成日志的条件，true 或 false，支持 SpringEL 表达式。
  ```java
  // 获取返回结果中的成功标志
  condition = "#result.getSuccess()"
  ```
- message: 日志内容，支持 SpringEL 表达式。
  ```java
  // 从数据库中查询用户名称和产品名称，结果示例：小刘使用支付宝下单了ABC产品
  message = "#findUserName(#request.getUserId()) + '使用' + #request.getPaymentType() + '下单了' + #findProductName(#request.getProductId()) + '产品'"
  
  // 从数据库中查询用户名称和地址，结果示例：小刘将地址从Jiangxi修改为Guangzhou
  message = "#findUserName(#request.getUserId()) + '将地址从' + #findOldAddress(#request.getOrderId()) + '修改为' + #findNewAddress(#request.getAddressId())"
  ```
- operatorId: 日志关联的操作人，支持 SpringEL 表达式。
  ```java
  // 从数据库中查询用户名称
  operatorId = "#findUserName(#request.getUserId())"
  ```
- bizId: 日志关联的业务编号，支持 SpringEL 表达式。
  ```java
  bizId = "#getBusinessId(#result.orderId)"
  ```
- category: 日志关联的类型，支持 SpringEL 表达式。
  ```java
  // 可以使用纯字符串或SpringEL表达式，自由发挥
  category = "'Operation Log'"
  ```
- tag: 日志关联的标签，支持 SpringEL 表达式。
  ```java
  // 可以使用纯字符串或SpringEL表达式，自由发挥
  tag = "'Create Order'"
  ```
- additional: 日志额外的信息，可自由发挥，支持 SpringEL 表达式。
  ```java
  // 从数据库中查询用户名称和会员等级
  additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
  ```

简单示例演示：
```java
@PostMapping("/orders")
@Logger(
    condition = "#result.getSuccess()",
    category = "'Operation Log'",
    tag = "'Create Order'",
    bizId = "#getBusinessId(#result.orderId)",
    operatorId = "#findUserName(#request.getUserId())",
    message = "#findUserName(#request.getUserId()) + '使用' + #request.getPaymentType() + '下单了' + #findProductName(#request.getProductId()) + '产品'",
    additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
)
public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
    // ...
}
```

`@LoggerComponent`: 用于标识一个组件是属于 logger 的。
`@LoggerFunction`: 用于支持在 `@Logger` 注解中自定义函数，函数名默认取 `@LoggerFunction` 注解下的方法名，方法必须是 static，同时函数名支持自定义，例如：`@LoggerFunction("findUserName")`

简单示例演示：
```java
@LoggerComponent
public class OrderRegisterFunction {

  @LoggerFunction
  public static String getBusinessId(String orderId) {
    return DigestUtil.sha256Hex(orderId);
  }

  @LoggerFunction("findUserName")
  public static String findUserName(String userId) {
    return "Jack";
  }

  @LoggerFunction
  public static String findUserVip(String userId) {
    return "VIP5";
  }

  @LoggerFunction
  public static String findProductName(String productId) {
    return "ABC";
  }

  @LoggerFunction
  public static String findOldAddress(String orderId) {
    return "Jiangxi";
  }

  @LoggerFunction
  public static String findNewAddress(String addressId) {
    return "Guangzhou";
  }
}
```

## 公众号

|         微信          |            公众号             |
|:-------------------:|:--------------------------:|
| ![](./docs/images/微信.jpg) | ![](./docs/images/公众号.jpg) |

