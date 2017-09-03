为什么做这个工具？
--------
在移动端开发中，我们常常需要借助Charles（Windows上就用Fiddler）来抓包，通过查看Http请求的详细信息，帮助我们Debug。

在日常的使用当中，我发现为Android配置代理太麻烦：

* 首先我们要取得本机的IP：在macOS这边执行ifconfig，从输出的结果中找到本机的局域网IP地址。借助grep指令，可以快一点定位到我们要的那个IP。
* 然后再在Android设备上配置ip为代理服务器地址。需要从系统设置->WLAN→长按当前连上的Wifi→修改网络→往下滑到高级选项→代理改成手动→手动输入主机名和端口（手输入累死人）→保存。
* 当我们需要去掉代理配置，还是得去高级选项那儿，把代理改成无，也是很麻烦。

解决思路
--------
为了寻求简化的方法，我们拆解下配置代理的过程，发现其实就是这么回事儿：
1. macOS拿自己的ip地址：ifconfig可以命令输出设备信息，解析这段文本应当可以拿到本机ip地址
2. 把这个IP给Android：可以用adb来实现，它有很多种方法，可以在macOS和Android直接传递数据，挑一种合适的就行
3. Android以这个IP为代理：包括OkHttp在内的各种Http库，都可以手动配置自己的Proxy

因此，我们可以做一个命令行小工具解决这个问题！

Android App的集成工作
--------
Android这边需要添加kidding-lib库的依赖：
```groovy
dependencies {
    compile 'com.github.realxu:kidding-lib:v0.1'
}
```
这个库托管在jitpack上，需要在项目的build.gradle里添加jitpack的源
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在代码中，你需要调用start方法开启Kidding，时机自己定：
```java
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Kidding.instance().start(this);
    }
}
```

也建议在App退到后台时调用stop停用Kidding，但要记得回来时重新start！

在创建OkHttp对象时，需要指定Kidding的ProxySelector：
```java
OkHttpClient client = new OkHttpClient.Builder()
        .proxySelector(KiddingProxySelector.instance())
        .build();
```

kid终端工具的安装和使用
--------
kid终端工具使用Golang编写，编译安装建议使用Go 1.8或者更高版本。
```bash
brew install go
```

下载源代码，编译
```bash
git clone https://github.com/realxu/kidding.git
cd kidding/console
export GOPATH=`pwd`
cd src/kid
go build
```

你可以把可执行文件kid复制到$PATH下面的某个路径，比如：
```bash
cp kid /usr/local/bin
```

现在，你可以把手机连上USB，启动App，在macOS的终端输入kid命令
```bash
kid
# output may be
# 1: 192.168.1.159
# 2: 192.168.57.1
# choose 192.168.1.159 automaticly
# Broadcasting: Intent { act=kidding.command (has extras) }
# Broadcast completed: result=0
```

手机上即可看到Toast提示，"代理服务器已经改变：192.168.1.159"，后续OkHttp将会尝试走这个代理访问网络。

像上面那样有多个备选地址时，可以用choose选项指定：
```bash
kid choose 2
```

不再需要代理时，用clear选项清空
```bash
kid clear
```
