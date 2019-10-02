package com.ajobs.impdaos

import com.ajobs.daos.UserInfoDao
import com.ajobs.mappers.UserInfoMapper
import com.ajobs.tabledatas.UserInfo
import com.ajobs.tools.Constants
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate


class UserInfoJdbcTemplate : UserInfoDao {
    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null


    override fun setDataSource(ds: DataSource?) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    override fun uploadUserInfo(profile: String?, introduce: String?, habit: String?): Int {
        var SQL: String
        if (getUserInfo() != null) {
            SQL = "update  ${Constants.UserInfoTableName} set profile=?,introduce=?,habit=?"
        } else
            SQL = "insert into ${Constants.UserInfoTableName} (profile, introduce,habit) values (?,?,?)"
        try {
            jdbcTemplateObject?.update(SQL, profile, introduce, habit)
            return Constants.CmdSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            println("uploadUserInfo error！！！")
        }
        return Constants.CmdFailure
    }

    override fun getUserInfo(): UserInfo? {
        try {
            var userInfo = jdbcTemplateObject?.queryForObject("select profile,introduce,habit from ${Constants.UserInfoTableName}", UserInfoMapper())
            return userInfo
        } catch (e: Exception) {
            println("getUserInfo error！！！")
            e.printStackTrace()
        }
        return null
    }

}