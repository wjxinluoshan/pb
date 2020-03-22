package com.ajobs.controllers

import com.ajobs.impdaos.OwnerJdbcTemplate
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest


@Controller
@RequestMapping("/ownerInfo")
class OwnerController {
    private val mSuccess = "0"
    private val mFailure = "1"

    private var mApplicationContext: ApplicationContext? = null
    private var mOwnerJdbcTemplate: OwnerJdbcTemplate? = null

    /**
     * 当前用户的登陆状态
     */
    private var mRequestIp = ""
    private var mOwnerStatus = "losed"


    private val LOGINED = "logined"
    private val LOSED_CONN = "losed"

    private val IP_HEADER_CANDIDATES = arrayOf("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR")

    /**
     * 得到请求方的ip
     */
    private fun getClientIpAddressIfServletRequestExist(): String {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return "0.0.0.0"
        }
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        for (header in IP_HEADER_CANDIDATES) {
            val ipList = request.getHeader(header)
            if (ipList != null && ipList.isNotEmpty() && !"unknown".equals(ipList, ignoreCase = true)) {
                return ipList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }
        }
        return request.remoteAddr
    }

    /**
     * 登录
     */
    @RequestMapping(value = ["login"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun login(userName: String, password: String, request: HttpServletRequest): String {
        getApplicationContext("jdbc.xml")
        if (mOwnerJdbcTemplate?.verifyOwner(userName, password) == mSuccess) {
            //用户已登陆
            mOwnerStatus = LOGINED
            mRequestIp = getClientIpAddressIfServletRequestExist()
            return mSuccess
        }
        return mFailure
    }

    /**
     * 用户掉线
     */
    @RequestMapping(value = ["logout"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun logout() {
        mOwnerStatus = LOSED_CONN
        mRequestIp = ""
    }

    /**
     * 得到用户的登陆状态
     */
    @RequestMapping(value = ["getOwnerOnlineStatus"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getOwnerOnlineStatus(): String {
        return if (mOwnerStatus == LOGINED && mRequestIp == getClientIpAddressIfServletRequestExist()) {
            mSuccess
        } else {
            mFailure
        }
    }

    /**
     * 获得网站浏览次数:
     */
    @RequestMapping(value = ["getSiteBrowseNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun getSiteBrowseNumber(): String {
        getApplicationContext("jdbc.xml")
        try {
            return mOwnerJdbcTemplate?.getSiteBrowseNumber()?.toString() ?: "0"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mFailure
    }

    /**
     * 获得网站浏览次数
     */
    @RequestMapping(value = ["uploadSiteBrowseNumber"], method = [RequestMethod.POST], produces = ["text/html; charset=UTF-8"])
    @ResponseBody
    fun uploadSiteBrowseNumber(): String {
        getApplicationContext("jdbc.xml")
        try {
            /**
             * 并发处理
             */
            return synchronized(this) { mOwnerJdbcTemplate?.uploadSiteBrowseNumber() ?: mFailure }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mFailure
    }

    /**
     * 获取应用的上下文对象
     */
    private fun getApplicationContext(xml: String) {
        if (mApplicationContext == null) {
            mApplicationContext = ClassPathXmlApplicationContext(xml)
            mOwnerJdbcTemplate = mApplicationContext?.getBean("ownerJdbcTemplate") as OwnerJdbcTemplate
        }
    }
}