/**
 * 加载标题格言图信息
 * 返回信息： titleName $mSplitString dictum $mSplitString titleBg
 */
var loadTitleDictumDInfoAjax = function () {
    $.ajax({
        method: 'POST',
        url: 'http://' + IP + ':' + PORT_PATH + '/data/getTitleDictumDInfo',
        // data: sendDataObj,
        dataType: 'text',
        success: function (data) {
            if (data !== FAILURE) {
                var dataArr = data.split(' pblog ');
                if (dataArr[0] && dataArr[0] !== 'null') {
                    $('#title_div').html(dataArr[0]);
                }
                if (dataArr[1] && dataArr[1] !== 'null') {
                    $('#dictum_div').html(dataArr[1]);
                }
                if (dataArr[2] && dataArr[2] !== 'null') {
                    $('#top_bg_img').attr('src', dataArr[2]);
                }
            }
        }
    });
};
// loadTitleDictumDInfoAjax();
/**
 *装饰
 */
var resizeCanvasWHWhenWindowResize = (canvas) => {
    canvas.setAttribute('width', document.body.clientWidth * 0.08);
    canvas.setAttribute('height', document.body.clientHeight * 0.98);
};
window.addEventListener('resize', () => {
    resizeCanvasWHWhenWindowResize(canvas);
});

var currentPositonOfMouse = [];
window.addEventListener('mousemove', (event) => {
    currentPositonOfMouse[0] = event.pageX;
    currentPositonOfMouse[1] = event.pageY;
});

function Ball(x, y, radius, t) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.t = t;
    this.initY = this.y;
    this.counter = 1;
    this.r = Math.floor(Math.random() * 256);
    this.g = Math.floor(Math.random() * 256);
    this.b = Math.floor(Math.random() * 256);
    this.update = function () {
        ctx.fillStyle = 'rgb(' + this.r + ',' + this.g + ',' + this.b + ')';
        ctx.beginPath();
        this.y = this.initY + 0.5 * 10 * Math.pow((this.counter++) * t, 2);
        let radius = this.radius;
        let offsetDis = 10;
        if (currentPositonOfMouse.length > 0) {
            if (this.x - offsetDis < currentPositonOfMouse[0] && this.x + this.radius + offsetDis > currentPositonOfMouse[0] &&
                this.y - offsetDis < currentPositonOfMouse[1] && this.y + this.radius + offsetDis > currentPositonOfMouse[1]) {
                radius = this.radius + 6;
            } else {
                radius = this.radius;
            }
        }
        ctx.arc(this.x, this.y, radius, 0, Math.PI * 2, true);
        ctx.fill();
        if (this.y > canvas.height) {
            let xMin = 10,
                xMax = canvas.width - 10,
                yStart = 20;
            this.x = Math.ceil(Math.random() * (xMax - xMin) + xMin);
            this.y = 0;
            this.counter = 1;
            this.r = Math.floor(Math.random() * 256);
            this.g = Math.floor(Math.random() * 256);
            this.b = Math.floor(Math.random() * 256);
            this.radius = Math.ceil(Math.random() * 3 + 3);
        }
    };
}

var ballArray = [];
// canvas绘画区域
var canvas = document.getElementById('decorate_canvas');
var ctx;
resizeCanvasWHWhenWindowResize(canvas);

