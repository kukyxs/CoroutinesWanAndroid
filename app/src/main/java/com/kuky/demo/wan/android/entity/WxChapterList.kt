package com.kuky.demo.wan.android.entity


/**
 * @author kuky.
 * @description 公众号作者对应的文章数据类
 */
data class WxChapterListData(
    val curPage: Int,
    val datas: MutableList<WxChapterListDatas>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class WxChapterListDatas(
    val apkLink: String,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<WxChapterListTagsBean>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

data class WxChapterListTagsBean(
    val name: String,
    val url: String
)


