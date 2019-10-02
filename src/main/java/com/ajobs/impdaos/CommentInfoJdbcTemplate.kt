package com.ajobs.impdaos

import com.ajobs.tabledatas.CommentTableInfo
import com.ajobs.tabledatas.ResponseCommentTableInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.lang.StringBuilder
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

class CommentInfoJdbcTemplate {
    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null
    private var mSplitString = " pblog "
    private val mFromComment = "comment"
    private val mFromResponse = "response"

    fun setDataSource(ds: DataSource) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    /**
     * 创建评论表和评论验证表
     */
    fun createCommentTable(tableName: String) {
        /*
        创建待评论的表
         */
        val createCommentTableSql = "CREATE TABLE `_$tableName` (\n" +
                "\t`id` VARCHAR(50) NOT NULL,\n" +
                "\t`commenterId` VARCHAR(50) NOT NULL,\n" +
                "\t`commentContent` VARCHAR(300) NOT NULL,\n" +
                "\t`commentDataTime` DATETIME NOT NULL\n" +
//                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "COLLATE='utf8_general_ci';"
        jdbcTemplateObject?.execute(createCommentTableSql)

        /*
        创建合法评论的表数据
         */
        val createVerifyCommentTableSql = "CREATE TABLE `_${tableName}_verify` (\n" +
                "\t`id` VARCHAR(50) NOT NULL\n" +
                ")\n" +
                "COLLATE='utf8_general_ci';"
        jdbcTemplateObject?.execute(createVerifyCommentTableSql)

    }

    /**
     * 创建回复表
     */
    fun createResponseTable(tableName: String) {
        val createResponseTableSql = "CREATE TABLE `_${tableName}_response` (\n" +
//                "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                "\t`rId` INT NOT NULL,\n" +
                "\t`cId` INT NOT NULL\n" +
//                "\t`responseContent` VARCHAR(300) NOT NULL,\n" +
//                "\t`responseDataTime` DATETIME NOT NULL,\n" +
//                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "COLLATE='utf8_general_ci';"
        jdbcTemplateObject?.execute(createResponseTableSql)
    }

    fun checkTableWhetherExisting(tableName: String, from: String): Boolean {
        var tName = if (from == mFromComment) {
            tableName
        } else
            tableName + "_response"
        var table_name = jdbcTemplateObject?.query("SELECT table_name FROM information_schema.TABLES WHERE table_name=?", arrayOf("_$tName")) { p0, _ -> p0.getString("table_name") }
        if (table_name != null && table_name.isNotEmpty())
            return true
        return false
    }

    fun insertDataIntoCommentTable(tableName: String, commenterId: String, commentContent: String) {
        var maxIdString: String?
        var maxIdInt = 1
        try {
            maxIdString = jdbcTemplateObject?.queryForObject("select id from _$tableName order by id DESC limit 1", String::class.java)
            if (maxIdString != null)
                maxIdInt = maxIdString.toInt() + 1
        } catch (e: Exception) {
        }
        jdbcTemplateObject?.update("insert into _$tableName (id,commenterId,commentContent,commentDataTime) values (?,?,?,?)", maxIdInt.toString(), commenterId, commentContent, Timestamp(Date().time))

    }

    fun insertDataIntoResponseTable(tableName: String, rId: String, cId: String) {
        jdbcTemplateObject?.update("insert into _${tableName}_response (rId,cId) values (?,?)", rId, cId)
    }