if (canvas.getContext) {
    ctx = canvas.getContext('2d');
    // drawing code here
    let offset = 0;

    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        ctx.setLineDash([4, 2]);
        ctx.lineDashOffset = -offset;
        ctx.strokeStyle = "#ED1C24";
        ctx.strokeRect(10, 10, canvas.width - 20, 10);

        ctx.strokeStyle = "#000";
        ctx.beginPath();
        let h = 100;
        ctx.moveTo(5, canvas.height - h);
        ctx.lineTo(10, canvas.height - h + 20);
        ctx.lineTo(canvas.width / 2, canvas.height - h + 70);
        ctx.lineTo(canvas.width - 10, canvas.height - h + 20);
        ctx.lineTo(canvas.width - 5, canvas.height - h);
        ctx.lineTo(canvas.width - 20, canvas.height - h);
        ctx.lineTo(canvas.width - 20, canvas.height - h + 20);
        ctx.lineTo(canvas.width / 2, canvas.height - h + 30);
        ctx.lineTo(20, canvas.height - h + 20);
        ctx.lineTo(20, canvas.height - h);
        ctx.closePath();
        ctx.stroke();

        ctx.setLineDash([]);
        ballArray.forEach((value, index) => {
            value.update();
        })
    }

    function march() {
        offset++;
        if (offset > 16) {
            offset = 0;
        }
        draw();
        setTimeout(march, 20);
    }

    march();

    function createBall() {
        let xMin = 10,
            xMax = canvas.width - 10,
            yStart = 20;
        let x = Math.ceil(Math.random() * (xMax - xMin) + xMin);
        if (ballArray.length < 30) {
            let radius = Math.ceil(Math.random() * 3 + 3);
            let ball = new Ball(x, yStart + radius / 2, radius, 0.05);
            ballArray.push(ball);
        } else {
            return
        }
        setTimeout(createBall, 200);
    }

    createBall();

} else {
    // canvas-unsupported code here
    canvas.style.display = 'none';
}
/**
 *动画
 */
$('body').css("opacity", "0");
$('body').animate({
    opacity: 1
}, 2000);
$('.title_div').animate({
    top: "100px"
}, 1000, function () {
    $(this).css("color", "red");
});
$('.top_bg_img').animate({
    marginLeft: "50%"
}, 1000, function () {
    $(this).css("color", "red");
    requestUserInfo();
    if (loadTitleDictumDInfoAjax)
        loadTitleDictumDInfoAjax();
});
//存放加载的用户信息
var userInfo = [];
var loadAjax = "";
/**
 *获取用户数据
 */
var requestUserInfo = function () {
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
        method: 'POST',
        url: 'http://' + IP + ':' + PORT_PATH + '/userInfo/userInfoRequire',
        dataType: 'text',
        success: function (data) {
            if (data === FAILURE) {
                if (confirm("数据加载失败可能你没上传相关信息，是否要重新加载？")) {
                    requestUserInfo();
                    return;
                } else {
                    return
                }
            }
            //成功
            userInfo = data.split(" pblog ");
            //设置profile img
            $('#mylogo_img').attr('src', userInfo[0]);
        }
    }).fail(function () {
        if (confirm("网络请求失败，是否要重新请求？")) {
            requestUserInfo();
        }
    });
};

/**
 *添加img动画
 */
var habitImg = $("#habit_img");
var habitImgW = habitImg.innerWidth();
var extendSize = 2;
var duration = 500;
var totalDuration = 1100;
var habitImgTimer = setInterval(function () {
    habitImg.animate({
        width: habitImgW + extendSize + "%"
    }, duration);
    habitImg.animate({
        width: habitImgW + "%"
    }, duration);
}, totalDuration);

/**
 *添加和删除简介和爱好信息内容标签
 */
var creatContentTag = function (className, classStyleName, styleName, htmlData) {
    let ele = $("<div class=" + className + " style=" + styleName + "></div>");
    //数据填充
    ele.html(htmlData);
    $('body').append(ele);
};
var removeContentTag = function (classStyleName) {
    if ($(classStyleName))
        $(classStyleName).remove();

};
/**
 *鼠标进入进出的监听处理事件（简介和爱好）
 */
$('#introduce_span').mouseenter(() => {
    creatContentTag('content_introduce_div', '.content_introduce_div', "'width:20%;height:10%;overflow-y:auto;" +
        "border: 3px solid #BADA55; background: lightsalmon;opacity: 0.5;" +
        "border-radius: 10px;position: fixed;margin-left: 13%;margin-top: 23%'", userInfo[1]);
});
$('#introduce_span').mouseleave(() => {
    removeContentTag('.content_introduce_div');
});
$('#habit_span').mouseenter(() => {
    creatContentTag('content_introduce_div', '.content_introduce_div', "'width:20%;height:10%;overflow-y:auto;" +
        "border: 3px solid red; background: lightsalmon;opacity: 0.5;" +
        "border-radius: 10px;position: fixed;margin-left: 13%;margin-top: 23%'", userInfo[2]);
});
$('#habit_span').mouseleave(() => {
    removeContentTag('.content_introduce_div');
});

