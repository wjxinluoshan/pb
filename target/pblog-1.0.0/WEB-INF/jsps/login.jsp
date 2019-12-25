<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Login</title>
    <%-- ??vue??--%>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <%--    jquery的在线API--%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <style>
        .login_div {
            width: 300px;
            height: 400px;
            border: 1px solid #00A2E8;
            text-align: center;
        }

        .info_input {
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="login_div">
    大学名：<input class="info_input" type="text" maxlength="32"/>
    </br>
    学号：<input class="info_input" type="text" maxlength="32"/>
    </br>
    密码：<input class="info_input" type="password" maxlength="32"/>
    </br>
    <p id="login_p" @click="loginCmd()">登录</p>
    </br>
    <p id="signUp_p" @click="signUpCmd()">注册</p>
</div>
</body>
<script>

    var loginResponseData = ['successed', 'failure'];
    var inputValueArr = [];
    var IP = "172.21.171.203";

    /*
     * 登录操作
     */

    var login_p = new Vue({
        el: "#login_p",
        methods: {
            loginCmd: function () {
                var inputArr = $('.info_input');
                inputValueArr.length = 0;
                for (var k = 0; k < inputArr.length; k++)
                    if (!inputArr[k].value) {
                        window.alert("请完善您要输入的信息!!!");
                        return;
                    } else {
                        inputValueArr.push(inputArr[k].value)
                    }
                /*
                 *开始进行数据的检验
                 */
                //使用jQuery框架
                $.ajax({
                    method: 'POST',
                    url: 'http://' + IP + ':8001/userInfo/loginUser',
                    data: {
                        compusName: inputValueArr[0],
                        stuNumber: inputValueArr[1],
                        password: inputValueArr[2]
                    },
                    dataType: 'text',
                    success: function (data) {
                        switch (data) {
                            case loginResponseData[1]:
                                alert("登陆失败!!!");
                                break;
                            default:
                                //登陆成功,跳转到主页面：重定向操作
                                window.location.href = 'http://' + IP + ':8001/content/mainshow';
                                break;
                        }
                    }
                }).fail(function () {
                    alert("网络请求失败！！！");
                });
            }
        }
    })

    /*
     *注册操作
     */
    var signUp_p = new Vue({
        el: "#signUp_p",
        methods: {
            signUpCmd: function () {
                $.ajax({
                    method: 'POST',
                    async: false,
                    url: 'http://' + IP + ':8001/userInfo/redirectSignUp',
                    dataType: 'text',
                    success: function (data) {
                        window.location.href = data;
                    }
                }).fail(function () {
                    alert("网络请求失败！！！");
                });
            }
        }
    })


</script>
</html>