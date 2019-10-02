package com.ajobs.impdaos

import com.ajobs.daos.TitleDictumDiagramInfoDao
import com.ajobs.mappers.TitleDictumDiagramInfoMapper
import com.ajobs.tabledatas.TitleDictumDiagramInfo
import com.ajobs.tools.CommandSqlField
import com.ajobs.tools.Constants
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate


class TitleDictumDiagramInfoJdbcTemplate : TitleDictumDiagramInfoDao {
    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null


    override fun setDataSource(ds: DataSource?) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    override fun updateInfo(titleName: String?, dictum: String?, titleBg: String?): Int? {
        var commandField = CommandSqlField()
        var map = commandField.returnNonNullField(mutableMapOf("titleName" to titleName, "dictum" to dictum, "titleBg" to titleBg))
        if (jdbcTemplateObject?.queryForObject("select count(*) from ${Constants.TitleDictumDiagramTableName}", Int::class.java) == 0) {
            jdbcTemplateObject?.update(commandField.returnInsertAvailableField(Constants.TitleDictumDiagramTableName,map))
        }
        return jdbcTemplateObject?.update(commandField.returnUpdateAvailableField(Constants.TitleDictumDiagramTableName, map))
    }

    override fun getTitleDictumDiagramInfo(): TitleDictumDiagramInfo? {
        return jdbcTemplateObject?.queryForObject("select * from ${Constants.TitleDictumDiagramTableName}", TitleDictumDiagramInfoMapper())
    }


}