/**
 * 模块处的点击事件
 */
var choiceLeisureArticleModule = 1;
var choiceProfessionalArticleModule = 2;
var choicePhotoModule = 3;
var choiceDocModule = 4;

var chooseArticleType;
var leisureModuleCanClick = true;
var professionalModuleCanClick = true;
var photoModuleCanClick = true;
var docModuleCanClick = true;
var moduleSpanIdArray = ['leisureArticle_span', 'professionalArticle_span', 'photos_span', 'docs_span'];
var setTheModuleSpanTextFWAndColorChangeCmd = function (jQueryEleId) {
    let change = function (jqId, color, fontWeight) {
        let id = '#' + jqId;
        $(id).css('color', color);
        $(id).css('font-weight', fontWeight);
    };
    for (let index = 0; index < moduleSpanIdArray.length; index++) {
        if (moduleSpanIdArray[index] === jQueryEleId) {
            change(jQueryEleId, '#FF7F27', 'bold');
            continue;
        }
        change(moduleSpanIdArray[index], '#000', 'normal');
    }
};
var setTheModulesWhetherClickFunc = function (leisure, professional, photo, doc) {
    leisureModuleCanClick = leisure;
    professionalModuleCanClick = professional;
    photoModuleCanClick = photo;
    docModuleCanClick = doc;
};
var theLeisureCurrentPageIndex = 1;
//休闲文章
$("#leisureArticle_span").on("click", function () {
    if (!leisureModuleCanClick)
        return;
    else {
        chooseArticleType = 'leisure';
        setTheModulesWhetherClickFunc(false, true, true, true)
    }
    showOrHidePagingDiv(choiceLeisureArticleModule);
    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getLeisureArticles', theLeisureCurrentPageIndex);
});
var theProfessionalCurrentPageIndex = 1;
//专业文章
$("#professionalArticle_span").on("click", function () {
    if (!professionalModuleCanClick)
        return;
    else {
        chooseArticleType = 'professional';
        setTheModulesWhetherClickFunc(true, false, true, true)
    }
    showOrHidePagingDiv(choiceProfessionalArticleModule);
    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getProfessionalArticles', theProfessionalCurrentPageIndex);
});
//用于加载文章的逻辑操作
var modifyArticleContentAjax;
var loadArticleData = function (url, pageNum) {
    showOrHiddenLoadingDiv(true);
    //根据当前的下标来请求数据的加载
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
        method: 'POST',
        url: url,
        data: {pageNumber: pageNum.toString()},
        dataType: 'text',
        success: function (data) {
            showOrHiddenLoadingDiv(false);
            //得到返回的数据
            //返回的数据形式为：
            //    空字符串；
            //     articleTitle，articleShortContent，articleFirstImageUrl,articleLocationLink，articleTitle....
            //数据有效
            if (data !== FAILURE) {
                $('.content_show_div').empty();
                //处理数据
                var dataArr = data.split(" pblog ");
                var judgeNumber = 0;
                //存放返回数据的小分支数据
                var valueArr = [];
                dataArr.forEach(function (value, index) {
                    judgeNumber++;
                    valueArr.push(value);
                    if (judgeNumber % 4 === 0) {
                        //生成一个元素展示数据并将其添加到父元素中
                        let aDiv = document.createElement('div');
                        aDiv.setAttribute('class', 'article_choice_ele_div');
                        //文章title
                        let a = document.createElement('a');
                        a.setAttribute('class', 'article_choice_ele_a');
                        a.innerHTML = valueArr[0];
                        a.href = valueArr[3];
                        //文章第一张缩略图
                        let img = document.createElement('img');
                        img.setAttribute('class', 'article_choice_ele_img');
                        //文章存在缩略图时
                        if (valueArr[2])
                            img.setAttribute('src', valueArr[2]);
                        //文章缩略图不存在时随机在四张指定的图片中填补
                        else {
                            switch (Math.floor(Math.random() * 4)) {
                                case 0:
                                    img.setAttribute('src', "/imgs/smile_icon_0.png");
                                    break;
                                case 1:
                                    img.setAttribute('src', "/imgs/smile_icon_1.png");
                                    break;
                                case 2:
                                    img.setAttribute('src', "/imgs/smile_icon_2.png");
                                    break;
                                case 3:
                                    img.setAttribute('src', "/imgs/smile_icon_3.png");
                                    break;
                            }
                        }
                        //文章简短内容
                        let textarea = document.createElement('div');
                        textarea.setAttribute('class', 'article_choice_ele_textarea');
                        textarea.innerHTML = valueArr[1];
                        aDiv.append(a);
                        aDiv.append(img);
                        aDiv.append(textarea);
                        /**
                         * 当在ownerMain才能编辑文章
                         */
                        if (window.location.href.includes('ownerMain')) {
                            /*
                             *编辑文章
                             */
                            let editButton = document.createElement('button');
                            editButton.innerHTML = '编辑文章';
                            editButton.style.marginLeft = '17%';
                            editButton.style.marginTop = '2%';
                            editButton.style.marginBottom = '0.5%';
                            editButton.canClick = true;
                            editButton.onclick = function () {
                                if (this.canClick) {
                                    this.canClick = false
                                } else {
                                    return
                                }
                                if (modifyArticleContentAjax)
                                    modifyArticleContentAjax.abort();
                                showOrHiddenLoadingDiv(true);
                                let modifyFunc = function () {
                                    modifyArticleContentAjax = $.ajax({
                                        method: 'POST',
                                        url: 'http://' + IP + ':' + PORT_PATH + '/article/loadArticleContentOfHtml',
                                        data: {data: a.href.toString()},
                                        dataType: 'text',
                                        success: function (data) {
                                            showOrHiddenLoadingDiv(false);
                                            if (data === SUCCESS) {
                                                editButton.canClick = true;
                                                window.location.href = 'http://' + IP + ':' + PORT_PATH + '/owner/ownerModifyArticle.html';
                                            } else {
                                                if (confirm("网页加载失败，重新加载？？？")) {
                                                    modifyFunc();
                                                } else {
                                                    editButton.canClick = true;
                                                }
                                            }
                                        }
                                    }).fail(function () {
                                        showOrHiddenLoadingDiv(false);
                                        if (confirm("网络加载失败，重新加载？？？"))
                                            modifyFunc();
                                        else
                                            editButton.canClick = true;
                                    });
                                };
                                modifyFunc()
                            };
                            aDiv.append(editButton);
                            /*
                             *删除文章
                             */
                            let deleteButton = document.createElement('button');
                            deleteButton.innerHTML = '删除文章';
                            deleteButton.style.marginLeft = '5%';
                            deleteButton.style.marginTop = '2%';
                            deleteButton.style.marginBottom = '0.5%';
                            deleteButton.canClick = true;
                            deleteButton.articleLocationLink = valueArr[3];
                            deleteButton.onclick = function () {
                                if (this.canClick) {
                                    this.canClick = false;
                                    showOrHiddenLoadingDiv(true);
                                    let deleteArticleAjaxFunc = function (articleLocationLink) {
                                        $.ajax({
                                            method: 'POST',
                                            url: 'http://' + IP + ':' + PORT_PATH + '/data/deleteAppointArticle',
                                            data: {
                                                articleType: chooseArticleType,
                                                articleLocationLink: articleLocationLink
                                            },
                                            dataType: 'text',
                                            success: function (data) {
                                                showOrHiddenLoadingDiv(false);
                                                deleteButton.canClick = true;
                                                if (data !== FAILURE) {
                                                    loadArticleData(url, pageNum);
                                                } else
                                                    alert("删除失败！！！")
                                            }
                                        }).fail(function () {
                                            showOrHiddenLoadingDiv(false);
                                            if (confirm("网络请求失败，重新请求？？？")) {
                                                deleteArticleAjaxFunc();
                                            } else {
                                                deleteButton.canClick = true;
                                            }
                                        });
                                    };
                                    deleteArticleAjaxFunc(this.articleLocationLink);
                                }
                            };
                            aDiv.append(deleteButton);
                        }
                        $('.content_show_div').append(aDiv);
                        valueArr.length = 0;
                    }
                });
                if (url.includes('getLeisureArticles')) {
                    theLeisureCurrentPageIndex = parseInt(pageNum);
                    //改变模块的字样
                    setTheModuleSpanTextFWAndColorChangeCmd(moduleSpanIdArray[0]);
                } else {
                    theProfessionalCurrentPageIndex = parseInt(pageNum);
                    //改变模块的字样
                    setTheModuleSpanTextFWAndColorChangeCmd(moduleSpanIdArray[1]);
                }
                //设置分页区域的变量
                pagingVariableSettingFunc(parseInt(pageNum));
            } else {
                if (confirm("您可能没有写文章或者说加载失败，重新加载？？？")) {
                    loadArticleData(url, pageNum);
                } else {
                    if (chooseArticleType) {
                        chooseArticleType = '';
                        $('.content_show_div').empty();
                        $('#paging_area_div').css('display', 'none')
                    }
                }

            }
        }
    }).fail(function () {
        showOrHiddenLoadingDiv(false);
        if (confirm("网络加载失败，重新加载？？？"))
            loadArticleData(url, pageNum);
    });
};

