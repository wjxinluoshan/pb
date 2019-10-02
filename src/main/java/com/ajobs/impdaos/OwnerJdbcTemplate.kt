package com.ajobs.impdaos

import com.ajobs.tools.Constants
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate


class OwnerJdbcTemplate {
    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null


    fun setDataSource(ds: DataSource?) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    /**
     * 验证博客拥有者信息
     */
    fun verifyOwner(userName: String, password: String): String {
        var resultInt = jdbcTemplateObject?.queryForObject("select count(*) from ${Constants.OwnerTableName} where userName='$userName' and password='$password'", Int::class.java)
        return if (resultInt == 1)
            Constants.CmdSuccess.toString()
        else
            Constants.CmdFailure.toString()
    }
    /**
     * 得到网站的浏览次数
     */
    fun getSiteBrowseNumber():Int?{
        return jdbcTemplateObject?.queryForObject("select siteBrowseNumber from ${Constants.OwnerTableName}", Int::class.java)
    }
    /**
     * 得到网站的浏览次数
     */
    fun uploadSiteBrowseNumber():String{
        var number:Int? = getSiteBrowseNumber()
        if(number==null) {
            number = 0
        }
        return jdbcTemplateObject?.update("update ${Constants.OwnerTableName} set siteBrowseNumber=${number+1}").toString()
    }
}