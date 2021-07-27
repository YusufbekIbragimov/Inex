package uz.yusufbek_ibragimov.inex.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import uz.yusufbek_ibragimov.inex.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/*
@SuppressLint("HardwareIds")
fun getConfig(activity: Activity): Map<String, String> {
    val configFile = HashMap<String, String>()

    configFile["bid"] = Settings.Secure.getString(
        activity.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    configFile["country"] = getCountry(activity)
    configFile["device"] = Build.BRAND.toUpperCase()

    configFile["af_id"] = AppsFlyerLib.getInstance().getAppsFlyerUID(activity)

    return configFile
}
*/


fun getCountry(activity: Activity): String {
    val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
    var service = ""
    if (telephonyManager != null) {
        service = telephonyManager.simCountryIso.toUpperCase()

        if (service.isEmpty()) {
            service = telephonyManager.networkCountryIso.toUpperCase()
        }
    }
    if (service.isEmpty()) {
        service = Locale.getDefault().isO3Country.toUpperCase()
    }
    if (service.isEmpty()) {
        service = Locale.getDefault().country.toUpperCase()
    }
    if (service.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        if (activity.resources.configuration.locales.size() > 0) {
            service = activity.resources.configuration.locales[0].isO3Country
        }
    }
    return service
}

/*fun getJSONFromURL(url: String): String {

    var `is`: InputStream? = null
    var result = ""
    var jArray: JSONArray? = null

    // Download JSON data from URL

    // Download JSON data from URL
    try {
        val httpclient: HttpClient = DefaultHttpClient()
        val httppost = HttpPost(url)
        val response: HttpResponse = httpclient.execute(httppost)
        val entity: HttpEntity = response.entity
        `is` = entity.content
    } catch (e: Exception) {
        Log.e("log_tag", "Error in http connection $e")
    }

    // Convert response to string

    // Convert response to string
    try {
        val reader = BufferedReader(InputStreamReader(`is`, "UTF-8"), 8)
        val sb = StringBuilder()
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {
            sb.append(
                """
                $line
                
                """.trimIndent()
            )
        }
        `is`?.close()
        result = sb.toString()
    } catch (e: Exception) {
        Log.e("log_tag", "Error converting result $e")
    }

    try {
        jArray = JSONArray(result)
    } catch (e: JSONException) {
        Log.e("log_tag", "Error parsing data $e")
    }

    return result

}*/


fun Activity.setItemStatusBarColor(@ColorInt color: Int, darkStatusBarTint: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

    val window: Window = (window).also {
        it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        it.statusBarColor = color
    }

    val decor = window.decorView
    if (darkStatusBarTint) {
        decor.systemUiVisibility = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        // We want to change tint color to white again.
        // You can also record the flags in advance so that you can turn UI back completely if
        // you have set other flags before, such as translucent or full screen.
        decor.systemUiVisibility = 0
    }
}

val Activity.isNetworkAvailable: Boolean
    get() {
        val context = applicationContext
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        for (i in info.indices) {
            if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

fun Activity.changeMode(flag: Boolean) {
    if (flag) {
        setItemStatusBarColor(ContextCompat.getColor(this, R.color.black), false)

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    } else {

        setItemStatusBarColor(ContextCompat.getColor(this, R.color.black), false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}