var thePhotoCurrentPageIndex = 1;
//照片
$("#photos_span").on("click", function () {
    if (!photoModuleCanClick)
        return;
    else {
        setTheModulesWhetherClickFunc(true, true, false, true)
    }
    showOrHidePagingDiv(choicePhotoModule);
    loadPhotosData(thePhotoCurrentPageIndex);
});
//加载照片的逻辑操作
var loadPhotosData = function (pageNum) {
    showOrHiddenLoadingDiv(true);
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
        method: 'POST',
        url: 'http://' + IP + ':' + PORT_PATH + '/data/getPicturesInfo',
        data: {pageNumber: pageNum.toString()},
        dataType: 'text',
        success: function (data) {
            showOrHiddenLoadingDiv(false);
            //得到返回的数据:
            // 返回的形式  pictureUrl，pictureName，pictureUrl....
            //数据有效
            if (data !== FAILURE) {
                /*
                 *   加载成功后删除原先的内容显示区域
                 */
                $('.content_show_div').empty();
                var showPictureTable = document.createElement('table');
                showPictureTable.setAttribute('class', 'show_pictures_table');
                showPictureTable.setAttribute('id', 'show_pictures_table');
                $('.content_show_div').append(showPictureTable);
                //清空上一页的数据
                choosedPicturesNameFromLocal.length = 0;
                choosedPicturesUrlFromLocal.length = 0;
                //分析数据
                var dataArr = data.split(' pblog ');
                for (let index = 0; index < dataArr.length; index++) {
                    if (index % 2 === 0)
                        choosedPicturesUrlFromLocal.push(dataArr[index]);
                    else
                        choosedPicturesNameFromLocal.push(dataArr[index]);
                }
                //不给动态加载的img添加点击事件
                whetherAddPictureImgClickEvent = false;
                tr = document.createElement('tr');
                currentIndexInTableColumn = 0;
                createImgTag(0);

                thePhotoCurrentPageIndex = parseInt(pageNum);
                setTheModuleSpanTextFWAndColorChangeCmd(moduleSpanIdArray[2]);
                //设置分页区域的变量
                pagingVariableSettingFunc(parseInt(pageNum));
            } else {
                if (confirm("您可能没有放照片或者说加载失败，重新加载？？？"))
                    loadPhotosData(pageNum);
            }
        }
    }).fail(function () {
        showOrHiddenLoadingDiv(false);
        if (confirm("网络加载失败，重新加载？？？"))
            loadPhotosData(pageNum);
    });
};

