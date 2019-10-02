package com.ajobs.impdaos

import com.ajobs.daos.DocInfoDao
import com.ajobs.mappers.DocInfoMapper
import com.ajobs.tabledatas.DocInfo
import com.ajobs.tools.Constants
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import javax.sql.DataSource

class DocInfoJdbcTemplate : DocInfoDao {

    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null
    /**
     * 单页的返回数目
     */
    private var singlePageNumber = 12

    fun getSinglePageNumber() = singlePageNumber


    override fun setDataSource(ds: DataSource) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    override fun uploadDocInfo(docFilePath: String, docFileName: String): String {
        if (jdbcTemplateObject?.update("INSERT INTO ${Constants.DocTableName} (docFilePath, docFileName) VALUES ('$docFilePath','$docFileName')") == 1)
            return Constants.CmdSuccess.toString()
        return Constants.CmdFailure.toString()
    }

    override fun getDocsInfo(pageNumber: Int): List<DocInfo>? {
        //开始处理分页number，倒序查询
        //1.得到总数据量
        var totalNum = getDocTotalNumber()
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
        return jdbcTemplateObject?.query("select docFilePath,docFileName from ${Constants.DocTableName} where id between $idPreviousIndex and $idNextIndex", DocInfoMapper())
    }

    override fun deleteDocInfo(docFilePath: String): String {
        try {
            if (File(docFilePath).delete()) {
                if (jdbcTemplateObject?.update("delete from ${Constants.DocTableName} where docFilePath='$docFilePath'") == 1) {
                    jdbcTemplateObject?.execute("alter table ${Constants.DocTableName} drop `id`")
                    jdbcTemplateObject?.execute("alter table ${Constants.DocTableName} add `id` int not null first ")
                    jdbcTemplateObject?.execute("ALTER TABLE ${Constants.DocTableName} MODIFY COLUMN  `id` int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id)")

                    return Constants.CmdSuccess.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Constants.CmdFailure.toString()
    }

    override fun getDocTotalNumber(): Long {
        return jdbcTemplateObject?.queryForObject("select count(*) from ${Constants.DocTableName}", Long::class.java)
                ?: 0L
    }
}