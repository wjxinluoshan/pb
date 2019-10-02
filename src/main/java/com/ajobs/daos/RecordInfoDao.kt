package com.ajobs.daos

import javax.sql.DataSource

interface RecordInfoDao {
    fun setDataSource(ds: DataSource)
   fun getRecordInfo():String?
}