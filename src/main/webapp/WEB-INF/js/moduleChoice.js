/**
 * 模块处的点击事件
 */
var loadAjax = "";
var theLeisureCurrentPageIndex = 1;
//休闲文章
$("#leisureArticle_span").on("click", function () {
    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getLeisureArticles', theLeisureCurrentPageIndex);
});
var theProfessionalCurrentPageIndex = 1;
//专业文章
$("#professionalArticle_span").on("click", function () {
    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getProfessionalArticles', theProfessionalCurrentPageIndex);
});
//用于加载文章的逻辑操作
var loadArticleData = function (url, pageNum) {
    //根据当前的下标来请求数据的加载
    if (loadAjax)
        loadAjax.abort()
    loadAjax = $.ajax({
        method: 'POST',
        url: url,
        data: pageNum.toString(),
        dataType: 'text',
        success: function (data) {
            //得到返回的数据
            //返回的数据形式为：
            //    空字符串；
            //     articleTitle，articleShortContent，articleFirstImageUrl，articleTitle....
            //数据有效
            if (data) {
                //处理数据
                var dataArr = data.split(",");
                var judgeNumber = 0;
                var valueArr = [];
                dataArr.forEach(function (value, index) {
                    judgeNumber++;
                    valueArr.push(value)
                    if (judgeNumber % 3 === 0) {
                        //生成一个元素展示数据并将其添加到父元素中


                        valueArr.length = 0;
                    }
                })
            } else {
                if (confirm("您可能没有写文章或者说加载失败，重新加载？？？"))
                    loadArticleData(url, pageNum);
            }
        }
    }).fail(function () {
        if (confirm("网络加载失败，重新加载？？？"))
            loadArticleData(url, pageNum);
        else
            return
    });
};

var thePhotoCurrentPageIndex = 1;
//照片
$("#photos_span").on("click", function () {
    loadPhotosData("", thePhotoCurrentPageIndex);
});
//加载照片的逻辑操作
var loadPhotosData = function (url, pageNum) {
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
        method: 'POST',
        url: url,
        data: pageNum.toString(),
        dataType: 'text',
        success: function (data) {
            //得到返回的数据
            //数据有效
            if (data) {

            } else {
                if (confirm("您可能没有放照片或者说加载失败，重新加载？？？"))
                    loadPhotosData(url, pageNum);
                else
                    return
            }
        }
    }).fail(function () {
        if (confirm("网络加载失败，重新加载？？？"))
            loadPhotosData(url, pageNum);
        else
            return
    });
};

var theDocCurrentPageIndex = 1;
//文档
$("#docs_span").on("click", function () {
    loadDocsData("", theDocCurrentPageIndex);
});
//加载文档的逻辑操作
var loadDocsData = function (url, pageNum) {
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
        method: 'POST',
        url: url,
        data: pageNum.toString(),
        dataType: 'text',
        success: function (data) {
            //得到返回的数据
            //数据有效
            if (data) {

            } else {
                if (confirm("您可能没有存放文档或者说加载失败，重新加载？？？"))
                    loadDocsData(url, pageNum);
                else
                    return
            }

        }
    }).fail(function () {
        if (confirm("网络加载失败，重新加载？？？"))
            loadDocsData(url, pageNum);
        else
            return
    });
};