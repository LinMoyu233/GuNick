# GuNick
## 这是什么？
一个简单的Minecraft Bukkit匿名/Nick插件，使用MySQL进行不同子服之间的数据互通。

并使用[ModernDisguise](https://github.com/iiAhmedYT/ModernDisguise)进行匿名。

代码写的很烂，reload指令并不彻底。蹲个野生大佬佬提PR改进QAQ。

笔者没有时间精力，有BUG能尽量自己修改就自己改，DeepSeek还可以。

## 实现的功能
1. 匿名/取消匿名
2. 数据库匿名同步
3. 匿名后提示在线管理玩家真实昵称
4. 类Hypixel BookGUI
5. 类Hypixel ActionBar提供是否隐身/匿名中

![](https://image.linmoyu.top/20250419010214118.webp)

## 未来可能实现(?)
1. 指令查询当前子服在线匿名玩家
2. 指令查询匿名玩家真实昵称以及其最近?条匿名记录
3. 可自定义皮肤
4. 随机皮肤/匿名
5. BookGUI设置前缀可以自定义前缀
5. BookGUI可以设置后缀

不做皮肤是因为带的Mojang API太玄学了，境内经常down。

没时间折腾其他API了。[ModernDisguise](https://github.com/iiAhmedYT/ModernDisguise)的DisguiseProvider有带setSkin的。

## 为什么会有这个项目？

笔者从2021年始开了4年的Minecraft服务器，期间并没有发现什么好用的匿名插件。市面上的匿名插件有三种：
1. 提供PAPI变量，使用其他插件自行修改PlayerTag或PlayerList的名字
2. 在前者基础上新增了修改DisplayName
3. 拦截、修改发包

三者在一些非全原创的小游戏服上没有办法一劳永逸，

像BedWars1058这种自写lib，使用NMS获取名字，改了lib也不管用的，匿名就很头疼。
![](https://i1.hdslb.com/bfs/new_dyn/13a9810f31468041035a050f060775cc28525429.png)
[拦截发包会导致白名字，使用ProtocolLib处理会导致血量始终为0](https://www.bilibili.com/opus/889834992319856640)~~(当然，也可能是我太菜了)~~。

在[该issues](https://github.com/tomkeuper/BedWars2023/issues/328)中看到了个[NickAPI](https://www.spigotmc.org/resources/nickapi-1-8-8-1-20-2-1-21.26013/)。最开始使用[PandaSpigot](https://github.com/hpfxd/PandaSpigot)时的确可用，但后面测试换核心后不可用，于是发现了它打了个[神秘补丁](https://github.com/hpfxd/PandaSpigot/blob/master/patches/api/0012-Ability-to-change-PlayerProfile-in-AsyncPreLoginEven.patch)==。

再搜就发现了[ModernDisguise](https://github.com/iiAhmedYT/ModernDisguise)，它修改了NMS名字，实测十分好使，于是就写了这个插件圆梦。

注：因为一些原因，没有写加入服务器时异步查询，目测不影响性能。

但如果你有强迫症一直想异步的话，[这个删掉的功能可能对你有用](https://github.com/GuCraft-Network/GuNick/commit/57f10842613046a4f0aeebc6bc1e23db099a4d5a#diff-e54ee585444703c8fa5fe960ee470686b654bcda2ab0b8d8076822d5a451006e)。

## 引用的第三方库
- [HikariCP](https://mvnrepository.com/artifact/com.zaxxer/HikariCP)
- [ModernDisguise](https://github.com/iiAhmedYT/ModernDisguise)
- [SLF4J](http://www.slf4j.org/)
- [SignGUI](https://github.com/Rapha149/SignGUI)
- [BookApi](https://github.com/Meteor2333/BookApi)

使用[Nicky](https://www.spigotmc.org/resources/nicky.590/)做依赖的版本：[NickHandler](https://github.com/LinMoyu233/NickHandler)

感谢。至此落幕，笔者已经没有精力去重建一个Minecraft 小游戏群组服了。
