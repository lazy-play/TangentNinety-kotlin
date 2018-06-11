package com.pudding.tangentninety.utils

import java.util.Locale

/**
 * Created by codeest on 16/8/14.
 *
 *
 * 在html中引入外部css,js文件   常规拼接顺序css->html->js
 * https://github.com/HotBitmapGG/RxZhiHu/blob/https--github.com/HotBitmapGG/RxZhiHuDaily/app/src/main/java/com/hotbitmapgg/rxzhihu/utils/HtmlUtil.java#L13
 */

object HtmlUtil {
    private const val HTML_HEAD = "<!doctype html><html>"
    private const val HTML_END = "</html>"
    private const val HEAD_START = "<head>"
    private const val HEAD_END = "</head>"
    private const val BODY_START = "<body className=\"\" onload=\"onLoaded(%s)\">"
    private const val BODY_END = "</body>"
    private const val META = "<meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,user-scalable=no\">"
    private const val NEEDED_FORMAT_CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/news_qa.min.css\"/>"
    // js script tag,需要格式化
    private const val NEEDED_FORMAT_JS_TAG = "<script src=\"%s\"></script>"
    private val BASE_JS_LIST = arrayOf("file:///android_asset/video.js", "file:///android_asset/img_replace.js")
    private const val JS_NIGHT = "file:///android_asset/night.js"
    private const val JS_LARGE_FONT = "file:///android_asset/large-font.js"

    const val MIME_TYPE = "text/html; charset=utf-8"

    const val ENCODING = "utf-8"

    private fun createBody(body: String, isNoPic: Boolean, isNight: Boolean): String {
        return if (isNoPic) {
            val replace = if (isNight)
                "img src=\"file:///android_asset/default_pic_content_image_download_dark.webp\" zhimg-src"
            else
                "img src=\"file:///android_asset/default_pic_content_image_download_light.webp\" zhimg-src"
            //            return body.replaceAll("img\\D*src",replace);
            val bodys = body.split("class=\"avatar\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sb = StringBuilder()
            for (s in bodys) {
                sb.append(s.replace("img\\D*src".toRegex(), replace))
            }
            sb.toString()
        } else {
            body
        }
    }

    /**
     * 根据js链接生成Script标签
     *
     * @param url String
     * @return String
     */
    private fun createJsTag(url: String): String {
        return String.format(NEEDED_FORMAT_JS_TAG, url)
    }

    private fun createJsTag(isNight: Boolean, isLargeFont: Boolean): String {

        val sb = StringBuilder()
        for (url in BASE_JS_LIST) {
            sb.append(createJsTag(url))
        }
        if (isNight) {
            sb.append(createJsTag(JS_NIGHT))
        }
        if (isLargeFont) {
            sb.append(createJsTag(JS_LARGE_FONT))
        }
        return sb.toString()
    }

    private fun createOnLoadMode(isBackground: Boolean): String {
        return String.format(Locale.US, BODY_START,isBackground)
    }

    /**
     * 根据样式标签,html字符串,js标签
     * 生成完整的HTML文档
     */
    fun createHtmlData(html: String): String {
        return createHtmlData(html, true, false, false, false)
    }

    fun createHtmlData(html: String, isNoPic: Boolean, isNight: Boolean, isLargeFont: Boolean): String {
        return createHtmlData(html, false, isNoPic, isNight, isLargeFont)
    }

    private fun createHtmlData(html: String,  isBackground: Boolean, isNoPic: Boolean, isNight: Boolean, isLargeFont: Boolean): String {
        return HTML_HEAD +
                HEAD_START +
                META +
                NEEDED_FORMAT_CSS_TAG +
                HEAD_END +
                createOnLoadMode(isBackground) +
                createJsTag(isNight, isLargeFont) +
                createBody(html, isNoPic, isNight) +
                BODY_END +
                HTML_END
    }
}
