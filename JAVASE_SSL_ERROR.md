
继续常规更新，修正到了coinmarketcap的新api，然后把bitcoin的基础功能也拿了过来，这样以后开启服务时，就能自动交易收发真实的bitcoin了（设定上ripplepower也可以当网关服务器用，我已经内置netty了，到时会把网页和相关服务部分加进去，就能一键开启网关运营模式在线运行）。反正ssl的问题不是不能破，只是java官方不更新的话，我可能得附带50MB左右的支持库（多版本的mono），以满足不同系统需要……

写完这个实装高频交易（HFT），然后整合下功能，优化下界面.

然后就是最关键的问题:

java link to the geotrust ssl ca - g3 exception

一直等着oracle修正java对4096位ssl协议的访问bug，但是等到了u51，却发现问题依旧，实在是无语了，Prime size must be multiple of 64, and can only range from 512 to 2048 (inclusive)这个简单的问题，这么久没人解决。

这个问题已经出现好几个月了（以前倒无所谓，现在ripple在线服务开始大量使用会导致java无法访问的4096bit ssl协议，开发新功能受限……）。

我已经再次向http://bugreport.java.com/报了SunJCE的2048bit限制问题（4个月前就报过，竟然到今天还没人修正，是没人用java了还是怎么滴，可是redhat那边却早就修复了，我一直以为oracle这边也会修复，没想到没人管……），导致访问geotrust ssl ca - g3协议时，会因为使用4096bit而异常的bug(本来ca用g2版则没事，可是ripple现在大量采用g3，只有少数还是g2……)，不过oracle大家都懂，首先未必看，其次看了也未必短期会解决，大家有时间的话可以帮着小弟催下（提交同类bug给他们），反正小弟已经尽力了……(虽然我可以自己用修正过的openjdk，但程序附带jdk体积有点大，不到最后一步不想这么办|||（最主要是多平台太麻烦了，还得搞三、四个版本的修改版openjdk出来 PS:亲测直接给官方jre打补丁也不行，因为需要修改的数据，在oracle提供的sunjce_provider.jar中，而这个jar偏偏附带class验证，修改的class替换过去直接无效，只能整个换成自己编译的openjdk……）)

另外，我也尝试过在java环境下，用jruby和jython以及groovy、scala这些较独立的第三方语言（以及其支持库）中的https封装进行网页数据读取，遗憾的是，他们访问https网站时，核心部分无一例外也会引用到SunJCE(也就是他们使用了sunjce_provider.jar里面的实现)，所以没用……（需要一个单独的java第三方ssl解析实现才能破，但是没找到，就连tomcat、jboss这些服务器都没有完整的自己的ssl解析封装，仅仅只有扩展java原有的，也是醉了，使用openssl的c++版本的话，又得重新封装很多类，等于重写一个jce-provider，开发量太大。唯一可破的就是上c#那边的mono跨平台支持库了，实在不行就用这玩意了……）

明明redhat的openjdk已经修正了此问题，但是官方的openjdk和标准jdk居然都没有修正（至少u51没有），其实就几行代码的事，但是oracle不改则所有标准java环境就都犯一个毛病，这月是没戏了，也不知道8月份oracle能不能在u60里修正，无语。

redhat的修正见此:

https://bugzilla.redhat.com/attachment.cgi?id=1012238&action=diff

具体来说，此bug不修正，Ripple网站服务中

使用协议 geotrust ssl ca - g2 通过java可以编程访问，因为是2048bit的 （比如：https://api.ripple.com/，还是g2版本）

使用协议 geotrust ssl ca - g3 通过java编程将不可以访问，会报错，因为目前的sunjce实现不处理2048bit以上的数据，而这个4096bit （比如：https://staging.validators.ripple.com，ripple新的在线服务基本都是g3了）

代码访问不了ca-g3的话，ripple提供的部分在线服务对java桌面版来说(PS:android版无此问题,万恶的oracle)也就悲剧了(主要是无法连接id服务，意味着我不能调取你的在线资料，也就无法获得你的私钥，让你通过账户名使用这个客户端)，这个客户端功能也就变得不完整了（核心部分不受影响，毕竟rippled走wss，而非https，直接使用私钥完全没问题，只是ripple提供的部分辅助类功能无法使用）……

