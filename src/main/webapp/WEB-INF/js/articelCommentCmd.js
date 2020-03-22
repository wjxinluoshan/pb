// <!--
// 上传回复信息
// -->
if (window.location.href.toString().includes('leisureArticleHtmls')) {
  $('#show_comment_area_div').css('display', 'none');
}

var publish_span = document.getElementById('publish_span');

// <!--
//   评论信息加载
// -->
/**
 * 存放来自server的相关数据
 */
var fromCommentTableDataArr = [[]];
var fromResponseTableDataArr = [[]];
var responsedCommentId;
var loadCommentDataAjax;
var loadCommentFunc = function () {
  showOrHiddenLoadingDiv(true);
  fromCommentTableDataArr = [[]];
  fromResponseTableDataArr = [[]];
  if (loadCommentDataAjax) {
    loadCommentDataAjax.abort();
  }
  // let tableName = window.location.href.toString().split(".")[0].split("/");
  // tableName = tableName[tableName.length - 1];
    let tableName = window.location.href.toString().split("/")
    tableName = tableName[tableName.length - 1];
    tableName = tableName.split(".")[0];
  /*
  在验证获得表中获取数据
   */
  tableName = tableName + '_verify';

  loadCommentDataAjax = $.ajax({
    method: 'POST',
    url: '/pb/article/getCommentAndResponseData',
    data: {tableName: tableName},
    dataType: 'text',
    success: function (data) {
      showOrHiddenLoadingDiv(false);
      if (data !== FAILURE) {
        /**
         * 开始的分析数据：
         *   " ppbblloogg "
         */
        let dataArr = data.split(" ppbblloogg ");
        //得到评论表的数据
        let commentDataArr = dataArr[0].split(' pblog ');
        let arrIndex = 0;
        commentDataArr.forEach(function (value, index) {
          fromCommentTableDataArr[arrIndex].push(value);
          if ((index + 1) % 4 === 0) {
            arrIndex++;
            if (index + 1 < commentDataArr.length) {
              fromCommentTableDataArr.push([]);
            }
          }
        });
        //得到回复表的数据
        let responseDataArr;
        if (dataArr[1] !== 'null') {
          responseDataArr = dataArr[1].split(' pblog ');
        }
        if (responseDataArr) {
          arrIndex = 0;
          responseDataArr.forEach(function (value, index) {
            fromResponseTableDataArr[arrIndex].push(value);
            if ((index + 1) % 2 === 0) {
              arrIndex++;
              if (index + 1 < responseDataArr.length) {
                fromResponseTableDataArr.push([]);
              }
            }
          });
        }

        //调用数据分析函数并生成html元素
        analyzeDataAndCreateElement();
        //显示评论数目
        if (fromCommentTableDataArr[0].length !== 0) {
          $('#show_comment_number_p').html(
              fromCommentTableDataArr.length + '条评论:');
        }
      }
    }
  }).fail(function () {
    showOrHiddenLoadingDiv(false);
    alert("评论加载失败！！！")
  });
};
var analyzeDataAndCreateElement = function () {
  //如果回复表的数据为空
  if (fromResponseTableDataArr[0].length === 0) {
    fromCommentTableDataArr.forEach(function (commentData, index) {
      analyzeDataAndCreateElementNoPreviousCommentDivFunc(commentData, index);
    });
  }
  //回复表含有数据
  else {
    let rIdCurrentIterateIndex = 0;
    fromCommentTableDataArr.forEach(function (commentData, index) {
      let id = commentData[0];
      let rId;
      if (rIdCurrentIterateIndex < fromResponseTableDataArr.length) {
        rId = fromResponseTableDataArr[rIdCurrentIterateIndex][0];
      } else {
        rId = undefined;
      }
      if (id !== rId) {
        analyzeDataAndCreateElementNoPreviousCommentDivFunc(commentData, index);
      } else {
        let cId = fromResponseTableDataArr[rIdCurrentIterateIndex][1];
        rIdCurrentIterateIndex++;
        $('#show_comment_area_div').append(
            $(' <div class="part_of_comment_div">\n' +
                '        <img class="commenter_img" id="commenter_img_' + index
                + '" src="/pb/imgs/comment_icon.png"/>\n' +
                '        <span class="commenter_span"  id="commenter_id_'
                + index + '"></span>\n' +
                '        <span class="comment_datetime_span" id="comment_datetime_span_'
                + index + '"></span>\n' +
                '        <div class="comment_area previous_comment_div">\n' +
                '            <span class="commenter_span" id="previous_commenter_id_span_'
                + index + '"></span><br>\n' +
                '            <p class="comment_area" id="previous_comment_content_p_'
                + index + '"></p>\n' +
                '        </div>\n' +
                '        <p class="comment_area" id="comment_content_p_' + index
                + '">\n' +
                '            </p>\n' +
                '        <img class="comment_reply_img" id="comment_reply_img_'
                + index + '" src="/pb/imgs/reply_icon.png"/>\n' +
                '    </div>'));
        //绑定相应事件和相关赋值操作
        let cIdAppointCommentData = fromCommentTableDataArr[fromCommentTableDataArr.length
        - cId];
        let endIndex = Math.ceil((6 / 17) * cIdAppointCommentData[1].length);
        if (endIndex > 6) {
          endIndex = 6;
        }
        $('#' + 'previous_commenter_id_span_' + index).html(
            cIdAppointCommentData[1].substring(0, endIndex));
        $('#' + 'previous_comment_content_p_' + index).html(
            cIdAppointCommentData[2]);

        analyzeDataAndCreateElementEventAndInsertDataNoPreviousCommentDivFunc(
            commentData, index);

      }
    });
  }

};

