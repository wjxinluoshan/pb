package com.ajobs.daos

import com.ajobs.tabledatas.TitleDictumDiagramInfo
import javax.sql.DataSource

interface TitleDictumDiagramInfoDao {
    fun setDataSource(ds: DataSource?)
    fun updateInfo(titleName:String?, dictum:String?, titleBg:String?):Int?
    fun getTitleDictumDiagramInfo():TitleDictumDiagramInfo?
}