package com.ajobs.daos

import com.ajobs.tabledatas.ArticleInfo
import javax.sql.DataSource

interface ArticleInfoDao {
    fun setDataSource(ds: DataSource)
    fun uploadArticleData(articleType: String, articleTitle: String, articleShortContent: String, articleFirstImageUrl: String, articleLocationLink: String): Int
    fun getArticleTotalNumber(articleType: String): Long
    fun getArticlesData(articleType: String, pageNumber: Int): List<ArticleInfo>?
    fun getArticleLocationLink(articleType: String, articleLocationLink: String): Int
    fun updateArticleData(articleType: String, articleTitle: String, articleShortContent: String, articleFirstImageUrl: String, articleLocationLink: String): Int
    fun getArticleNames(tableName: String): List<String>?
    fun deleteArticleInfo(tableName: String, articleLocationLink: String)
}