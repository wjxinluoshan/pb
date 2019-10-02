package com.ajobs.controllers;

import com.ajobs.impdaos.UserInfoJdbcTemplate;
import com.ajobs.tabledatas.UserInfo;
import com.ajobs.tools.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userInfo")
public class UserController {
    private final String mSuccess = "0";
    private final String mFailure = "1";

    private ApplicationContext mApplicationContext = null;
    private UserInfoJdbcTemplate mUserInfoJdbcTemplate = null;


    /**
     * 用户信息修改
     */
    @RequestMapping(value = "uploadUserInfo", method = RequestMethod.POST , produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String uploadUserInfo(String profile, String introduce, String habit) {
        /*
         * 将前端传来的图片 base64编码进行解码
         */
//        FileOutputStream(File("D:/compilers/a.jpg")).use {
//            var byteArr = Base64.getMimeDecoder().decode(profile.toByteArray())
//            it.write(byteArr)
//        }
        try {
            getApplicationContext("jdbc.xml");
            mUserInfoJdbcTemplate = (UserInfoJdbcTemplate) mApplicationContext.getBean("userInfoJdbcTemplate");
            if (mUserInfoJdbcTemplate.uploadUserInfo(profile, introduce, habit) == Constants.CmdSuccess)
                return mSuccess;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFailure;
    }

    /**
     * 用户数据的提取
     */
    @RequestMapping(value = "userInfoRequire", method = RequestMethod.POST, produces = "text/html; charset=UTF-8")
    @ResponseBody
    public String userInfoRequire() {
        try {
            getApplicationContext("jdbc.xml");
            UserInfo userInfo = mUserInfoJdbcTemplate.getUserInfo();
            if (userInfo != null) {
                return userInfo.getProfile() + " pblog " + userInfo.getIntroduce() + " pblog " + userInfo.getHabit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mFailure;
    }

    /**
     * 获取应用的上下文对象
     */
    private void getApplicationContext(String xml) {
        if (mApplicationContext == null) {
            mApplicationContext = new ClassPathXmlApplicationContext(xml);
            mUserInfoJdbcTemplate = (UserInfoJdbcTemplate) mApplicationContext.getBean("userInfoJdbcTemplate");
        }
    }
}
