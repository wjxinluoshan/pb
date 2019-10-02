package com.ajobs.impdaos

import com.ajobs.daos.PictureInpDao
import com.ajobs.mappers.PictureInfoMapper
import com.ajobs.tabledatas.PictureInfo
import com.ajobs.tools.Constants
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class PictureInfoJdbcTemplate : PictureInpDao {

    private var dataSource: DataSource? = null
    private var jdbcTemplateObject: JdbcTemplate? = null

    private var singlePageNumber = 2
    fun getSinglePageNumber() = singlePageNumber


    override fun setDataSource(ds: DataSource) {
        this.dataSource = ds
        this.jdbcTemplateObject = JdbcTemplate(ds)
    }

    /**
     * 得到picture信息集合
     */
    override fun getPictureInfo(pageNumber: Int): List<PictureInfo>? {
        //开始处理分页number，倒序查询
        //1.得到总数据量
        var totalNum = getPicturesTotalNumber()
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
        return jdbcTemplateObject?.query("select pictureUrl,pictureName from ${Constants.PictureTableName} where id between $idPreviousIndex and $idNextIndex", PictureInfoMapper())

    }

    /**
     * 上传picture信息
     */
    override fun uploadPicture(pictureUrl: String, pictureName: String): String {
        //检查该图片,不存在上传
        if (jdbcTemplateObject?.queryForObject("select count(*) from ${Constants.PictureTableName} where pictureUrl='$pictureUrl'", Int::class.java) == 0) {
            if (jdbcTemplateObject?.update("INSERT INTO ${Constants.PictureTableName} (pictureUrl, pictureName) VALUES ('$pictureUrl','$pictureName')") == 1)
                return Constants.CmdSuccess.toString()
        }
        return Constants.CmdFailure.toString()
    }

    override fun getPicturesTotalNumber(): Long {
        return jdbcTemplateObject?.queryForObject("select count(*) from ${Constants.PictureTableName}", Long::class.java)
                ?: 0L
    }

    /**
     * 删除picture
     */
    override fun deletePicturesInfo(pictureUrl: String): String {
        if (jdbcTemplateObject?.update("delete from ${Constants.PictureTableName} where pictureUrl='$pictureUrl'") == 1) {
            jdbcTemplateObject?.execute("alter table ${Constants.PictureTableName} drop `id`")
            jdbcTemplateObject?.execute("alter table ${Constants.PictureTableName} add `id` int not null first ")
            jdbcTemplateObject?.execute("ALTER TABLE ${Constants.PictureTableName} MODIFY COLUMN  `id` int NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY(id)")

            return Constants.CmdSuccess.toString()
        }
        return Constants.CmdFailure.toString()
    }
}