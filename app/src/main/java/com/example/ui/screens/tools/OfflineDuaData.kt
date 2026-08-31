package com.example.ui.screens.tools

import android.content.Context
import org.json.JSONArray

data class OfflineDuaBlock(val type: String, val text: String)
data class OfflineDuaItem(val id: Int, val title: String, val asset: String, val blocks: List<OfflineDuaBlock>)

fun cleanDuaTitle(rawTitle: String): String {
    // Strip leading serial numbers (e.g. "010 ", "011 ", "12. ", "১। ")
    return rawTitle
        .replace(Regex("^[0-9০-৯]{1,4}[\\.\\-:\\)\\s|/]?\\s*"), "")
        .trim()
}

fun loadOfflineDuas(context: Context): List<OfflineDuaItem> {
    val json = context.assets.open("dua_offline/dua_data.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
    val array = JSONArray(json)
    return buildList(array.length()) {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val blocksJson = obj.getJSONArray("blocks")
            val blocks = buildList(blocksJson.length()) {
                for (j in 0 until blocksJson.length()) {
                    val block = blocksJson.getJSONObject(j)
                    add(OfflineDuaBlock(block.getString("type"), block.getString("text")))
                }
            }
            val rawTitle = obj.getString("title")
            val cleanedTitle = cleanDuaTitle(rawTitle).ifEmpty { rawTitle }
            add(OfflineDuaItem(obj.getInt("id"), cleanedTitle, obj.getString("asset"), blocks))
        }
    }
}

