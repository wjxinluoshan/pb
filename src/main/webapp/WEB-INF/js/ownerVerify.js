/**
 *检验用户是否登录
 * 同步请求
 *   async: false
 */
var loginStatus = $.ajax({
    method: 'POST',
    url: 'http://' + IP + ':' + PORT_PATH + '/ownerInfo/getOwnerOnlineStatus',
    async: false,
    dataType: 'text',
    success: function (data) {
        if (data === FAILURE) {
            if (confirm("信息验证失败，是否要重新请求？")) {
                window.location.reload();
            } else {
                window.location.href = 'http://' + IP + ':' + PORT_PATH + '/owner/login.html';
            }
        }
    }
}).fail(function () {
    if (confirm("网络请求失败，是否要重新请求？")) {
        loginStatus();
    }
});