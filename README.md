# Service Invocation Monitor
* 还在使用StopWatch在代码里到处埋点？这是一款轻量级的0侵入式链路监控的Idea插件，可以监控Junit、SpringBoot应用的调用链，统计接口方法耗时等，后续还会支持其他性能指标。
* 平时开发中可能会很少关注到代码的性能，希望在平时的开发中，能帮助大家关注代码的性能，提升代码质量。

## 快速开始
* 使用方式很简单，只需要添加一行VMoption即可，`-Dmonitor.package=你需要监控的包名`，若不设置，也不影响程序的正常运行。
* 也可以直接通过我的Demo项目快速上手 https://github.com/caijianying/Service-invocation-monitor-demo

### 小Tips
* 关于插件更多的详细介绍，可以查看我的技术文档，里面记录着插件从无到有的产生过程 https://juejin.cn/post/7122055033906003981/
* 若觉得有方便到您，欢迎Star哦.若有建议，请提Issue.大家共同进步。
