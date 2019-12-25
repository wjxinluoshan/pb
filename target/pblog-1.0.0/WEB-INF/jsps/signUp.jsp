<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sign Up</title>
    <%--    VUE的在线API--%>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <%--    jquery的在线API--%>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <style>
        .signUp_div {
            width: 300px;
            height: 400px;
            border: 1px solid #FFAEC9;
            text-align: center;
        }

        #profile {
            margin-top: 10px;
            width: 80px;
            height: 80px;
            border-radius: 80px;
            border: 1px dotted #000;
        }

        .info_input {
            margin-top: 10px;
        }

        #commit_p {
            color: #00A2E8;
        }
    </style>
</head>

<body>
<div class="signUp_div">
    <img id="profile" v-on:click="setImgSrc()"/>
    </br>
    别名：<input class="info_input" type="text" maxlength="16"/>
    </br>
    大学名：<input class="info_input" type="text" maxlength="32"/>
    </br>
    学号：<input class="info_input" type="text" maxlength="32"/>
    </br>
    姓名：<input class="info_input" type="text" maxlength="32"/>
    </br>
    密码：<input class="info_input" type="password" maxlength="32"/>
    </br>
    <p id="commit_p" v-on:click="commitInfoCmd()">提交</p>
</div>
</body>
<script>
    var IP = "172.21.171.203";
    var signUpResponseData = ['userExisting', 'successed', 'failure'];
    //图像的二进制的数据
    var profileBinaryData = ""
    //存放input中的数据
    var inputValueArr = [];

    //图像处理
    var profile = new Vue({
        el: '#profile',
        methods: {
            //打开文本选择器，进行图片的选择
            setImgSrc: function () {
                if (document.getElementById("_ef")) {
                    document.getElementById("_ef").remove();
                }
                var inputObj = document.createElement('input');
                inputObj.setAttribute('id', '_ef');
                inputObj.setAttribute('type', 'file');
                //设置input所选取文件的为图片
                inputObj.setAttribute("accept", "image/png,image/jpeg");
                inputObj.setAttribute("style", 'visibility:hidden');
                document.body.appendChild(inputObj);
                inputObj.click();
                //进行图片的选择
                inputObj.onchange = function (event) {
                    var resultFile = inputObj.files[0];
                    if (resultFile) {

                        //将图像转换成可以预览的
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            var urlData = profileBinaryData = this.result;
                            //预览图片
                            document.getElementById('profile').setAttribute("src", urlData);
                            //上传photo：去除将photo Base64编码后的头
                            // if (profileBinaryData.includes('image/jpeg'))
                            //     profileBinaryData = profileBinaryData.replace('data:image/jpeg;base64,', '');
                            // else if (profileBinaryData.includes('image/png'))
                            //     profileBinaryData = profileBinaryData.replace('data:image/png;base64,', '');

                        };
                        reader.readAsDataURL(resultFile);
                    }
                    document.getElementById("_ef").remove();
                }
            }
        }
    })

    //提交按钮处理
    var commit_p = new Vue({
        el: "#commit_p",
        methods: {
            /*
            *提交按钮的点击事件
            */
            commitInfoCmd: function () {
                //1.首先检查是否上面的input是否为空，并且将数据存放在数组中
                inputValueArr.length = 0;
                var inputArr = document.getElementsByClassName("info_input");
                for (var k = 0; k < inputArr.length; k++)
                    if (!inputArr[k].value) {
                        window.alert("请完善您要输入的信息!!!");
                        return;
                    } else {
                        inputValueArr.push(inputArr[k].value)
                    }

                //2.检查密码是否合法

                //3.检查profile的src是否为空
                if (!profileBinaryData) {
                    window.alert("请选择您的图像!!!");
                    return;
                }

                //向姑服务器传递数据
                this.sendMsgToServer();
            },
            /*
             *向服务器发送数据进行用户的登录
             *    ajax传递的数据是 plainObject；{}  或 String
             */
            sendMsgToServer: function () {
                var passData = {
                    profile: profileBinaryData,
                    aliaName: inputValueArr[0],
                    compusName: inputValueArr[1],
                    stuNumber: inputValueArr[2],
                    stuName: inputValueArr[3],
                    password: inputValueArr[4]
                }

                //使用jQuery框架
                $.ajax({
                    method: 'POST',
                    url: 'http://' + IP + ':8001/userInfo/registryUser',
                    data: passData,
                    dataType: 'text',
                    success: function (data) {
                        switch (data) {
                            case signUpResponseData[0]:
                                alert("用户已存在!!!");
                                break;
                            case signUpResponseData[1]:
                                alert("恭喜您，注册成功!!!");
                                break;
                            case signUpResponseData[2]:
                                alert("很抱歉，注册失败!!!");
                                break;

                        }
                    }
                }).fail(function () {
                    alert("网络请求失败!!!");
                });

                // $.ajax({
                //     method: 'POST',
                //     url: 'http://' + IP + ':8001/userInfo/test',
                //     data:{name:profileBinaryData}
                // }).fail(function () {
                //     alert("error");
                // });

            }
        }
    })

</script>

</html>