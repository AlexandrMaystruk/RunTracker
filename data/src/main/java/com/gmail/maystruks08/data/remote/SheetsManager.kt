//package com.gmail.maystruks08.data.remote
//
//import android.R.attr.country
//import org.json.JSONObject
//import timber.log.Timber
//import java.io.*
//import java.net.HttpURLConnection
//import java.net.URL
//import java.net.URLEncoder
//import javax.net.ssl.HttpsURLConnection
//
//
//class SheetsManager {
//
//    fun getSheets(){
//         try {
//
//             ?action=addItem&itemName=Test Name&brand=Zalupa
//
//             https://script.google.com/macros/s/AKfycbzThVMj_w7NGCl8-5kHQudv-0U3CGgNYvm2QW6fDaYdn4FmGg/exec
//
//             //https://docs.google.com/spreadsheets/d/e/2PACX-1vSPU11LmU5rzjcxuzCCvOB-lhYP3hrTUOG8a6AR-YP1kCBpSF2NlThGlezcUegiahSJqzW1Hb6ajNwZ/pubhtml
//            https://docs.google.com/spreadsheets/d/e/2PACX-1vT_c14nosTs7WpSuP0jwv5KLyiYmu6K_eUc0QE1acU33PZD9NgLatPGducNGz-GFugV3K9Xd97LAnCY/pubhtml
//
//             //Change your web app deployed URL or u can use this for attributes (name, country)
//            val url = URL("https://script.google.com/macros/d/e/2PACX-1vSPU11LmU5rzjcxuzCCvOB-lhYP3hrTUOG8a6AR-YP1kCBpSF2NlThGlezcUegiahSJqzW1Hb6ajNwZ/exec?id=1Ed_SM6AXwIDaqF-PiA0kYkA8_QpRi69B4rJFUSC-4dg&sheet=100for24_Result")
//            val postDataParams = JSONObject()
//
//            //int i;
//            //for(i=1;i<=70;i++)
//
//            //    String usn = Integer.toString(i);
//            val id = "YOUR SPREAD SHEET ID"
//            postDataParams.put("name", name)
//            postDataParams.put("country", country)
//            postDataParams.put("id", id)
//            Timber.e("params%s", postDataParams.toString())
//            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
//             conn.readTimeout = 15000
//             conn.connectTimeout = 15000
//             conn.requestMethod = "POST"
//             conn.doInput = true
//             conn.doOutput = true
//            val os: OutputStream = conn.outputStream
//            val writer = BufferedWriter(
//                OutputStreamWriter(os, "UTF-8")
//            )
//            writer.write(getPostDataString(postDataParams))
//            writer.flush()
//            writer.close()
//            os.close()
//            val responseCode: Int = conn.responseCode
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                val `in` = BufferedReader(InputStreamReader(conn.inputStream))
//                val sb = StringBuffer("")
//                var line: String? = ""
//                while (`in`.readLine().also { line = it } != null) {
//                    sb.append(line)
//                    break
//                }
//                `in`.close()
//                sb.toString()
//            } else {
//                String("false : $responseCode")
//            }
//        } catch (e: Exception) {
//            String("Exception: " + e.message)
//        }
//    }
//
//    @Throws(java.lang.Exception::class)
//    fun getPostDataString(params: JSONObject): String? {
//        val result = StringBuilder()
//        var first = true
//        val itr = params.keys()
//        while (itr.hasNext()) {
//            val key = itr.next()
//            val value = params[key]
//            if (first) first = false else result.append("&")
//            result.append(URLEncoder.encode(key, "UTF-8"))
//            result.append("=")
//            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
//        }
//        return result.toString()
//    }
//}