var theDocCurrentPageIndex = 1;
//文档
$("#docs_span").on("click", function () {
    if (!docModuleCanClick)
        return;
    else {
        setTheModulesWhetherClickFunc(true, true, true, false)
    }
    showOrHidePagingDiv(choiceDocModule);
    loadDocsData(theDocCurrentPageIndex);
});
//加载文档的逻辑操作
var loadDocsData = function (pageNum) {
    showOrHiddenLoadingDiv(true);
    if (loadAjax)
        loadAjax.abort();
    loadAjax = $.ajax({
            method: 'POST',
            url: 'http://' + IP + ':' + PORT_PATH + '/data/getDocInfo',
            data: {pageNumber: pageNum.toString()},
            dataType: 'text',
            success: function (data) {
                console.log("612"+data)
                showOrHiddenLoadingDiv(false);
                /*
                 *   加载成功后删除原先的内容显示区域
                 *   docFileName，docFileName....
                 */
                if (data !== FAILURE) {
                    $('.content_show_div').empty();
                    var showPictureTable = document.createElement('table');
                    showPictureTable.setAttribute('class', 'show_pictures_table');
                    showPictureTable.setAttribute('id', 'show_docs_table');
                    $('.content_show_div').append(showPictureTable);
                    //清空上一页的数据
                    choosedDocsNameFromLocal.length = 0;
                    //分析数据
                    var dataArr = data.split(SPLIT_STRING);
                    for (let index = 0; index < dataArr.length; index++) {
                        if (index % 2 !== 0) {
                            choosedDocsNameFromLocal.push(dataArr[index]);
                        } else {
                            choosedDocsPathFromLocal.push(dataArr[index])
                        }
                    }
                    whetherAddDocImgClickEvent = false;
                    docCurrentIndexInTableColumn = 0;
                    docTr = document.createElement('tr');
                    docCreateImgTag(0);

                    theDocCurrentPageIndex = parseInt(pageNum);
                    setTheModuleSpanTextFWAndColorChangeCmd(moduleSpanIdArray[3]);
                    //设置分页区域的变量
                    pagingVariableSettingFunc(parseInt(pageNum));
                } else {
                    if (confirm("您可能没存放文档或者说加载失败，重新加载？？？"))
                        loadDocsData(pageNum);
                }
            }
        }
    ).fail(function () {
        showOrHiddenLoadingDiv(false);
        if (confirm("网络加载失败，重新加载？？？"))
            loadDocsData(pageNum);
    });
};

