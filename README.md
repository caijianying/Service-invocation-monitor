# Service Invocation Monitor
* 还在使用StopWatch在代码里到处埋点？这是一款轻量级的0侵入式链路监控的Idea插件，可以监控Junit、SpringBoot应用的调用链，统计接口方法耗时等，后续还会支持其他性能指标。
* 平时开发中可能会很少关注到代码的性能，希望在平时的开发中，能帮助大家关注代码的性能，提升代码质量。

## 快速开始
* 下载完插件后，重启Idea。像平常一样，项目启动即可。
* 也可以直接通过我的Demo项目快速上手 https://github.com/caijianying/Service-invocation-monitor-demo
* 看效果
 ![image](https://user-images.githubusercontent.com/25894814/192182495-b2962d10-a719-4e36-b153-d3c867417028.png)

## 启动参数配置
|  启动命令   |  是否必填  |参数说明  | 默认值  |
|  ----  | ----  | ----  | ----  |
| `-Dmonitor.package`  | 非必填 | 监控的包名 | 默认取启动类所在包名 |
| `-Dmonitor.timeCost.threshold` | 非必填 | 判断高耗时的阀值，高于阀值，标记为红色。单位为毫秒 | 100 |

### 小Tips
* 关于插件更多的详细介绍，可以查看 https://juejin.cn/post/7122055033906003981/ ，里面记录着插件从无到有的产生过程 
* 若觉得有方便到您，欢迎Star哦.若有建议，请提Issue.大家共同进步。
