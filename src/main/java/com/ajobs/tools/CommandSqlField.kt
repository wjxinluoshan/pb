package com.ajobs.tools

import java.lang.StringBuilder

class CommandSqlField {
    fun returnNonNullField(map: Map<String, Any?>): Map<String, Any> {
        var rMap = mutableMapOf<String, Any>()
        for ((key, value) in map) {
            if (value != null) {
                rMap[key] = value
            }
        }
        return rMap
    }

    fun returnInsertAvailableField(tableName: String, map: Map<String, Any>): String {
        var sb = StringBuilder()
        for (key in map.keys) {
            sb.append(key).append(",")
        }
        var keyString = sb.toString()
        var keyStr = keyString.substring(0, keyString.length - 1)
        sb.clear()
        for (value in map.values) {
            if(value is String)
            sb.append("'").append(value).append("',")
        }
        var valueString = sb.toString()
        var valueStr = valueString.substring(0, valueString.length - 1)
        return "insert into $tableName ($keyStr) values($valueStr)"
    }

    fun returnUpdateAvailableField(tableName: String, map: Map<String, Any>): String {
        var sb = StringBuilder()
        for ((key,value) in map) {
            if(value is String)
            sb.append(key).append("='").append(value).append("',")
        }
        var string = sb.toString()
        var str = string.substring(0, string.length - 1)
        return "update $tableName set $str"
    }

}