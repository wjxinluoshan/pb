package com.ajobs.impdaos

import com.ajobs.daos.ArticleInfoDao
import com.ajobs.mappers.ArticleInfoMapper
import com.ajobs.tabledatas.ArticleInfo
import com.ajobs.tools.Constants
import org.springframework.jdbc.core.JdbcTemplate
import javax.print.DocFlavor
import javax.sql.DataSource

class ArticleInfoJdbcTemplate : ArticleInfoDao {

    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null

    //单页数据为4
    private val singlePageNumber = 4

    fun getSinglePageNumber() = singlePageNumber

    override fun setDataSource(ds: DataSource) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    /**
     * 上传数据到数据库表
     */
    override fun uploadArticleData(articleType: String, articleTitle: String, articleShortContent: String, articleFirstImageUrl: String, articleLocationLink: String): Int {
        val SQL = "insert into $articleType (articleTitle, articleShortContent,articleFirstImageUrl,articleLocationLink) values (?,?,?,?)"
        try {
            jdbcTemplateObject?.update(SQL, articleTitle, articleShortContent, articleFirstImageUrl, articleLocationLink)
            return Constants.CmdSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            println("uploadArticleInfo error！！！")
        }
        return Constants.CmdFailure
    }


    /**
     * 返回数据中存放的文章的总数量
     * 接收者判断返回值为空还是0L
     */
    override fun getArticleTotalNumber(articleType: String): Long {
        return jdbcTemplateObject?.queryForObject("select count(*) from $articleType", Long::class.java)
                ?: 0L
    }

    /**
     * 根据页号检索
     * 返回响应的文章数据
     */
    override fun getArticlesData(articleType: String, pageNumber: Int): List<ArticleInfo>? {
        //开始处理分页number，倒序查询
        //1.得到总数据量
        var totalNum = getArticleTotalNumber(articleType)
        //2.计算寻要查询最大的数据量
        var searchDataMaxNum = pageNumber * singlePageNumber
        //3.判断总数据量和查询最大数据量的关系
        var deltaNum = totalNum - searchDataMaxNum

        var idPreviousIndex: Long
        var idNextIndex: Long
        //如果偏移量>=0，则返回的数据的 id 范围 在  deltaNum+1 --->  totalNum-(pageNumber-1)*singlePageNumber
        if (deltaNum >= 0) {
            idPreviousIndex = deltaNum + 1
            idNextIndex = totalNum - (pageNumber - 1) * singlePageNumber
        }
        //反之1--->totalNum-(pageNumber-1)*singlePageNumber
        else {
            idPreviousIndex = 1
            idNextIndex = totalNum - (pageNumber - 1) * singlePageNumber
        }
        //返回查询数据
        return jdbcTemplateObject?.query("select articleTitle,articleShortContent,articleFirstImageUrl,articleLocationLink from $articleType where id between $idPreviousIndex and $idNextIndex", ArticleInfoMapper())
    }

    override fun getArticleLocationLink(articleType: String, articleLocationLink: String): Int {
        return jdbcTemplateObject?.queryForObject("select count(*) from $articleType where articleLocationLink='$articleLocationLink'", Int::class.java)
                ?: 0
    }

    override fun updateArticleData(articleType: String, articleTitle: String, articleShortContent: String, articleFirstImageUrl: String, articleLocationLink: String): Int {
        return jdbcTemplateObject?.update("update $articleType set articleTitle=?,articleShortContent=?,articleFirstImageUrl=? where articleLocationLink=?",
                articleTitle, articleShortContent, articleFirstImageUrl, articleLocationLink) ?: 0
    }

    override fun getArticleNames(tableName: String): List<String>? {
        return jdbcTemplateObject?.query("select articleTitle,articleLocationLink from $tableName") { p0, _ ->
            p0.getString("articleTitle") + " pblog " + p0.getString("articleLocationLink")
        }
    }

    override fun deleteArticleInfo(tableName: String, articleLocationLink: String) {
        if (jdbcTemplateObject?.update("delete from $tableName where articleLocationLink=?", articleLocationLink) == 1) {
            jdbcTemplateObject?.execute("alter table $tableName drop `id`")
            jdbcTemplateObject?.execute("alter table $tableName add `id` int not null first ")
            jdbcTemplateObject?.execute("ALTER TABLE $tableName MODIFY COLUMN  `id` int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id)")
        }
    }
}