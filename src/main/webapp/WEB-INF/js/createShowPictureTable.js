/**
 * 选择图片和删除图片的操作
 */
var whetherAddPictureImgClickEvent = true;
var canUpload = false;
//已经选择的照片；存放为照片的base64编码
var choosedPicturesUrlFromLocal = [];
var choosedPicturesNameFromLocal = [];
//table的列数
var tableTagColumnNumber = 4;
//当前位置在table列中第几列
var currentIndexInTableColumn = 0;
var tr = document.createElement('tr');
var createImgTag = function (urlIndex) {
    for (; urlIndex < choosedPicturesUrlFromLocal.length; urlIndex++) {
        currentIndexInTableColumn++;
        var td = document.createElement('td');
        var img = document.createElement('img');
        img.setAttribute('src', choosedPicturesUrlFromLocal[urlIndex]);
        img.setAttribute('width', '80%');
        //图片未成功加载出来显示出来的字样
        img.setAttribute('alt', choosedPicturesNameFromLocal[urlIndex]);
        //悬浮在图片上现实的字样
        img.setAttribute('title', choosedPicturesNameFromLocal[urlIndex]);
        //给图片添加点击事件
        if (whetherAddPictureImgClickEvent)
            img.onclick = function () {
                //删除该照片
                if (window.confirm("是否要删除该照片？")) {
                    canUpload = false;
                    currentIndexInTableColumn = 0;
                    tr = document.createElement('tr');
                    var index = choosedPicturesNameFromLocal.indexOf(this.getAttribute('title'));
                    choosedPicturesUrlFromLocal.splice(index, 1);
                    choosedPicturesNameFromLocal.splice(index, 1);
                    this.remove();
                    $('#show_pictures_table').empty();
                    if (choosedPicturesUrlFromLocal.length > 0)
                        createImgTag(0);
                    else {
                        $('#choice_photos_input').val('');
                    }
                }
            };
        /**
         * 点击下载
         */
        else {
            img.tag = urlIndex;
            img.onclick = function () {
                if (confirm('是否要下载' + choosedPicturesNameFromLocal[this.tag])) ;
                else return;

                //'data:image/jpeg;base64,'
                let parts = choosedPicturesUrlFromLocal[this.tag].split(";base64,");
                //mage/jpeg
                let contentType = parts[0].split(':')[1];
                //base64编码进行解码
                let raw = window.atob(parts[1]);
                let rawLength = raw.length;
                let uInt8Array = new Uint8Array(rawLength);
                for (let i = 0; i < rawLength; ++i) {
                    uInt8Array[i] = raw.charCodeAt(i);
                }
                let link = document.createElement('a');
                link.download = choosedPicturesNameFromLocal[this.tag];
                link.href = window.URL.createObjectURL(new Blob([uInt8Array], {
                    type: contentType
                }));
                link.click();
            };
        }
        /**
         * 放大图片：在td中
         */
        let magnifyImg = document.createElement('img');
        magnifyImg.url = img.getAttribute('src');
        magnifyImg.setAttribute('src', '/imgs/magnify_icon.png');
        magnifyImg.setAttribute('id', 'magnifyImg');
        magnifyImg.style.width = '10%';
        magnifyImg.style.visibility = 'hidden';
        magnifyImg.onmouseenter = function () {
            this.style.cursor = 'pointer';
        };
        magnifyImg.onclick = function () {
            let magnifiedImgDiv = $('<div class="magnify_img_div" id="magnify_img_div">' +
                '<div  style="background: #00A2E8;height: max-content"><img  id="failure_img" style="width: 5%" src="/imgs/failure_icon.png" /><img  id="magnify_img" style="width: 5%;margin-left: 40%;;padding: 0" src="/imgs/magnify_icon.png" /><img  id="shrink_img" style="width: 4%;padding: 0;margin-top: 0" src="/imgs/shrink_icon.png" />' +
                '</div>' +
                '<div id="img_div" style="width: 100%;height: 100%;overflow: auto"><img draggable="false" id="show_img" src="' + this.url + '" style="width: 100%" />' +
                '</div>' +
                '</div>');
            $('body').append(magnifiedImgDiv);
            $('#magnify_img_div').css({
                'position': 'fixed',
                'z-index': '10',
                'width': '70%',
                'height': '80%',
                'top': '1%',
                'left': '50%',
                'transform': 'translateX(-50%)'
            });
            /**
             * 缩放图片
             */
            let width = 100;
            $('#magnify_img').click(() => {
                if (width === 500)
                    return;
                width += 50;
                $('#show_img').css({'width': width + '%'});
            });
            $('#shrink_img').click(() => {
                if (width === 100)
                    return;
                width -= 50;
                $('#show_img').css({'width': width + '%'});
            });
            /**
             * 删除图片展示div
             */
            $('#failure_img').click(() => {
                $('#magnify_img_div').remove();
            });
            /**
             * 改变控制img的cursor
             */
            let initIndicatorImgMouseEnterEvent = function (imgIdArr) {
                imgIdArr.forEach(function (value) {
                    $('#' + value).mouseenter(function () {
                        $(this).css('cursor', 'pointer');
                    })
                })
            };
            initIndicatorImgMouseEnterEvent(['failure_img', 'magnify_img', 'shrink_img']);
            //是否可以移动照片
            let ifMovingImg = false;
            //起始移动位置
            let startX = 0;
            let startY = 0;
            //记录上子鼠标在img上的坐标位置 x.y
            let previousCoor = [0, 0];
            //显示照片的主div
            let imgDIv = document.getElementById('img_div');
            /**
             * 鼠标在浏览的img上的鼠标点击事件
             */
            $('#show_img').mousedown((event) => {
                ifMovingImg = true;
                $('#show_img').css({'cursor': 'grab'});
                previousCoor[0] = startX = event.originalEvent.x;
                previousCoor[1] = startY = event.originalEvent.y;
            });
            //是否重载变量  yD, xD;
            let overridXYD = true;
            //记录鼠标在 x y 方向的状态是否维持
            let yD, xD;
            $('#show_img').mousemove(function (event) {
                //照片移动
                if (ifMovingImg) {
                    //重载xD ,yD
                    if (overridXYD) {
                        yD = event.originalEvent.y - previousCoor[1] > 0;
                        xD = event.originalEvent.x - previousCoor[0] > 0;
                        overridXYD = false;
                    }

                    /**
                     * 如果说本次鼠标移动的方向在 x , y 上同上次发生了变化：
                     *    -重新设置起始移动位置为上一次鼠标所处的 x,y位置
                     *    -重载xD , yD
                     */
                    if (event.originalEvent.y - previousCoor[1] > 0 !== yD) {
                        startY = previousCoor[1];
                        overridXYD = true;
                    }
                    if (event.originalEvent.x - previousCoor[0] > 0 !== xD) {
                        startX = previousCoor[0];
                        overridXYD = true;
                    }

                    /**
                     *鼠标移动偏移量设置
                     */
                    let offsetTop = (event.originalEvent.y - startY) / 10;
                    let offsetLeft = (event.originalEvent.x - startX) / 10;
                    //鼠标上一次所在的位置
                    previousCoor[0] = event.originalEvent.x;
                    previousCoor[1] = event.originalEvent.y;
                    //图片在有效的情况下将进行拖拽效果
                    imgDIv.scrollTop += offsetTop;
                    imgDIv.scrollLeft += offsetLeft;
                }
            });
            /**
             * 如果说鼠标不在img上img处于可拖拽状态时，修改其
             */
            $('#show_img').mouseup(function () {
                if (ifMovingImg) {
                    ifMovingImg = false;
                    $(this).css('cursor', 'default')
                }
            });
            $('#show_img').mouseleave(function () {
                if (ifMovingImg) {
                    ifMovingImg = false;
                    $(this).css('cursor', 'default')
                }
            });
        };
        /**
         * 当鼠标在和不在main显示的img上的时候的事件处理
         */
        td.onmouseenter = function () {
            magnifyImg.style.visibility = 'visible';
        };
        td.onmouseleave = function () {
            magnifyImg.style.visibility = 'hidden';
        };

        td.append(img);
        td.append(magnifyImg);
        tr.append(td);
        //换行，新生成trTag
        if (currentIndexInTableColumn % tableTagColumnNumber === 0) {
            $('#show_pictures_table').append(tr);
            tr = document.createElement('tr');
            currentIndexInTableColumn = 0;
        }
    }
    if (currentIndexInTableColumn < tableTagColumnNumber)
        $('#show_pictures_table').append(tr);
    canUpload = true;
};