package com.ajobs.mappers;

import com.ajobs.tabledatas.ArticleInfo;
import com.ajobs.tabledatas.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleInfoMapper implements RowMapper<ArticleInfo> {
    public ArticleInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setArticleTitle(rs.getString("articleTitle"));
        articleInfo.setArticleShortContent(rs.getString("articleShortContent"));
        articleInfo.setArticleFirstImageUrl(rs.getString("articleFirstImageUrl"));
        articleInfo.setArticleLocationLink(rs.getString("articleLocationLink"));
        return articleInfo;
    }
}