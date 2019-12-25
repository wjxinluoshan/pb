package com.ajobs.controllers

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.Exception

/**
 * 全局错误信息拦截
 */
@ControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun error(): String = "access failure！！！"
}