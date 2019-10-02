package com.ajobs.controllers

import com.ajobs.impdaos.ArticleInfoJdbcTemplate
import com.ajobs.impdaos.DocInfoJdbcTemplate
import com.ajobs.impdaos.PictureInfoJdbcTemplate
import com.ajobs.impdaos.TitleDictumDiagramInfoJdbcTemplate
import com.ajobs.tools.Constants
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.lang.Exception
import java.net.URLDecoder
import javax.servlet.http.HttpServletRequest
import java.util.Random
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/data")
class DataController {

    private var mApplicationContext: ApplicationContext? = null
    private var mArticleInfoJdbcTemplate: ArticleInfoJdbcTemplate? = null
    private var mPictureInfoJdbcTemplate: PictureInfoJdbcTemplate? = null
    private var mDocInfoJdbcTemplate: DocInfoJdbcTemplate? = null
    private var mTitleDictumDiagramInfoJdbcTemplate: TitleDictumDiagramInfoJdbcTemplate? = null

    private val mSuccess = "0"
    private val mFailure = "1"
    private val mSplitString = " pblog "


    private var mArticleTemplateFile: File? = null
    private var mArticleTemplateFileContent = ""
    /**
     * 上传文章
     */
    @RequestMapping(value = ["/uploadArticle"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun uploadArticleData(articleType: String, articleTitle: String
                          , articleShortContent: String, firstThumbnailImgInArticle: String
                          , articleHTMLContent: String, articleHref: String): String {
        getApplicationContext("jdbc.xml")
        try {
            /**
             * 在这里处理文章的html数据
             */
            //使用占位符机制将实现编写好的html文件中的placeholder替换成指定的内容（?+）

            mArticleTemplateFile = File(Constants.DocDirPath, "article_template.html")
            var mArticleTemplateFileContentSB = StringBuffer()
            BufferedReader(InputStreamReader(FileInputStream(mArticleTemplateFile), Charsets.UTF_8)).use {
                it.readLines().forEach {
                    mArticleTemplateFileContentSB.append(it)
                }
            }
            mArticleTemplateFileContent = mArticleTemplateFileContentSB.toString()


            var indexOne = mArticleTemplateFileContent.indexOf("?+")
            var indexTwo = mArticleTemplateFileContent.indexOf("?+", indexOne + 1)
            var indexThree = mArticleTemplateFileContent.indexOf("?+", indexTwo + 1)
            mArticleTemplateFileContent = mArticleTemplateFileContent.replaceRange(indexThree, indexThree + 2, articleHTMLContent)
            mArticleTemplateFileContent = mArticleTemplateFileContent.replaceRange(indexTwo, indexTwo + 2, articleTitle)
            mArticleTemplateFileContent = mArticleTemplateFileContent.replaceRange(indexOne, indexOne + 2, articleTitle)

            var at: String
            //http://localhost:8080/leisureArticleHtmls/12945630.html
            if (articleHref.isNotEmpty()) {
                var fileDir: String
                var fileName: String
                articleHref.split("/").run {
                    fileName = this[lastIndex]
                    fileDir = this[lastIndex - 1]
                }
                var path: String
                if (Constants.LeisureArticleDirPath.contains(fileDir)) {
                    path = Constants.LeisureArticleDirPath
                    at = Constants.LeisureArticleInfoTableName
                } else {
                    path = Constants.ProfessionalArticleDirPath
                    at = Constants.ProfessionalArticleInfoTableName
                }
                FileOutputStream(File(path, fileName)).use {
                    it.write(mArticleTemplateFileContent.toByteArray())
                }
                mArticleInfoJdbcTemplate?.updateArticleData(at, articleTitle, articleShortContent, firstThumbnailImgInArticle, articleHref)
            } else {
                //将文章给存放在指定的目录下（重复检查）
                //生成一段随机数字作为文章的索引
                var articleLocationLinkPreviousSuffix: String
                if (articleType.contains("休闲")) {
                    at = Constants.LeisureArticleInfoTableName
                    articleLocationLinkPreviousSuffix = Constants.ArticleLinkPreviousSuffix + "leisureArticleHtmls/"
                } else {
                    at = Constants.ProfessionalArticleInfoTableName
                    articleLocationLinkPreviousSuffix = Constants.ArticleLinkPreviousSuffix + "professionalArticleHtmls/"
                }
                //生成随机的8位数
                var random = Random()
                var randomNumber = random.nextInt(10000000) + 10000000
                var all = "$articleLocationLinkPreviousSuffix$randomNumber.html"
                //该文档链接已经存在
                while (mArticleInfoJdbcTemplate?.getArticleLocationLink(at, articleLocationLinkPreviousSuffix) == 1) {
                    randomNumber = random.nextInt(10000000) + 10000000
                    all = "$articleLocationLinkPreviousSuffix$randomNumber.html"
                }
                //4.将数据插入到数据库中
                mArticleInfoJdbcTemplate?.uploadArticleData(at, articleTitle, articleShortContent, firstThumbnailImgInArticle, all)
                //将文件写在响应的文件夹下面
                if (articleType.contains("休闲")) {
                    FileOutputStream(File(Constants.LeisureArticleDirPath, "$randomNumber.html")).use {
                        it.write(mArticleTemplateFileContent.toByteArray())
                    }
                } else {
                    FileOutputStream(File(Constants.ProfessionalArticleDirPath, "$randomNumber.html")).use {
                        it.write(mArticleTemplateFileContent.toByteArray())
                    }
                }
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 查询休闲文章
     *    发送给客户端的数据形式为：
     *         空字符串；
     *         articleTitle，articleShortContent，articleFirstImageUrl，articleLocationLink,articleTitle....
     */
    @RequestMapping(value = ["/getLeisureArticles"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getLeisureArticlesData(pageNumber: String): String {
        return try {
            getArticleDatas(Constants.LeisureArticleInfoTableName, pageNumber)
        } catch (e: Exception) {
            e.printStackTrace()
            mFailure
        }
    }

    /**
     * 查询专业文章
     */
    @RequestMapping(value = ["/getProfessionalArticles"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getProfessionalArticles(pageNumber: String): String {
        return try {
            getArticleDatas(Constants.ProfessionalArticleInfoTableName, pageNumber)
        } catch (e: Exception) {
            e.printStackTrace()
            mFailure
        }
    }


    /**
     * 得到文章的我名字集合
     */
    @RequestMapping(value = ["/getArticleNamesFromAppointTable"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getArticleNamesFromAppointTable(tableType: String): String {
        getApplicationContext("jdbc.xml")
        var tableName = ""
        if (tableType == "leisure") {
            tableName = Constants.LeisureArticleInfoTableName
        } else if (tableType == "professional") {
            tableName = Constants.ProfessionalArticleInfoTableName
        }
        try {
            var sb = StringBuilder()
            mArticleInfoJdbcTemplate?.getArticleNames(tableName)?.run {
                forEach {
                    var dataArr = it.split(mSplitString)
                    sb.append(dataArr[0]).append(mSplitString).append(dataArr[1]).append(mSplitString)
                }
                if (sb.isNotEmpty())
                    return sb.toString().substringBeforeLast(mSplitString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 查询休闲文章总数
     */
    @RequestMapping(value = ["/getLeisureArticlePageNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getLeisureArticlePageNumber(): String {
        try {
            return getArticlePageNumber(Constants.LeisureArticleInfoTableName) + mSplitString + mArticleInfoJdbcTemplate?.getSinglePageNumber()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 查询专业文章总数
     */
    @RequestMapping(value = ["/getProfessionalArticlePageNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getProfessionalArticlePageNumber(): String {
        try {
            return getArticlePageNumber(Constants.ProfessionalArticleInfoTableName) + mSplitString + mArticleInfoJdbcTemplate?.getSinglePageNumber()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 删除文章
     *   articleLocationLink： *****.html 中的*****
     */
    @RequestMapping(value = ["/deleteAppointArticle"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun deleteAppointArticle(articleType: String, articleLocationLink: String): String {
        getApplicationContext("jdbc.xml")
        try {
            /*
             *删除数据库记录
             */
            if (articleType == "leisure")
                mArticleInfoJdbcTemplate!!.deleteArticleInfo(Constants.LeisureArticleInfoTableName, articleLocationLink)
            else if (articleType == "professional")
                mArticleInfoJdbcTemplate!!.deleteArticleInfo(Constants.ProfessionalArticleInfoTableName, articleLocationLink)
            var articleName = articleLocationLink.split(".")[0].split("/").run {
                this[lastIndex]
            }
            /*
             *删除文件
             */
            if (articleLocationLink.contains("professionalArticleHtmls")) {
                try {
                    ArticleController().deleteAppointCommentRelationshipTables(articleName)
                } catch (e: Exception) {
                }
                File(Constants.ProfessionalArticleDirPath, "$articleName.html").delete()
            } else {
                File(Constants.LeisureArticleDirPath, "$articleName.html").delete()
            }

            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            return mFailure
        }
    }

    /**
     * 获取文章总数的逻辑处理
     */
    private fun getArticlePageNumber(articleType: String): String {
        getApplicationContext("jdbc.xml")
        return mArticleInfoJdbcTemplate?.getArticleTotalNumber(articleType).toString()
    }

    /**
     * 获取文章的逻辑处理
     */
    private fun getArticleDatas(articleType: String, pageNumber: String): String {
        getApplicationContext("jdbc.xml")
        var articleInfos = mArticleInfoJdbcTemplate?.getArticlesData(articleType, pageNumber.toInt())
        //返回数据
        articleInfos?.run {
            if (isNotEmpty()) {
                var stringBuilder = StringBuilder()
                forEach {
                    stringBuilder.append(it.articleTitle).append(mSplitString)
                            .append(it.articleShortContent).append(mSplitString)
                            .append(it.articleFirstImageUrl).append(mSplitString)
                            .append(it.articleLocationLink).append(mSplitString)
                }
                var data = stringBuilder.toString()
                return data.subSequence(0, data.length - mSplitString.length).toString()
            }
        }
        return mFailure
    }

    /**
     * 获取应用的上下文对象
     */
    private fun getApplicationContext(xml: String) {
        if (mApplicationContext == null) {
            mApplicationContext = ClassPathXmlApplicationContext(xml)
            mArticleInfoJdbcTemplate = mApplicationContext?.getBean("articleInfoJdbcTemplate") as ArticleInfoJdbcTemplate
            mPictureInfoJdbcTemplate = mApplicationContext?.getBean("pictureInfoJdbcTemplate") as PictureInfoJdbcTemplate
            mDocInfoJdbcTemplate = mApplicationContext?.getBean("docInfoJdbcTemplate") as DocInfoJdbcTemplate
            mTitleDictumDiagramInfoJdbcTemplate = mApplicationContext?.getBean("titleDictumDiagramInfoJdbcTemplate") as TitleDictumDiagramInfoJdbcTemplate
        }
    }

    /**
     * 上传照片的url和名字
     */
    @RequestMapping(value = ["/uploadPictures"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun uploadPicturesToDb(data: String): String {
        getApplicationContext("jdbc.xml")
        var index = 0
        var pictureUrl = ""
        var pictureName: String
        try {
            for (value in data.split(" pblog ")) {
                index++
                if (index == 1)
                    pictureUrl = value
                else {
                    pictureName = value
                    index = 0
                    mPictureInfoJdbcTemplate?.uploadPicture(pictureUrl, pictureName)
                }
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 得到数据库中的图片信息
     *  返回的形式  pictureUrl，pictureName，pictureUrl....
     */
    @RequestMapping(value = ["/getPicturesInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getPicturesInfo(pageNumber: String): String {
        getApplicationContext("jdbc.xml")
        try {
            var pictureInfoList = mPictureInfoJdbcTemplate?.getPictureInfo(pageNumber.toInt())
            pictureInfoList?.run {
                var stringBuilder = StringBuilder()
                forEach {
                    stringBuilder.append(it.pictureUrl).append(" pblog ")
                            .append(it.pictureName).append(" pblog ")
                }
                if (this.isNotEmpty()) {
                    var data = stringBuilder.toString()
                    return data.subSequence(0, data.length - 7).toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 删除照片的操作
     */
    @RequestMapping(value = ["/deletePicturesInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun deletePicturesInfo(data: String): String {
        getApplicationContext("jdbc.xml")
        var deleteCounter = 0
        //删除操作
        var dataArr = data.split(mSplitString)
        dataArr.forEach {
            try {
                if (mPictureInfoJdbcTemplate?.deletePicturesInfo(it) == mSuccess) {
                    deleteCounter++
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (deleteCounter != 0)
                    return "部分照片删除操作完成!!!"
            }
        }
        if (dataArr.size == deleteCounter)
            return mSuccess
        return mFailure
    }

    /**
     * 得到图片的总数
     */
    @RequestMapping(value = ["/getPicturesTotalNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getPicturesTotalNumber(): String {
        getApplicationContext("jdbc.xml")
        try {
            return mPictureInfoJdbcTemplate?.getPicturesTotalNumber().toString() + mSplitString + mPictureInfoJdbcTemplate?.getSinglePageNumber()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    private var mFile: ArrayList<File>? = null
    /**
     * 上传文档
     */
    @RequestMapping(value = ["/uploadDocs"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun uploadDocsToFilesDir(@RequestParam(value = "data", required = false) data: Array<MultipartFile>?, @RequestParam(value = "filesNameString", required = false) filesNameString: String?, request: HttpServletRequest): String {
        getApplicationContext("jdbc.xml")
        if (filesNameString == null) {
            mFile = ArrayList()
            var mpl: MultipartFile? = null
            var uploadedDocNumber = 0
            try {
                data?.forEach {
                    mpl = it
                    var file = File(Constants.DocDirPath, it.originalFilename)
                    var lastSuffix = 1
                    //如果文件存在则该文件名（）上传操作
                    var fileLastSuffix = ""
                    it.originalFilename.split(".").run {
                        fileLastSuffix = this[lastIndex]
                    }
                    var fileName = it.originalFilename.subSequence(0, it.originalFilename.length - 1 - fileLastSuffix.length)

                    while (file.exists()) {
                        file = File(Constants.DocDirPath, "$fileName(${++lastSuffix}).$fileLastSuffix")
                    }
                    FileOutputStream(file).use {
                        it.write(mpl!!.bytes)
                        uploadedDocNumber++
                    }
                    mFile?.add(file)
                }
                return mSuccess
            } catch (e: Exception) {
                e.printStackTrace()
                if (uploadedDocNumber > 0) {
                    return "部分文件上传失败!!!"
                }
            }
            return mSuccess
        } else {
            var filesName = filesNameString.split(",")
            var fileIndex = 0
            mFile?.forEach {
                if (it.name.replace(Regex("\\([0-9]+\\)"), "") == filesName[fileIndex]) {
                    fileIndex++
                    //开始将filePathAndNameMap的信息存放在数据库中
                    try {
                        mDocInfoJdbcTemplate?.uploadDocInfo(it.absolutePath.replace("\\", "/"), it.name)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("数据插入数据库的操作失败!!!")
                    }
                }
                //将formdata的文件中文乱码给转换过来
                else {
                    var file = File(it.parent, filesName[fileIndex])
                    var lastSuffix = 1
                    //如果文件存在则该文件名（）上传操作
                    var fileLastSuffix = ""
                    filesName[fileIndex].split(".").run {
                        fileLastSuffix = this[lastIndex]
                    }
                    var fileName = filesName[fileIndex].subSequence(0, filesName[fileIndex++].length - 1 - fileLastSuffix.length)

                    while (file.exists()) {
                        file = File(Constants.DocDirPath, "$fileName(${++lastSuffix}).$fileLastSuffix")
                    }
                    it.renameTo(file)
                    //开始将filePathAndNameMap的信息存放在数据库中
                    try {
                        mDocInfoJdbcTemplate?.uploadDocInfo(file.absolutePath.replace("\\", "/"), file.name)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("数据插入数据库的操作失败!!!")
                    }
                }

            }
        }

        return mFailure
    }

    /**
     * 获取文档信息
     *  返回的信息格式 ：
     *            docFilePath，docFileName,docFilePath....
     */
    @RequestMapping(value = ["/getDocInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getDocsInfo(pageNumber: String): String {
        getApplicationContext("jdbc.xml")
        try {
            var docList = mDocInfoJdbcTemplate?.getDocsInfo(pageNumber.toInt())
            docList?.run {
                var strBuilder = StringBuilder()
                forEach {
                    strBuilder.append(it.docFilePath).append(mSplitString)
                            .append(it.docFileName).append(mSplitString)
                }
                if (this.isNotEmpty()) {
                    var data = strBuilder.toString()
                    return data.substringBeforeLast(mSplitString)
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mFailure
    }


    /**
     * 得到文档的总数量
     */
    @RequestMapping(value = ["/getDocsTotalNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getDocsTotalNumber(): String {
        getApplicationContext("jdbc.xml")
        try {
            return mDocInfoJdbcTemplate?.getDocTotalNumber().toString() + mSplitString + mDocInfoJdbcTemplate?.getSinglePageNumber()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 删除文档
     */
    @RequestMapping(value = ["/deleteDocsInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun deleteDocsInfo(data: String): String {
        getApplicationContext("jdbc.xml")
        var deleteCounter = 0
        //删除操作

        try {
            data.split(mSplitString).forEach {
                if (mDocInfoJdbcTemplate?.deleteDocInfo(it) == mSuccess) {
                    if (deleteCounter == 0)
                        deleteCounter++
                }
            }
            return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            if (deleteCounter != 0)
                return "部分文档删除操作完成!!!"
        }

        return mFailure
    }

    /**
     * 下载文档
     */
    @RequestMapping(value = ["/downloadDoc"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun downloadDoc(filePath: String, response: HttpServletResponse) {
        try {
            FileInputStream(File(URLDecoder.decode(filePath, "utf-8"))).use {
                response.contentType = "application/x-download"
                var fileName: String
                filePath.split("/").run {
                    fileName = this[lastIndex]
                }
                response.addHeader("Content-Disposition", "attachment;filename=$fileName")
                IOUtils.copy(it, response.outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(URLDecoder.decode(filePath, "utf-8"))
    }


//    /**
//     * 上传文档
//     */
//    @RequestMapping(value = ["/testUploadDocs"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
//    @ResponseBody
//    fun test(@RequestParam(value = "data") data: MultipartFile): String {
//        print(data.size)
//        return "C:\\Users\\wjx\\Pictures\\Feedback\\{2512BCAC-474D-4125-881C-A34303CB7E23}\\ym18.jpg"
//    }

    /**
     * 修改标题格言图信息
     */
    @RequestMapping(value = ["/updateTitleDictumDInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun updateTitleDictumDInfo(@RequestParam(value = "titleName", required = false) titleName: String?, @RequestParam(value = "dictum", required = false) dictum: String?, @RequestParam(value = "titleBg", required = false) titleBg: String?): String {
        getApplicationContext("jdbc.xml")
        try {
            if (mTitleDictumDiagramInfoJdbcTemplate?.updateInfo(titleName, dictum, titleBg) != null)
                return mSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }

    /**
     * 得到标题格言图信息
     *   返回信息： titleName $mSplitString dictum $mSplitString titleBg
     */
    @RequestMapping(value = ["/getTitleDictumDInfo"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getTitleDictumDInfo(): String {
        getApplicationContext("jdbc.xml")
        try {
            mTitleDictumDiagramInfoJdbcTemplate?.getTitleDictumDiagramInfo()?.run {
                return "$titleName$mSplitString$dictum$mSplitString$titleBg"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mFailure
    }
}