    /**
     * 创建插入数据的时间
     */
    private fun createDateTime(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())
    }

    /**
     * 得到评论验证和恢复的数据：
     *    特定的分隔符来进行分割，数据返回给客户端，有客户端进行数据的细操作
     *    return@:评论&回复
     */
    fun getCommentAndResponseData(tableName: String): Array<String>? {
        var sb = getCommentData(tableName)
        //检测comment Table中是否含有数据
        var commentDataInfo: String
        if (sb.isNotEmpty())
            commentDataInfo = sb.toString().substringBeforeLast(mSplitString)
        else
            return arrayOf("null", "null")
        sb.clear()
        var tName = tableName.substringBeforeLast("_verify")
        if (checkTableWhetherExisting(tName, mFromResponse)) {
            var responseInfoList = jdbcTemplateObject?.query("select * from _${tName}_response", ResponseTableMapper())
            responseInfoList?.apply {
                reverse()
            }?.forEach {
                sb.append(it.rId).append(mSplitString)
                        .append(it.cId).append(mSplitString)
            }
        }
        return if (sb.isNotEmpty())
            arrayOf(commentDataInfo, sb.toString().substringBeforeLast(mSplitString))
        else
            arrayOf(commentDataInfo, "null")
    }

    /**
     * 得到评论表数据:
     *    该数据来自于评论验证表的数据
     */
    fun getCommentData(tableName: String): StringBuilder {
        var sb = StringBuilder()
        var commentInfoList: MutableList<CommentTableInfo>?
        if (tableName.contains("verify")) {
            commentInfoList = mutableListOf()
            var tName = tableName.substringBeforeLast("_verify")
            jdbcTemplateObject?.query("select id from  _$tableName") { p0 ->
                commentInfoList?.add(jdbcTemplateObject!!.query("select * from _${tName} where id='${p0.getString("id")}'", CommentTableMapper())[0])
                while (p0.next()) {
                    commentInfoList?.add(jdbcTemplateObject!!.query("select * from _${tName} where id='${p0.getString("id")}'", CommentTableMapper())[0])
                }
            }
        } else
            commentInfoList = jdbcTemplateObject?.query("select * from _${tableName}", CommentTableMapper())
        commentInfoList?.apply {
            reverse()
        }?.forEach {
            sb.append(it.id).append(mSplitString)
                    .append(it.commenterId).append(mSplitString)
                    .append(it.commentContent).append(mSplitString)
                    .append(it.commentDataTime).append(mSplitString)
        }
        return sb
    }

    /**
     * 修改comment和verifyComment表的数据
     */
    fun modifyCommentTableData(tableName: String, idArr: List<String>?) {
        /*
         jdbcTemplateObject?.execute("alter table ${Constants.PictureTableName} drop `id`")
          jdbcTemplateObject?.execute("alter table ${Constants.PictureTableName} add `id` int not null first ")
          jdbcTemplateObject?.execute("ALTER TABLE ${Constants.PictureTableName} MODIFY COLUMN  `id` int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id)")
         */
        if (!checkTableWhetherExisting(tableName, mFromResponse))
            createResponseTable(tableName)
        if (idArr != null) {
            idArr.forEach {
                //删除该id
                jdbcTemplateObject?.update("delete from  _${tableName} where id=?", it)
                //删除rId
                jdbcTemplateObject?.update("delete from _${tableName}_response where rId=?", it)
                //获取cId = it的ResultSet(rid)
                jdbcTemplateObject?.query("select rId from _${tableName}_response where cId=? ", arrayOf(it)) { p0 ->
                    p0.getString("rId")?.run {
                        jdbcTemplateObject?.update("delete from _${tableName}_response where rId=?", this)
                        jdbcTemplateObject?.update("delete from _${tableName} where id=?", this)
                    }
                    while (p0.next()) {
                        p0.getString("rId")?.run {
                            jdbcTemplateObject?.update("delete from _${tableName}_response where rId=?", this)
                            jdbcTemplateObject?.update("delete from _${tableName} where id=?", this)
                        }
                    }
                }
            }
            /*
               *将剩下在评论表中的数据给添加到验证完毕评论表中
               *  暴力大法：
               */
            jdbcTemplateObject?.execute("truncate  table _${tableName}_verify ")
            jdbcTemplateObject?.update("insert into _${tableName}_verify (id) select id from _${tableName}")

        } else {
            /*
                *将剩下在评论表中的数据给添加到验证完毕评论表中
                *  暴力大法：
                */
            jdbcTemplateObject?.execute("truncate  table _${tableName}_verify ")
            jdbcTemplateObject?.update("insert into _${tableName}_verify (id) select id from _${tableName}")
        }

    }

    /**
     * 删除指定文章所有表
     */
    fun deleteAppointCommentRelationshipTables(tableName: String) {
        if (checkTableWhetherExisting(tableName, mFromComment)) {
            jdbcTemplateObject?.execute("drop table _$tableName")
            jdbcTemplateObject?.execute("drop table _${tableName}_verify")
            if (checkTableWhetherExisting(tableName, mFromResponse))
                jdbcTemplateObject?.execute("drop table _${tableName}_response")
        }
    }

    inner class CommentTableMapper : RowMapper<CommentTableInfo> {
        override fun mapRow(p0: ResultSet, p1: Int): CommentTableInfo? {
            return CommentTableInfo(
                    p0.getString("id"),
                    p0.getString("commenterId"),
                    p0.getString("commentContent"),
                    p0.getTimestamp("commentDataTime").toString().split(".")[0]
            )
        }
    }

    inner class ResponseTableMapper : RowMapper<ResponseCommentTableInfo> {
        override fun mapRow(p0: ResultSet, p1: Int): ResponseCommentTableInfo? {
            return ResponseCommentTableInfo(
                    p0.getString("rId"),
                    p0.getString("cId")
            )
        }
    }
}