/**
 * 显示分页
 */
//存放模块数据所拥有的页数
// var pageNumberIndexArray = [];
var pageTotalNumber = 0;
var pagingSpanArray = [];
var loadModuleDataEleTotalNumberAjax;
/**
 * 分页字样的变化
 * @param currentSpanIndex
 * @param spanInnerHtmlEndIndex
 * @param startIndexValue
 */
var pagingSpanTextChangeFunc = function (currentSpanIndex, spanInnerHtmlEndIndex, startIndexValue) {
    if ($('#paging_area_div').css('display') === 'none')
        $('#paging_area_div').css('display', 'block');
    pagingSpanArray[currentSpanIndex].style.color = '#263238';
    pagingSpanArray[currentSpanIndex].canClick = false;
    for (let index = 0; index <= spanInnerHtmlEndIndex; index++) {
        pagingSpanArray[index].innerHTML = (startIndexValue++).toString();
        if (index !== currentSpanIndex && !pagingSpanArray[index].canClick) {
            pagingSpanArray[index].style.color = '#4285f4';
            pagingSpanArray[index].canClick = true;
        }
    }
    for (let index = spanInnerHtmlEndIndex + 1; index < PAGING_MAXIMUM; index++) {
        if (pagingSpanArray[index].innerHTML)
            pagingSpanArray[index].innerHTML = '';
        pagingSpanArray[index].style.color = '#4285f4';
        pagingSpanArray[index].canClick = true;
    }
};
/**
 * 分页span相关变量的修改，逻辑分页
 * @param pageNumber
 */
