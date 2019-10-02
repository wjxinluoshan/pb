package com.ajobs.impdaos

import com.ajobs.daos.RecordInfoDao
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class RecordInfoTemplate:RecordInfoDao {
    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null
    override fun setDataSource(ds: DataSource) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
   }

    override fun getRecordInfo(): String? {

        return null
    }
}