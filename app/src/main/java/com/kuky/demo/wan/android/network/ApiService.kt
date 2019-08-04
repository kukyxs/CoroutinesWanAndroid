package com.kuky.demo.wan.android.network

import com.kuky.demo.wan.android.entity.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author kuky.
 * @description
 */
interface ApiService {

    // ===============================>
    // 首页文章
    @GET("/article/list/{page}/json")
    suspend fun homeArticles(@Path("page") page: Int): HomeArticleEntity

    // 首页 Banner
    @GET("/banner/json")
    suspend fun homeBanner(): HomeBannerEntity

    // 首页第二个 Banner.同项目分类功能重复
    @Deprecated("同项目分类功能重复，勿调用")
    @GET("/article/listproject/{page}/json")
    suspend fun homeProject(@Path("page") page: Int): ProjectDetailEntity

    // 常用网站
    @GET("/friend/json")
    suspend fun commonlyUsedWebsite(): WebsiteEntity

    // 热词搜索
    @GET("/hotkey/json")
    suspend fun hotKeys(): HotKeyEntity

    // 置顶文章
    @GET("/article/top/json")
    suspend fun topArticle(@Header("Cookie") cookie: String): TopArticleEntity

    // ==============================>
    // 体系
    @GET("/tree/json")
    suspend fun knowledgeSystem(): AndroidSystemEntity

    /**
     * 体系下的文章，cid 查看 [SystemCategory] #id
     * 返回数据同首页文章列表
     */
    @GET("/article/list/{page}/json")
    suspend fun articleInCategory(@Path("page") page: Int, @Query("cid") cid: Int, @Header("Cookie") cookie: String): WxChapterList

    // 项目分类
    @GET("/project/tree/json")
    suspend fun projectCategory(): ProjectCategoryEntity

    /**
     * 返回项目分类下的所有项目列表，cid 查看 [ProjectCategoryData] #id
     */
    @GET("/project/list/{page}/json")
    suspend fun projectList(@Path("page") page: Int, @Query("cid") cid: Int, @Header("Cookie") cookie: String): ProjectDetailEntity

    // ================================>
    // 登录
    @POST("/user/login")
    @FormUrlEncoded
    suspend fun login(@Field("username") username: String, @Field("password") password: String): Response<WanUserEntity>

    // 注册
    @POST("/user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String, @Field("password") password: String,
        @Field("repassword") repassword: String
    ): Response<WanUserEntity>

    // 退出
    @GET("/user/logout/json")
    suspend fun loginout(): BasicResultData

    // ===============================>
    // 收藏文章列表
    @GET("/lg/collect/list/{page}/json")
    suspend fun userCollectedArticles(@Path("page") page: Int, @Header("Cookie") cookie: String): UserCollectEntity

    // 收藏文章，项目
    @POST("/lg/collect/{id}/json")
    suspend fun collectArticleOrProject(@Path("id") id: Int, @Header("Cookie") cookie: String): BasicResultData

    // 取消收藏，文章列表
    @POST("/lg/uncollect_originId/{articleId}/json")
    suspend fun unCollectArticle(@Path("articleId") articleId: Int, @Header("Cookie") cookie: String): ResponseBody

    // 取消收藏，收藏列表
    @POST("/lg/uncollect/{articleId}/json")
    @FormUrlEncoded
    suspend fun unCollectCollection(
        @Path("articleId") articleId: Int, @Field("originId") originId: Int,
        @Header("Cookie") cookie: String
    ): BasicResultData

    // 收藏网站列表
    @GET("/lg/collect/usertools/json")
    suspend fun collectWebsiteList(@Header("Cookie") cookie: String): WebsiteEntity

    // 收藏网站
    @POST("/lg/collect/addtool/json")
    @FormUrlEncoded
    suspend fun addWebsite(
        @Field("name") name: String, @Field("link") link: String,
        @Header("Cookie") cookie: String
    ): BasicResultData

    // 编辑收藏网址
    @POST("/lg/collect/updatetool/json")
    @FormUrlEncoded
    suspend fun editWebsite(
        @Field("id") id: Int, @Field("name") name: String,
        @Field("link") link: String, @Header("Cookie") cookie: String
    ): ResponseBody

    // 删除收藏的网址
    @POST("/lg/collect/deletetool/json")
    @FormUrlEncoded
    suspend fun deleteWebsite(@Field("id") id: Int, @Header("Cookie") cookie: String): BasicResultData

