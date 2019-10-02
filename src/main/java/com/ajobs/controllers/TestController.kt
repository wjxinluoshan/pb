package com.ajobs.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import java.io.FileOutputStream

@Controller
@RequestMapping("/testp")
class TestController {

    private var mNumber = 0
    @RequestMapping(value = ["test"], method = [RequestMethod.GET])
    @ResponseBody
    fun test() {
        FileOutputStream(File("WEB-INF/professionalArticleHtmls", "wjx.txt")).use {
            it.write(10)
        }
    }
}