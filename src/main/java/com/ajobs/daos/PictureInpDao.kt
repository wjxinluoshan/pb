package com.ajobs.daos

import com.ajobs.tabledatas.PictureInfo
import javax.sql.DataSource

interface PictureInpDao {
    fun setDataSource(ds: DataSource)
    fun uploadPicture(pictureUrl: String, pictureName: String): String
    fun getPictureInfo(pageNumber: Int): List<PictureInfo>?
    fun getPicturesTotalNumber(): Long
    fun deletePicturesInfo(pictureUrl:String):String
}