var pagingVariableSettingFunc = function (pageNumber) {
    if (pageNumber === 1) {
        $('#left_arrow_img').css('visibility', 'hidden');
        $('#right_arrow_img').css('visibility', 'hidden');
        if (pageTotalNumber !== 1)
            $('#right_arrow_img').css('visibility', 'visible');
    } else if (pageNumber === pageTotalNumber) {
        $('#left_arrow_img').css('visibility', 'visible');
        $('#right_arrow_img').css('visibility', 'hidden');
    } else {
        if ($('#left_arrow_img').css('visibility') === 'hidden')
            $('#left_arrow_img').css('visibility', 'visible');
        if ($('#right_arrow_img').css('visibility') === 'hidden')
            $('#right_arrow_img').css('visibility', 'visible');
    }

    /**
     * 在这里实现span innerHtml 的改变还有颜色的改变
     */
    //当span的数量<=PAGING_MAXIMUM
    if (pageTotalNumber <= PAGING_MAXIMUM) {
        let clickedSpanIndexInArr = pageNumber % PAGING_MAXIMUM - 1;
        if (clickedSpanIndexInArr < 0) {
            clickedSpanIndexInArr = pageTotalNumber - 1;
        }
        pagingSpanTextChangeFunc(clickedSpanIndexInArr, pageTotalNumber - 1, 1);
    } else {
        //当clickedSpanIndexInArr在Math.floor(PAGING_MAXIMUM/2)
        //左移
        let middleIndex = Math.floor(PAGING_MAXIMUM / 2);
        if (pageNumber > parseInt(pagingSpanArray[middleIndex].innerHTML)) {
            let endSpanIndex;
            let startIndexValue;
            if (pageNumber + PAGING_MAXIMUM - 1 - middleIndex > pageTotalNumber) {
                endSpanIndex = middleIndex + pageTotalNumber - pageNumber;
            } else {
                endSpanIndex = PAGING_MAXIMUM - 1;
            }
            startIndexValue = pageNumber - middleIndex;
            pagingSpanTextChangeFunc(middleIndex, endSpanIndex, startIndexValue);
        }
        //右移
        else if (pageNumber < parseInt(pagingSpanArray[middleIndex].innerHTML)) {
            let endSpanIndex;
            let startIndexValue;
            if (pageNumber - middleIndex <= 0) {
                startIndexValue = 1;
            } else {
                startIndexValue = pageNumber - middleIndex;
            }
            if (pageNumber + PAGING_MAXIMUM - 1 - middleIndex > pageTotalNumber) {
                endSpanIndex = middleIndex + pageTotalNumber - pageNumber;
            } else {
                endSpanIndex = PAGING_MAXIMUM - 1;
            }
            if (startIndexValue === 1)
                pagingSpanTextChangeFunc(pageNumber - 1, endSpanIndex, startIndexValue);
            else {
                pagingSpanTextChangeFunc(middleIndex, endSpanIndex, startIndexValue);
            }
        }
        //不移动
        else {
            let clickedSpanIndexInArr = pageNumber % PAGING_MAXIMUM - 1;
            if (clickedSpanIndexInArr < 0) {
                clickedSpanIndexInArr = pageTotalNumber - 1;
            }
            pagingSpanTextChangeFunc(clickedSpanIndexInArr, PAGING_MAXIMUM - 1, 1);
        }

    }

};
/**
 * 加载各模块的数据量和初始化span数组
 * @param url
 * @param choiceModuleType
 */
