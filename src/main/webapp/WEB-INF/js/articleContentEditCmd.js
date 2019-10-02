// Replace the <textarea id="editor_textarea"> with a CKEditor
// instance, using default configuration.
CKEDITOR.replace('editor_textarea', {height: '500px'});

var articleHTMLContent = '';
var articleShortContent = '';
var articleTitle;
var articleHref='';
//存放的形式为base64编码
var firstThumbnailImgInArticle = '';

//文章类型
var articleType = '休闲文章';

var cais = document.getElementsByClassName('choice_article_input');
for (var index = 0; index < cais.length; index++) {
    cais[index].onclick = function () {
        articleType = this.value;
    }
}
/**
 * 文章数据的提交ajax
 */
var uploadArticleDataAjax;
var inputButtonCanClick = true;
var uploadArticleFuncCmd = function () {
    showOrHiddenLoadingDiv(true);
    //数据传送
    if (uploadArticleDataAjax)
        uploadArticleDataAjax.abort();
    uploadArticleDataAjax = $.ajax({
        method: 'POST',
        url: 'http://' + IP + ':' + PORT_PATH + '/data/uploadArticle',
        data: {
            articleType: articleType,
            articleTitle: articleTitle,
            articleShortContent: articleShortContent,
            firstThumbnailImgInArticle: firstThumbnailImgInArticle,
            articleHTMLContent: articleHTMLContent,
            articleHref:articleHref
        },
        dataType: 'text',
        success: function (data) {
            showOrHiddenLoadingDiv(false);
            inputButtonCanClick = true;
            if (data === FAILURE) {
                if (confirm("文章上传失败，重新上传？？？")) {
                    uploadArticleFuncCmd()
                } else {
                    return
                }
            } else {
                window.location.href = 'http://' + IP + ':' + PORT_PATH + '/owner/ownerMain.html';
            }

        }
    }).fail(function () {
        showOrHiddenLoadingDiv(false);
        if (confirm("网络链接失败，重新链接？？？"))
            uploadArticleFuncCmd();
    });
};
/**
 * 点击上传
 */
$('#upload_docs_button').on('click', function () {
    if (!inputButtonCanClick) {
        alert("上次上传数据的操作正在执行，请稍等!!!");
        return;
    }
    inputButtonCanClick = false;
    var at = $('#article_title_input').val().trim();
    if (!at) {
        alert("请填写文章标题！！！");
        return
    } else {
        articleTitle = at;
    }
    //文章的html数据
    articleHTMLContent = CKEDITOR.instances.editor_textarea.getData();
    //文章的简短内容
    var nodeList = CKEDITOR.instances.editor_textarea.document.getElementsByTag('p');
    for (var index = 0; index < nodeList.count(); index++) {
        articleShortContent = nodeList.getItem(index).$.innerText.trim().substring(0, 60);
        if (articleShortContent) {
            break;
        }
    }
    //文章的第一张缩略图
    var imgNodeList = CKEDITOR.instances.editor_textarea.document.getElementsByTag('img');
    for (var index = 0; index < imgNodeList.count(); index++) {
        firstThumbnailImgInArticle = imgNodeList.getItem(index).$.getAttribute('src');
        break;
    }
    //// config.fullPage = true; 取<body></body>中的字符串
    // var beginIndex = data.indexOf('<body>');
    // var endIndex = data.indexOf('</body>');
    // data = data.substring(beginIndex + 6, endIndex);
    // console.log(data)

    uploadArticleFuncCmd();
});
//粘贴的状态机
var canPaste = true;
//paste img cmd
CKEDITOR.instances.editor_textarea.on('instanceReady', function (event) {
    this.document.on("paste", function (e) {
        if (!canPaste) {
            alert('上次操作未结束!!!');
            return
        }
        canPaste = false;
        var promise = new Promise(function (resolve, reject) {
            //获取该ckeditor实例的所有剪切板数据
            var items = e.data.$.clipboardData.items;
            for (var i = 0; i < items.length; ++i) {
                var item = items[i];
                if (item.kind === 'file' && item.type.includes('image/')) {
                    var imgFile = item.getAsFile();
                    if (!imgFile) {
                        resolve();
                        return;
                    }
                    var reader = new FileReader();
                    reader.readAsDataURL(imgFile);
                    reader.onload = function (e) {
                        if (this.result.includes('base64'))
                            CKEDITOR.instances["editor_textarea"].insertHtml('<img src="' + this.result + '" alt="加载失败!!!" />');
                        resolve();
                    };
                    return;
                }
            }
        });
        promise.then(function () {
            canPaste = true;
        });
    });
});