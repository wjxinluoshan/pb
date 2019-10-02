package com.ajobs.daos

import com.ajobs.tabledatas.DocInfo
import javax.sql.DataSource

interface DocInfoDao {
    fun setDataSource(ds: DataSource)
    fun uploadDocInfo(docFilePath: String, docFileName: String): String
    fun getDocsInfo(pageNumber: Int): List<DocInfo>?
    fun getDocTotalNumber(): Long
    fun deleteDocInfo(docFilePath: String): String
}