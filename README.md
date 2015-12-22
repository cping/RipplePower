# RipplePower-Ripple-Client(Ripple local wallet)

#2015-12-19

正在整理代码，开始继续搞客户端，Java的PC版完整工程及需要的jar都在eclipse包下，直接导入项目（同时导入那两个）既可开发。另外，最新那边Ripple开了C#支持库的坑，所以明年我也开C#版客户端……

C#版支持库地址：https://github.com/ripple/ripple-dot-net/

官方的本地钱包源码在此：https://github.com/ripple/ripple-client-desktop

另外推荐两个第三方的Ripple应用:

在线交易情况，推荐使用heartbit开发的(此站2016年也会开放在线钱包): http://heartbit.io/app
在线钱包，推荐使用gatehub.net的: https://gatehub.net/

PS: Ripple官方建议迁移到gatehub不是没道理的，因为此站确实比Ripple官方的钱包实用和安全，比如我曾经尝试多代理IP穷举此站ID，结果几次访问后测试的存在ID立刻被封，并且要求邮件确认解封（而Ripple官方在线钱包多IP是可以穷举的，只是慢点）。当然，更主要的就是交易责任的转移，美帝对于“类似洗钱”的行为限制太多，大家都懂的，Ripple官方在线钱包要求实名后本来就没什么人用了，而前端转到gatehub就没Ripple官方的事情，可以专心搞后台……
___________

RipplePower is a Ripple(Rippled Server) API Client for the Java language licensed under Apache License 2.0

RipplePower core includes software from the Ripple-lib-java to parse Ripple-JSON-RPC response from the any Rippled API. You can see the open source code at https://github.com/ripple/ripple-lib-java

# Download RipplePower

Desktop Version [Demo](https://github.com/cping/RipplePower/releases/download/0.1.3/demo-0.1.3.zip)

Android Version (work in progress)

IOS Version (work in progress)

Please see [XRPMoon.com](http://www.xrpmoon.com/blog) for downloads, build and installation instructions and other documentation.

# Ripple Desktop Client (100% Java)

RipplePower is a Powerful Ripple desktop client ,It can run in HTML5 browsers (via GWT), desktop(JavaSE), Android, iOS and WP.

In the page right [Download ZIP] option, you can download the entire project source code and Demo files.

在Github页面右侧下方【Download ZIP】选项，您可以下载整个项目源码与Demo文件.

#Download Java Runtime Platform

下载Java程序运行环境

http://www.java.com

#Donation

<a href="https://ripple.com//send?to=rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp&name=cping&label=Thank you donate to RipplePower&amount=100/XRP&dt=20140906"><img src="https://raw.github.com/cping/RipplePower/master/rippledonate.png" alt="RippleDonate" /></a>

#Base Design

![RipplePower](https://raw.github.com/cping/RipplePower/master/base_en.png "base")

![RipplePower](https://raw.github.com/cping/RipplePower/master/base.png "base")

#Screenshot

![RipplePower](https://raw.github.com/cping/RipplePower/master/001.png "0")
![RipplePower](https://raw.github.com/cping/RipplePower/master/002.png "1")
![RipplePower](https://raw.github.com/cping/RipplePower/master/003.png "2")
![RipplePower](https://raw.github.com/cping/RipplePower/master/004.png "3")

#Rippled Path

https://github.com/ripple/rippled

PS : This is the repository for Ripple's rippled, reference P2P server.

#RipplePower License

License : http://www.apache.org/licenses/LICENSE-2.0

