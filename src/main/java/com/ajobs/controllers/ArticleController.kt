package com.ajobs.controllers

import com.ajobs.impdaos.CommentInfoJdbcTemplate
import com.ajobs.tools.Constants
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.*


@Controller
@RequestMapping("/article")
class ArticleController {

    private var mArticleMainContentOfHtml: String? = null
    private val mSuccess = "0"
    private val mFailure = "1"
    private val mSplitString = " pblog "
    private val mCommentAndResponseDataSplitString = " ppbblloogg "

    private val mFromComment = "comment"
    private val mFromResponse = "response"

    private var mApplicationContext: ApplicationContext? = null
    private var mCommentInfoJdbcTemplate: CommentInfoJdbcTemplate? = null
    /**
     * data:
     *     -eg:http://localhost:8080/leisureArticleHtmls/12945630.html
     */
    @RequestMapping(value = ["/loadArticleContentOfHtml"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun loadArticleContentOfHtml(data: String): String {
        try {
            var fileDir: String
            var fileName: String
            data.split("/").run {
                fileName = this[lastIndex]
                fileDir = this[lastIndex - 1]
            }
            var path: String
            path = if (Constants.LeisureArticleDirPath.contains(fileDir)) {
                Constants.LeisureArticleDirPath
            } else {
                Constants.ProfessionalArticleDirPath
            }
            BufferedReader(InputStreamReader(FileInputStream(File(path, fileName)), Charsets.UTF_8)).use {
                var sb = StringBuffer()
                it.readLines().forEach {
                    sb.append(it)
                }
                var contentOfHtml = sb.toString()
                mArticleMainContentOfHtml = contentOfHtml.split("<title>")[1].split("</title>")[0] + mSplitString
//                mArticleMainContentOfHtml += contentOfHtml.split("<div class=\"show_article-content_div\">")[1].split("</div></body></html>")[0]
                mArticleMainContentOfHtml += contentOfHtml.split("<div class=\"show_article-content_div\">")[1].split(" <div class=\"comment_area_div\" id=\"comment_area_div\">")[0]
                mArticleMainContentOfHtml += (mSplitString + data)
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 得到文章的html内容
     */
    @RequestMapping(value = ["/getArticleMainContentOfHtml"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getArticleMainContentOfHtml(): String {
        if (mArticleMainContentOfHtml == null)
            return mFailure
        return mArticleMainContentOfHtml!!
    }

    /**
     * insert data into commentTable:
     *    tableName: 文章的 .html前的数据
     */
    @RequestMapping(value = ["/insertCommentContentToAppointTable"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun insertCommentContentToAppointTable(tableName: String, commenterId: String, commentContent: String): String {
        getApplicationContext("jdbc.xml")
        try {
            if (mCommentInfoJdbcTemplate!!.checkTableWhetherExisting(tableName, mFromComment)) {
                mCommentInfoJdbcTemplate?.insertDataIntoCommentTable(tableName, commenterId, commentContent)
            } else {
                mCommentInfoJdbcTemplate?.createCommentTable(tableName)
                mCommentInfoJdbcTemplate?.insertDataIntoCommentTable(tableName, commenterId, commentContent)
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * insert data into responseTable:
     *    rId:commenTable的数据总数加一
     */
    @RequestMapping(value = ["/insertResponseAndCommentContentToAppointTable"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun insertResponseAndCommentContentToAppointTable(tableName: String, rId: String, cId: String, commenterId: String, commentContent: String): String {
        getApplicationContext("jdbc.xml")
        try {
            if (mCommentInfoJdbcTemplate!!.checkTableWhetherExisting(tableName, mFromResponse)) {
                mCommentInfoJdbcTemplate?.insertDataIntoResponseTable(tableName, rId, cId)
                insertCommentContentToAppointTable(tableName, commenterId, commentContent)
            } else {
                mCommentInfoJdbcTemplate?.createResponseTable(tableName)
                mCommentInfoJdbcTemplate?.insertDataIntoResponseTable(tableName, rId, cId)
                insertCommentContentToAppointTable(tableName, commenterId, commentContent)
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     *得到指定文章的评论数据:
     *   在 ownerMain.html中加载使用
     */
    @RequestMapping(value = ["/getCommentData"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getCommentData(tableName: String): String {
        getApplicationContext("jdbc.xml")
        try {
            if (mCommentInfoJdbcTemplate!!.checkTableWhetherExisting(tableName, mFromComment)) {
                val commentData = mCommentInfoJdbcTemplate?.getCommentData(tableName)
                if (commentData != null && commentData.isNotEmpty())
                    return commentData.toString().substringBeforeLast(mSplitString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     *得到指定文章的回复评论数据
     */
    @RequestMapping(value = ["/getCommentAndResponseData"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getCommentAndResponseData(tableName: String): String {
        getApplicationContext("jdbc.xml")
        try {
            if (mCommentInfoJdbcTemplate!!.checkTableWhetherExisting(tableName, mFromComment)) {
                val dataArr = mCommentInfoJdbcTemplate?.getCommentAndResponseData(tableName)
                if (dataArr != null && dataArr[0] != "null") {
                    return dataArr[0] + mCommentAndResponseDataSplitString + dataArr[1]
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 验证评论完成
     */
    @RequestMapping(value = ["/verifyCommentDataDone"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun verifyCommentDataDone(tableName: String, data: String): String {
        getApplicationContext("jdbc.xml")
        try {
            //分析的数据是comment的id号
            if (data == "") {
                mCommentInfoJdbcTemplate?.modifyCommentTableData(tableName, null)
            } else {
                val idArr = data.split(mSplitString)
                mCommentInfoJdbcTemplate?.modifyCommentTableData(tableName, idArr)
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 获取应用的上下文对象
     */
    private fun getApplicationContext(xml: String) {
        if (mApplicationContext == null) {
            mApplicationContext = ClassPathXmlApplicationContext(xml)
            mCommentInfoJdbcTemplate = mApplicationContext?.getBean("commentInfoJdbcTemplate") as CommentInfoJdbcTemplate
        }
    }

    fun deleteAppointCommentRelationshipTables(tableName: String) {
        getApplicationContext("jdbc.xml")
        mCommentInfoJdbcTemplate?.deleteAppointCommentRelationshipTables(tableName)
    }
}