var analyzeDataAndCreateElementNoPreviousCommentDivFunc = function (commentData,
    index) {
  $('#show_comment_area_div').append($(' <div class="part_of_comment_div">\n' +
      '        <img class="commenter_img" id="commenter_img_' + index
      + '" src="/pb/imgs/comment_icon.png"/>\n' +
      '        <span class="commenter_span"  id="commenter_id_' + index
      + '"></span>\n' +
      '        <span class="comment_datetime_span" id="comment_datetime_span_'
      + index + '"></span>\n' +
      // '        <div class="comment_area previous_comment_div">\n' +
      // '            <span class="commenter_span"></span><br>\n' +
      // '            <p class="comment_area"></p>\n' +
      // '        </div>\n' +
      '        <p class="comment_area" id="comment_content_p_' + index + '">\n'
      +
      '            </p>\n' +
      '        <img class="comment_reply_img" id="comment_reply_img_' + index
      + '" src="/pb/imgs/reply_icon.png"/>\n' +
      '    </div>'));
  //绑定相应事件和相关赋值操作
  analyzeDataAndCreateElementEventAndInsertDataNoPreviousCommentDivFunc(
      commentData, index)
};

var analyzeDataAndCreateElementEventAndInsertDataNoPreviousCommentDivFunc = function (commentData,
    index) {
  //设置commenterId
  let endIndex = Math.ceil((6 / 17) * commentData[1].length);
  if (endIndex > 6) {
    endIndex = 6;
  }
  $('#' + 'commenter_id_' + index).html(commentData[1].substring(0, endIndex));
  //日期
  $('#' + 'comment_datetime_span_' + index).html(commentData[3]);
  //评论内容
  $('#' + 'comment_content_p_' + index).html(commentData[2]);
  //回复img点击事件
  let img = document.getElementById('comment_reply_img_' + index);
  img.tag = commentData[0];
  img.commenterId = $('#' + 'commenter_id_' + index).html();
  img.commenterContent = $('#' + 'comment_content_p_' + index).html();
  img.onclick = function () {
    responsedCommentId = this.tag;
    let scroll_offset = $("#show_comment_number_p").offset().top - 40;
    $("body").animate({
      scrollTop: scroll_offset
    }, 500, function () {
      $('#wanna_responsed_comment_info_div').css('display', 'block');
      $('#previous_commenter_id_p').html('回复：' + img.commenterId);
      $('#previous_comment_content_p').html(img.commenterContent);
    });
  };
};

if (window.location.href.toString().includes('professionalArticleHtmls')) {
  /**
   *取消某人的评论
   */
  $('#cancel_response_comment_img').click(() => {
    $('#wanna_responsed_comment_info_div').css('display', 'none');
    wannaResponseCommentId = undefined;
  });
  /**
   *  rId:commenTable的数据总数加一
   * cId:在回复icon上设置一个标签 img.tag = cId
   */
  publish_span.canClick = true;
  //评论发布
  publish_span.onclick = function () {
    if (this.canClick) {
      this.canClick = false;
    } else {
      return;
    }
    showOrHiddenLoadingDiv(true);
    let commentContent = $('#markdown_editor').val().trim();
    if (!commentContent) {
      alert("请您好好写评论！！！");
      return;
    }
    let email = $('#email_input').val().trim();
    let reg = new RegExp(
        "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    if (reg.test(email)) {
      let url;
      let data;
      // let tableName = window.location.href.toString().split(".")[0].split("/");
      // tableName = tableName[tableName.length - 1];
      let tableName = window.location.href.toString().split("/")
      tableName = tableName[tableName.length - 1];
      tableName = tableName.split(".")[0];
      if ($('#wanna_responsed_comment_info_div').css('display') === 'none') {
        url = '/pb/article/insertCommentContentToAppointTable';
        data = {
          tableName: tableName,
          commenterId: email,
          commentContent: commentContent
        }
      } else {
        url = '/pb/article/insertResponseAndCommentContentToAppointTable';
        data = {
          tableName: tableName,
          rId: parseInt(fromCommentTableDataArr[0][0]) + 1 + '',
          cId: responsedCommentId,
          commenterId: email,
          commentContent: commentContent
        }
      }

      $.ajax({
        method: 'POST',
        url: url,
        data: data,
        dataType: 'text',
        success: function (data) {
          publish_span.canClick = true;
          showOrHiddenLoadingDiv(false);
          if (data !== FAILURE) {
            // if ($('.part_of_comment_div')) {
            $('#wanna_responsed_comment_info_div').css('display', 'none');
            $('#markdown_editor').val('');
            $('#email_input').val('');
            //$('.part_of_comment_div').remove();
            //}
            //loadCommentFunc();
            //显示的就是已经加载的数据
            alert('评论提交成功待审核')
          } else {
            alert("评论上传失败！！！")
          }
        }
      }).fail(function () {
        publish_span.canClick = true;
        showOrHiddenLoadingDiv(false);
        alert("评论上传失败！！！，网络未连接？？？")
      });
    } else {
      publish_span.canClick = true;
      alert("填入有效邮箱！！！");
    }
  };

  //加载评论数据
  loadCommentFunc();

}