package com.kuky.demo.wan.android.network

import com.kuky.demo.wan.android.entity.*
import okhttp3.ResponseBody
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

    // 首页第二个 Banner
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
    suspend fun topArticle(): TopArticleEntity

    // ==============================>
    // 体系
    @GET("/tree/json")
    suspend fun knowledgeSystem(): AndroidSystemEntity

    /**
     * 体系下的文章，cid 查看 [SystemCategory] #id
     * 返回数据同首页文章列表
     */
    @GET("/article/list/{page}/json")
    suspend fun articleInCategory(@Path("page") page: Int, @Query("cid") cid: Int): HomeArticleEntity

    // 项目分类
    @GET("/project/tree/json")
    suspend fun projectCategory(): ProjectCategoryEntity

    /**
     * 返回项目分类下的所有项目列表，cid 查看 [ProjectCategoryData] #id
     */
    @GET("/project/list/{page}/json")
    suspend fun projectList(@Path("page") page: Int, @Query("cid") cid: Int): ProjectDetailEntity

    // ================================>
    // 登录
    @POST("/user/login")
    @FormUrlEncoded
    suspend fun login(@Field("username") username: String, @Field("password") password: String): WanUserEntity

    // 注册
    @POST("/user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String, @Field("password") password: String,
        @Field("repassword") repassword: String
    ): WanUserEntity

    // 退出
    @GET("/logout/json")
    suspend fun loginout(): ResponseBody

    // ===============================>
    // 收藏文章列表
    @GET("/lg/collect/list/{page}/json")
    suspend fun userCollectedArticles(@Path("page") page: Int): UserCollectEntity

    // 取消收藏，文章列表
    @POST("/lg/uncollect_originId/{articleId}/json")
    suspend fun uncollectArticle(@Path("articleId") articleId: Int): ResponseBody

    // 取消收藏，收藏列表
    @POST("/lg/uncollect/{articleId}/json")
    @FormUrlEncoded
    suspend fun uncollectCollection(@Path("articleId") articleId: Int, @Field("originId") originId: Int): ResponseBody

    // 收藏网站列表
    @GET("/lg/collect/usertools/json")
    suspend fun collectWebsiteList(): WebsiteEntity

    // 收藏网站
    @POST("/lg/collect/addtool/json")
    @FormUrlEncoded
    suspend fun addWebsite(@Field("name") name: String, @Field("link") link: String): ResponseBody

    // 编辑收藏网址
    @POST("/lg/collect/updatetool/json")
    @FormUrlEncoded
    suspend fun editWebsite(@Field("id") id: Int, @Field("name") name: String, @Field("link") link: String): ResponseBody

    // 删除收藏的网址
    @POST("/lg/collect/deletetool/json")
    @FormUrlEncoded
    suspend fun deleteWebsite(@Field("id") id: Int): ResponseBody

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
        @Path("page") page: Int
    ): WxChapterList

    // 公众号历史数据，k 传空字符则全部记录
    @GET("/wxarticle/list/{wxid}/{page}/json")
    suspend fun chapterHistoryList(
        @Path("wxid") wxid: Int, @Path("page") page: Int,
        @Query("k") keyword: String
    ): TopArticleEntity
}