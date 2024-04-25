![](https://img.shields.io/badge/JDK-1.8+-success.svg)
![](https://maven-badges.herokuapp.com/maven-central/com.lzhpo/logger-spring-boot-starter/badge.svg?color=blueviolet)
![](https://img.shields.io/:license-Apache2-orange.svg)
[![Style check](https://github.com/lzhpo/logger-spring-boot-starter/actions/workflows/style-check.yml/badge.svg)](https://github.com/lzhpo/logger-spring-boot-starter/actions/workflows/style-check.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b7b948873ebf40b4be396fe7f0483a97)](https://app.codacy.com/gh/lzhpo/logger-spring-boot-starter/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

## 开源地址

- GitHub：[https://github.com/lzhpo/logger-spring-boot-starter](https://github.com/lzhpo/logger-spring-boot-starter)
- Gitee：[https://gitee.com/lzhpo/logger-spring-boot-starter](https://gitee.com/lzhpo/logger-spring-boot-starter)

## 前言

> 操作日志在一个系统中占据着举足轻重的位置，记录操作日志的方式也五花八门，但应讲究可读性、复杂场景支持、业务代码解耦，不侵入业务代码，以保持我们业务代码的整洁。

## 如何使用？

*logger同时支持SpringBoot2和SpringBoot3*

### 1.导入依赖

> 依赖已发布至Maven中央仓库，可直接引入依赖。

- Maven：

  ```xml
  <dependency>
    <groupId>com.lzhpo</groupId>
    <artifactId>logger-spring-boot-starter</artifactId>
    <version>${latest-version}</version>
  </dependency>
  ```
- Gradle:
  ```groovy
  implementation 'com.lzhpo:logger-spring-boot-starter:${latest-version}'
  ```

### 2.使用注解

#### 2.1 `@Logger`注解

_`@Logger` 注解已经支持在 IDEA 中自动 SpringEL 表达式高亮并且自动提示。_

`@Logger`注解解释：
- condition: 生成日志的条件，非必需，true 或 false，支持 SpringEL 表达式。
  ```java
  // 获取返回结果中的成功标志
  condition = "#result.getSuccess()"
  ```
- message: 日志内容，必需，支持 SpringEL 表达式。
  ```java
  // 从数据库中查询用户名称和产品名称，结果示例：小刘使用支付宝下单了ABC产品
  message = "#findUserName(#request.getUserId()) + '使用' + #request.getPaymentType() + '下单了' + #findProductName(#request.getProductId()) + '产品'"
  
  // 从数据库中查询用户名称和地址，结果示例：小刘将地址从Jiangxi修改为Guangzhou
  message = "#findUserName(#request.getUserId()) + '将地址从' + #findOldAddress(#request.getOrderId()) + '修改为' + #findNewAddress(#request.getAddressId())"
  ```
- operatorId: 日志关联的操作人，非必需，支持 SpringEL 表达式。也可以实现`OperatorAware`接口的`getCurrentOperatorId`方法进行获取。 如果既在注解传入了operatorId，又实现了`OperatorAware`接口，则优先取注解中的。
  ```java
  // 从数据库中查询用户名称
  operatorId = "#findUserName(#request.getUserId())"
  ```
- businessId: 日志关联的业务编号，非必需，支持 SpringEL 表达式。
  ```java
  businessId = "#getBusinessId(#result.getOrderId())"
  ```
- category: 日志关联的类型，非必需，支持 SpringEL 表达式。
  ```java
  // 可以使用纯字符串或SpringEL表达式，自由发挥
  category = "'Operation Log'"
  ```
- tag: 日志关联的标签，非必需，支持 SpringEL 表达式。
  ```java
  // 可以使用纯字符串或SpringEL表达式，自由发挥
  tag = "'Create Order'"
  ```
- prelude: 日志在业务代码执行前解析还是在执行后解析（true：执行前解析；false：执行后解析），非必须，默认为 false。若为 true，在注解中的表达式无法使用 result 变量。
- returning: 是否需要返回业务代码执行结果，非必须，默认为true。
- additional: 日志额外的信息，非必需，可自由发挥，支持 SpringEL 表达式。
  ```java
  // 从数据库中查询用户名称和会员等级
  additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
  ```

简单示例演示：

_详细示例可看Junit测试用例_

```java
@PostMapping("/orders")
@Logger(
    condition = "#result.getSuccess()",
    category = "'Operation Log'",
    tag = "'Create Order'",
    businessId = "#getBusinessId(#result.getOrderId())",
    operatorId = "#findUserName(#request.getUserId())",
    message = "#findUserName(#request.getUserId()) + '使用' + #request.getPaymentType() + '下单了' + #findProductName(#request.getProductId()) + '产品'",
    additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
)
public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
    // ...
}
```

#### 2.2 自定义函数注解

- `@LoggerComponent`: 用于标识一个组件是属于 logger 的。
- `@LoggerFunction`: 用于支持在 `@Logger` 注解中自定义函数，需要注意的是方法必须是 static，函数名默认取 `@LoggerFunction` 注解下的方法名，函数名支持自定义，例如：`@LoggerFunction("findUserName")`

简单示例演示：
```java
@LoggerComponent
public class OrderRegisterFunction {

  @LoggerFunction
  public static String findProductName(String productId) {
    // ...
  }
}
```

#### 2.3 异步监听日志事件

日志解析完毕之后会发布一个 `LoggerEvent` 事件，可以自定义 Listener 进行处理。

例如：
```java
@Slf4j
@Component
public class LoggerEventListener {

    @Async
    @EventListener
    public void process(LoggerEvent event) {
        log.info("Received LoggerEvent: {}", event);
        log.info(event.getMessage());
    }
}
```

`LoggerEvent` 字段解释:
- logId: 日志编号。
- message: 日志内容。
- operatorId: 操作人编号。
- businessId: 业务编号。
- category: 日志分类。
- tag: 日志标签。
- additional: 日志额外信息。
- createTime: 日志创建时间。
- takeTime: 业务方法耗时，单位：毫秒。
- result: 业务方法执行结果。
- success: 业务方法是否执行成功。
- errors: 业务方法执行期间发生的异常。
- diffResults: 对象diff的结果。

#### 2.4 对象 diff

对象 diff 的意思就是给两个对象，找出它们的区别。

示例：
```java
// 相同对象diff
@Logger(message = "#DIFF(#oldUser, #newUser)")
public void userDiff(User oldUser, User newUser) {
    // NOP
}

// 不同对象diff
@Logger(message = "#DIFF(#admin, #user)")
public void adminUserDiff(Admin admin, User user) {
  // NOP
}
```

其中，`DIFF` 是内置的函数，它会返回字符串形式的 diff 格式化结果，如有多个 diff 结果可设置指定的字符进行分隔。
`DIFF` 的结果会放在 `LoggerEvent` 的 `diffResults` 字段，可以在 `LoggerEvent` 的监听器里面进行处理。

支持自定义模板和分隔符：
```yml
logger:
  diff:
    delimiter: ", "
    template: "[{filedName}] has been updated from [{oldValue}] to [{newValue}]"
```

同时 diff 也支持排除指定对象或字段，或者设置 diff 字段的标题。

```java
// 排除此对象diff
@LoggerDiffObject(disabled = true)
public class UserWithDisabledObject {

    private String username;
    
    private String email;
}
```

```java
public class UserWithDisabledField {

    // 排除此字段diff
    @LoggerDiffField(disabled = true)
    private String username;
    
    private String email;
}
```

```java
public class UserWithTitle {

    // 设置字段的标题
    @LoggerDiffField(title = "用户名称")
    private String username;

    // 设置字段的标题
    @LoggerDiffField(title = "用户年龄")
    private Integer age;
}
```

#### 2.5 关于`@Logger`注解在IDEA设置SpringEL的提示

`@Logger` 注解中的属性已经支持在 IDEA 中自动有 SpringEL 的提示，无需手动设置。

## 公众号

|         微信          |            公众号             |
|:-------------------:|:--------------------------:|
| ![](./docs/images/微信.jpg) | ![](./docs/images/公众号.jpg) |