    // ===============================>
    // 搜索文章
    @POST("/article/query/{page}/json")
    @FormUrlEncoded
    suspend fun searchArticle(@Path("page") page: Int, @Field("k") keyword: String): HomeArticleEntity

    // 公众号列表
    @GET("/wxarticle/chapters/json")
    suspend fun wxCahpters(): WxChapterEntity

    // 查看某个公众号历史数据
    @GET("/wxarticle/list/{wxid}/{page}/json")
    suspend fun wxChapterList(
        @Path("wxid") wxid: Int,
        @Path("page") page: Int,
        @Header("Cookie") cookie: String
    ): WxChapterList

    // 公众号历史数据，k 传空字符则全部记录
    @GET("/wxarticle/list/{wxid}/{page}/json")
    suspend fun chapterHistoryList(
        @Path("wxid") wxid: Int, @Path("page") page: Int,
        @Query("k") keyword: String
    ): TopArticleEntity

    // ===============================>
    /**
     * 获取所有的待办列表
     * @param page 页码，从 1 开始
     * @param cookie 登录后保存的 Cookie，通过 [com.kuky.demo.wan.android.data.PreferencesHelper] #fetchCookie 方法获取
     * @param param 参数设置如下：
     *      status 状态， 1-完成；0未完成; 默认全部展示；
     *      type 创建时传入的类型, 默认全部展示
     *      priority 创建时传入的优先级；默认全部展示
     *      orderby 1:完成日期顺序；2.完成日期逆序；3.创建日期顺序；4.创建日期逆序(默认)
     *
     * @return 查询接口后替换 ResponseBody
     */
    @GET("/lg/todo/v2/list/{page}/json")
    suspend fun fetchTodoList(
        @Path("page") page: Int, @Header("Cookie") cookie: String,
        @QueryMap param: HashMap<String, Int>
    ): TodoEntity

    /**
     * 新增一条待办
     * @param param 参数设置如下：
     *      title: 新增标题（必须）
     *      content: 新增详情（必须）
     *      date: 2018-08-01 预定完成时间（不传默认当天，建议传）
     *      type: 大于0的整数（可选）用于，在app 中预定义几个类别：例如 工作1，生活2，娱乐3，新增的时候传入0，1，2 查询的时候，传入type 进行筛选
     *      priority 大于0的整数，在app 中预定义几个优先级：重要（1），一般（2）等，查询的时候，传入priority 进行筛选
     *
     * @param cookie 登录后保存的 Cookie，通过 [com.kuky.demo.wan.android.data.PreferencesHelper] #fetchCookie 方法获取
     */
    @POST("/lg/todo/add/json")
    @FormUrlEncoded
    suspend fun addTodo(@FieldMap param: HashMap<String, Any>, @Header("Cookie") cookie: String): BasicResultData

    /**
     * 更新一条待办
     * @param id 更新待办 id
     * @param cookie 登录后保存的 Cookie，通过 [com.kuky.demo.wan.android.data.PreferencesHelper] #fetchCookie 方法获取
     * @param param 参数设置如下：
     *      title: 更新标题 （必须）
     *      content: 新增详情（必须）
     *      date: 2018-08-01（必须）
     *      status: 0 // 0为未完成，1为完成
     *      type: 大于0的整数（可选）用于，在app 中预定义几个类别：例如 工作1，生活2，娱乐3，新增的时候传入0，1，2 查询的时候，传入type 进行筛选
     *      priority 大于0的整数，在app 中预定义几个优先级：重要（1），一般（2）等，查询的时候，传入priority 进行筛选
     */
    @POST("lg/todo/update/{id}/json")
    @FormUrlEncoded
    suspend fun updateTodo(
        @Path("id") id: Int, @Header("Cookie") cookie: String,
        @FieldMap param: HashMap<String, Any>
    ): BasicResultData

    /**
     * 仅更新待办状态
     * @param id 更新待办 id
     * @param status 1 完成 0 未完成
     * @param cookie 登录后保存的 Cookie，通过 [com.kuky.demo.wan.android.data.PreferencesHelper] #fetchCookie 方法获取
     */
    @POST("lg/todo/done/{id}/json")
    @FormUrlEncoded
    suspend fun updateTodoState(
        @Path("id") id: Int, @Field("status") status: Int,
        @Header("Cookie") cookie: String
    ): BasicResultData

    // 删除一条待办
    @POST("/lg/todo/delete/{id}/json")
    suspend fun deleteTodo(@Path("id") id: Int, @Header("Cookie") cookie: String): BasicResultData
}