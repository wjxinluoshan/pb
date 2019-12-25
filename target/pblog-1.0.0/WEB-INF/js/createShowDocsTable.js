/**
 * 选择文档和删除文档的操作
 */
var whetherAddDocImgClickEvent = true;
var canDocsUpload = false;
var choosedDocsNameFromLocal = [];
var choosedDocsPathFromLocal = [];
//table的列数
var docTableTagColumnNumber = 4;
//当前位置在table列中第几列
var docCurrentIndexInTableColumn = 0;
var docTr = document.createElement('tr');
var docCreateImgTag = function (urlIndex) {
    for (; urlIndex < choosedDocsNameFromLocal.length; urlIndex++) {
        docCurrentIndexInTableColumn++;
        var td = document.createElement('td');
        var img = document.createElement('img');
        if (choosedDocsNameFromLocal[urlIndex].endsWith('.doc'))
            img.setAttribute('src', '/pb/imgs/doc_larger_icon.png');
        else if (choosedDocsNameFromLocal[urlIndex].endsWith('.docx')) {
            img.setAttribute('src', '/pb/imgs/docx_icon.png');
        } else if (choosedDocsNameFromLocal[urlIndex].endsWith('.pdf')) {
            img.setAttribute('src', '/pb/imgs/pdf_icon.png');
        } else if (choosedDocsNameFromLocal[urlIndex].endsWith('.xls')) {
            img.setAttribute('src', '/pb/imgs/xls_icon.png');
        } else if (choosedDocsNameFromLocal[urlIndex].endsWith('.xlsx')) {
            img.setAttribute('src', '/pb/imgs/xlsx_icon.png');
        } else if (choosedDocsNameFromLocal[urlIndex].endsWith('.ppt')) {
            img.setAttribute('src', '/pb/imgs/ppt_icon.png');
        } else {
            img.setAttribute('src', '/pb/imgs/pptx_icon.png');
        }
        img.setAttribute('width', '20%');
        //图片未成功加载出来显示出来的字样
        img.setAttribute('alt', choosedDocsNameFromLocal[urlIndex]);
        //悬浮在图片上现实的字样
        img.setAttribute('title', choosedDocsNameFromLocal[urlIndex]);
        //文档操作的点目事件
        if (whetherAddDocImgClickEvent)
            img.onclick = function () {
                //删除该照片
                if (window.confirm("是否要删除该文档？")) {
                    canDocsUpload = false;
                    docCurrentIndexInTableColumn = 0;
                    docTr = document.createElement('tr');
                    var index = choosedDocsNameFromLocal.indexOf(this.getAttribute('title'));
                    choosedDocsNameFromLocal.splice(index, 1);
                    this.remove();
                    $('#show_docs_table').empty();
                    if (choosedDocsNameFromLocal.length > 0)
                        docCreateImgTag(0);
                    else {
                        $('#choice_docs_input').val('');
                    }
                }

            };
        //ownerMain和main操作的点击事件:   文件的下载
        else {
            img.tag = urlIndex;
            img.canClick = true;
            //单机下载
            img.onclick = function () {
                let img = this;
                if (this.canClick) {
                    if (confirm('是否要下载' + choosedDocsNameFromLocal[img.tag])) ;
                    else return;
                    showOrHiddenLoadingDiv(true);
                    this.canClick = false;
                    let oReq = new XMLHttpRequest();
                    oReq.open("POST", '/pb/data/downloadDoc', true);
                    oReq.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                    oReq.responseType = "blob";
                    oReq.onload = function (Event) {
                        showOrHiddenLoadingDiv(false);
                        let blob = oReq.response;
                        img.canClick = true;
                        let link = document.createElement('a');
                        link.download = choosedDocsNameFromLocal[img.tag];
                        link.href = window.URL.createObjectURL(new Blob([blob]));
                        link.click();
                    };
                    oReq.onerror = function () {
                        showOrHiddenLoadingDiv(false);
                        img.canClick = true;
                    };
                    oReq.send('filePath=' +  encodeURI(encodeURI(choosedDocsPathFromLocal[img.tag])));
                    // oReq.send('filePath=' + choosedDocsPathFromLocal[img.tag]);
                }
            };
        }

        td.append(img);
        td.append(document.createElement("br"));
        var span = document.createElement("span");
        span.innerHTML = choosedDocsNameFromLocal[urlIndex];
        td.append(span);
        docTr.append(td);
        //换行，新生成trTag
        if (docCurrentIndexInTableColumn % docTableTagColumnNumber === 0) {
            $('#show_docs_table').append(docTr);
            docTr = document.createElement('tr');
            docCurrentIndexInTableColumn = 0;
        }
    }
    if (docCurrentIndexInTableColumn < docTableTagColumnNumber)
        $('#show_docs_table').append(docTr);
    canDocsUpload = true;
};