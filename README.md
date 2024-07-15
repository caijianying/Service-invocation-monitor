# Service Invocation Monitor

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/caijianying/Service-invocation-monitor/blob/main/LICENSE)
[![](https://img.shields.io/badge/Author-小白菜-orange.svg)](https://caijianying.github.com)
[![](https://img.shields.io/badge/version-V20240228-brightgreen.svg)](https://github.com/caijianying/Service-invocation-monitor)

# 介绍
* 这是一款基于SkyWalking的，轻量级的0侵入式链路监控的Idea插件，可以监控SpringBoot应用的调用链，统计接口方法耗时等。
* 平时开发中可能会很少关注到代码的性能，希望在平时的开发中，能帮助大家关注代码的性能，提升代码质量。

## 实现原理及关键技术
* JavaAgent的Attach机制
* IntelliJ Platform Plugin SDK

## 有了它，你可以
* 查看接口方法调用的耗时
* 高耗时链路高亮显示
* 支持配置启动命令，详见 [启动参数配置](https://github.com/caijianying/Service-invocation-monitor/blob/main/README.md#%E5%90%AF%E5%8A%A8%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE)

## 目前已支持
* MVC相关注解，@GetMapping、@PostMapping、@DeleteMapping、@PutMapping、@PatchMapping
* Spring注解 @Service
* Dubbo3.x 链路采集
* 跨线程调用链路采集，支持Runnable、Callable方式创建异步线程

## 快速开始
* 下载完插件后，重启Idea。像平常一样，项目启动即可。
* 也可以直接通过我的Demo项目快速上手 https://github.com/caijianying/Service-invocation-monitor-demo
* 看效果
 ![image](https://user-images.githubusercontent.com/25894814/211231932-563ad434-b436-4601-88ca-ed9848b92de4.png)
* 也可以通过独立窗口，查看历史链路
![image](https://user-images.githubusercontent.com/25894814/218969055-78695962-a2ae-4965-a345-5f51b41066f8.png)

* 支持idea插件参数配置：Preferences -> tools -> Service Invocation Monitor
![image](https://user-images.githubusercontent.com/25894814/218968373-4b34863f-854f-4a39-b6cd-8b0c5e397aa3.jpg)


## 启动参数配置
|  启动命令   |  是否必填  |参数说明  | 默认值  |
|  ----  | ----  | ----  | ----  |
| `-Dmonitor.package`  | 非必填 | 监控的包名 | 默认取启动类所在包名 |
| `-Dmonitor.timeCost.threshold` | 非必填 | 判断高耗时的阀值，高于阀值，标记为红色。单位为毫秒 | 100 |
| `-Dmonitor.sampleRate` | 非必填 | 采样率，精度为1/10000. 10000 means 100% sample in default. | 10000 |


### 小Tips
* 关于插件更多的详细介绍，可以查看 https://juejin.cn/post/7122055033906003981/ ，里面记录着插件从无到有的产生过程 
* 若觉得有方便到您，欢迎Star哦.若有建议，请提Issue.大家共同进步。
