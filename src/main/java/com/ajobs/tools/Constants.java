package com.ajobs.tools;

public class Constants {
    public final static String UserInfoTableName = "用户信息表";
    public final static String LeisureArticleInfoTableName = "休闲文章信息表";
    public final static String ProfessionalArticleInfoTableName = "专业文章信息表";
    public final static String OwnerTableName = "拥有者信息表";
    public final static String PictureTableName = "照片信息表";
    public final static String DocTableName = "文档信息表";
    public final static String TitleDictumDiagramTableName = "标题格言图表";
    /**
     * 存档文档的绝对路径
     */
   public final static String DocDirPath = "/root/wjx/tomcat/apache-tomcat-9.0.29/webapps/pb/files";
//    public final static String DocDirPath = "files";

    /**
     * 存放文章的绝对路径
     */
    public final static String LeisureArticleDirPath = "/root/wjx/tomcat/apache-tomcat-9.0.29/webapps/pb/WEB-INF/leisureArticleHtmls";
    public final static String ProfessionalArticleDirPath = "/root/wjx/tomcat/apache-tomcat-9.0.29/webapps/pb/WEB-INF/professionalArticleHtmls";
//    public final static String LeisureArticleDirPath = "WEB-INF/leisureArticleHtmls";
//    public final static String ProfessionalArticleDirPath = "WEB-INF/professionalArticleHtmls";


    public final static int CmdSuccess = 0;
    public final static int CmdFailure = 1;

    public final static String ArticleLinkPreviousSuffix = "/pb/";
//public final static String ArticleLinkPreviousSuffix = "http://139.199.126.77:8686/pblog/";

}