var loadModuleDataEleTotalNumberFunc = function (url, choiceModuleType) {
    // pageNumberIndexArray.length = 0;
    $('#paging_tr').empty();
    pagingSpanArray.length = 0;
    // if (pagingSpanArray.length === 0)
    for (let pageNumber = 0; pageNumber < PAGING_MAXIMUM; pageNumber++) {
        let td = document.createElement('td');
        td.setAttribute('class', 'paging_td');
        let span = document.createElement('span');
        span.innerHTML = '';
        span.canClick = true;
        span.onclick = function (e) {
            if (!this.canClick || !this.innerHTML)
                return;
            this.canClick = false;
            switch (choiceModuleType) {
                case choiceLeisureArticleModule:
                    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getLeisureArticles', this.innerHTML);
                    break;
                case choiceProfessionalArticleModule:
                    loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getProfessionalArticles', this.innerHTML);
                    break;
                case choicePhotoModule:
                    loadPhotosData(this.innerHTML);
                    break;
                case choiceDocModule:
                    loadDocsData(this.innerHTML);
                    break;
            }
        };
        pagingSpanArray.push(span);
        td.append(span);
        $('#paging_tr').append(td)
    }
    // pagingSpanArray.length = 0;
    if (loadModuleDataEleTotalNumberAjax)
        loadModuleDataEleTotalNumberAjax.abort();
    loadModuleDataEleTotalNumberAjax = $.ajax({
        method: 'POST',
        url: url,
        dataType: 'text',
        success: function (data) {
            //data:   数目+' pblog '+页单数
            if (data === FAILURE) {
                if (confirm("获取数据数量失败，重新加载？？？")) {
                    loadModuleDataEleTotalNumberFunc(url, choiceModuleType);
                }
                $('#paging_area_div').css('display', 'none');
                return;
            }
            //pagingImg点击事件
            pagingImgClickInit();
            /**
             *最大的tr数据为7
             */
                //得到数据所分的页数
            let dataArr = data.split(' pblog ');
            pageTotalNumber = Math.ceil(parseInt(dataArr[0]) / parseInt(dataArr[1]));
            // for (var index = 1; index <= pageNumber; index++) {
            //     pageNumberIndexArray.push(index);
            // }
        }
    }).fail(function () {
        if (confirm("网络请求失败，是否要重新请求？")) {
            loadModuleDataEleTotalNumberFunc(url, choiceModuleType);
        }
    });
};
/**
 * 分页img的点击事件
 */
var mChoiceModuleType;
var firstInitPagingImgClickEvent = true;
/**
 * paging imgd点击事件的主要处理逻辑
 */
var goToPreviousOrNextPageFunc = function (previous) {
    let pageNum;
    switch (mChoiceModuleType) {
        case choiceLeisureArticleModule:
            if (previous)
                pageNum = --theLeisureCurrentPageIndex;
            else
                pageNum = ++theLeisureCurrentPageIndex;
            loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getLeisureArticlePageNumber', pageNum);
            break;
        case choiceProfessionalArticleModule:
            if (previous)
                pageNum = --theProfessionalCurrentPageIndex;
            else
                pageNum = ++theProfessionalCurrentPageIndex;
            loadArticleData('http://' + IP + ':' + PORT_PATH + '/data/getProfessionalArticlePageNumber', pageNum);
            break;
        case choicePhotoModule:
            if (previous)
                pageNum = --thePhotoCurrentPageIndex;
            else
                pageNum = ++thePhotoCurrentPageIndex;
            loadPhotosData(pageNum);
            break;
        case choiceDocModule:
            if (previous)
                pageNum = --theDocCurrentPageIndex;
            else
                pageNum = ++theDocCurrentPageIndex;
            loadDocsData(pageNum);
            break;
    }
};
var pagingImgClickInit = function () {
    if (firstInitPagingImgClickEvent) {
        firstInitPagingImgClickEvent = false;
        $('#left_arrow_img').on('click', function () {
            goToPreviousOrNextPageFunc(true);
        });
        $('#right_arrow_img').on('click', function () {
            goToPreviousOrNextPageFunc(false);
        });
    }
};
/**
 * 是否显示分页区域：
 *    --显示：则调用 loadModuleDataEleTotalNumberFunc
 * @param choiceModuleType
 * @param hideOrShow
 */
var showOrHidePagingDiv = function (choiceModuleType) {
    mChoiceModuleType = choiceModuleType;
    let url = '';
    switch (choiceModuleType) {
        case choiceLeisureArticleModule:
            url = 'http://' + IP + ':' + PORT_PATH + '/data/getLeisureArticlePageNumber';
            break;
        case choiceProfessionalArticleModule:
            url = 'http://' + IP + ':' + PORT_PATH + '/data/getProfessionalArticlePageNumber';
            break;
        case choicePhotoModule:
            url = 'http://' + IP + ':' + PORT_PATH + '/data/getPicturesTotalNumber';
            break;
        case choiceDocModule:
            url = 'http://' + IP + ':' + PORT_PATH + '/data/getDocsTotalNumber';
            break;
    }
    loadModuleDataEleTotalNumberFunc(url, choiceModuleType);
};
