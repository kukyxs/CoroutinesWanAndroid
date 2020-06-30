### 大概介绍
该项目基于「[玩 Android 接口](https://www.wanandroid.com/blog/show/2)」接口, 整体采用 MVVM, Android Jetpack, Retrofit, Kotlin 协程进行编写。
该项目是和大佬 [Taonce](https://github.com/Taonce) 一起编写完成, 目前已完成所有的开发功能。

### update 
#### 2020-06-30 更新日志: `paging2` 升级到 `paging3`; `livedata` 大部分替换成 `fow`; 开启暗黑模式; 优化代码结构


### 目前已完成功能
- 首页最新博文
- 首页项目分类
- 首页学习体系
- 首页公众号
- 公众号文章列表
- 文章详情查看
- 我的收藏
- 我的待办
- 搜索
- 广场分享文章列表
- 文章分享/删除功能
- 积分功能

### 目前存在问题
- `DrawerLayout` 的 `menu.xml` 无法通过 `DataBinding` 处理, 只能通过普通方式处理, 如果有解决方案请提 issue
- `Paging3` 踩坑阶段, 后期会有问题添加..

### 特此感谢（排名不分先后）
- [玩 Android 洋神](https://www.wanandroid.com/)
- [Kotlin](https://github.com/JetBrains/kotlin)
- [AndroidX](https://developer.android.com/jetpack/androidx)
- [Retrofit](https://github.com/square/retrofit)
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [Glide](https://github.com/bumptech/glide)
- [Banner](https://github.com/youth5201314/banner)
- [FlexboxLayout](https://github.com/google/flexbox-layout)
- [FloatingActionButton](https://github.com/Clans/FloatingActionButton)
- [anko](https://github.com/Kotlin/anko)

### 支持一下
如果该项目对你有帮助, 请在右上角帮我们 **star** 一下。如果有什么问题, 可以直接提 **issue**

### 体验下载
[在此点击下载体验包](https://github.com/kukyxs/CoroutinesWanAndroid/releases/download/2.0.0/wan_2.0.apk)

### 附上效果图：
<div align="center">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E5%85%A5%E5%8F%A3%E9%A1%B5.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E9%A6%96%E9%A1%B5-%E6%9C%80%E6%96%B0%E5%8D%9A%E6%96%87.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E9%A6%96%E9%A1%B5-%E9%A1%B9%E7%9B%AE%E5%88%86%E7%B1%BB.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E9%A6%96%E9%A1%B5-%E5%AD%A6%E4%B9%A0%E4%BD%93%E7%B3%BB.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E9%A6%96%E9%A1%B5-%E5%85%AC%E4%BC%97%E5%8F%B7.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E5%85%AC%E4%BC%97%E5%8F%B7%E6%96%87%E7%AB%A0%E5%88%97%E8%A1%A8.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%96%87%E7%AB%A0%E8%AF%A6%E6%83%85.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E9%A6%96%E9%A1%B5-%E4%BE%A7%E6%A0%8F.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E6%94%B6%E8%97%8F-%E6%96%87%E7%AB%A0.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E6%94%B6%E8%97%8F-%E7%BD%91%E5%9D%80.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E5%BE%85%E5%8A%9E-%E5%88%97%E8%A1%A8.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E5%BE%85%E5%8A%9E-%E7%AD%9B%E9%80%89.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E5%BE%85%E5%8A%9E-%E7%BC%96%E8%BE%91.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%88%91%E7%9A%84%E5%BE%85%E5%8A%9E-%E6%96%B0%E5%BB%BA.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%90%9C%E7%B4%A2-%E5%8E%86%E5%8F%B2%E8%AE%B0%E5%BD%95.png" width="316">
    <img src="https://github.com/kukyxs/CoroutinesWanAndroid/blob/master/snapshots/%E6%90%9C%E7%B4%A2-%E7%BB%93%E6%9E%9C%E5%88%97%E8%A1%A8.png" width="316">
</div>
