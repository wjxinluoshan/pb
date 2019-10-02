package com.ajobs.tabledatas;

public class ArticleInfo {
    private String articleTitle;
    private String articleShortContent;
    private String articleFirstImageUrl;
    private String articleLocationLink;

    public String getArticleLocationLink() {
        return articleLocationLink;
    }

    public void setArticleLocationLink(String articleLocationLink) {
        this.articleLocationLink = articleLocationLink;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleShortContent() {
        return articleShortContent;
    }

    public void setArticleShortContent(String articleShortContent) {
        this.articleShortContent = articleShortContent;
    }

    public String getArticleFirstImageUrl() {
        return articleFirstImageUrl;
    }

    public void setArticleFirstImageUrl(String articleFirstImageUrl) {
        this.articleFirstImageUrl = articleFirstImageUrl;
    }
}
