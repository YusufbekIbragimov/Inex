package uz.yusufbek_ibragimov.inex

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import uz.yusufbek_ibragimov.inex.core.utils.BASE_URL_WEB_SITE
import uz.yusufbek_ibragimov.inex.core.utils.isNetworkAvailable
import uz.yusufbek_ibragimov.inex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    val MyTag="MainActivity"

    private var countDownTimer: CountDownTimer? = null
    private var isBacked = 1
    private var isConnected: Boolean = true

    override fun onStart() {
        super.onStart()
        window.statusBarColor= Color.WHITE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isConnected = isNetworkAvailable
        if (isConnected){
            setViewData()
        }else{
            binding.noInternetLayout.visibility=View.VISIBLE
        }

        binding.swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                setViewData2()
            }

        })

    }
    private fun setViewData2() {
        binding.webViewId.loadUrl(BASE_URL_WEB_SITE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setViewData() {

        /*val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)*/

        binding.webViewId.settings.apply {
            javaScriptEnabled=true
            domStorageEnabled=true
            javaScriptCanOpenWindowsAutomatically=true
            databaseEnabled=true
        }
        binding.webViewId.webChromeClient=object : WebChromeClient(){
            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                Log.d(MyTag, "onConsoleMessage: $message")
                super.onConsoleMessage(message, lineNumber, sourceID)
            }
        }

        binding.webViewId.apply {
//            cookieManager.setAcceptThirdPartyCookies(this, true)
            webViewClient = MyWebViewClient()
        }

        countDownTimer = object : CountDownTimer(2_500, 2_500) {

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                binding.progressBarId.visibility = View.VISIBLE
                binding.imageLogoId.visibility = View.GONE
                binding.inexTv.visibility = View.GONE
                binding.webViewId.visibility = View.VISIBLE
                binding.swipeRefreshLayout.visibility = View.VISIBLE
            }

        }
        countDownTimer?.start()
        binding.webViewId.loadUrl(BASE_URL_WEB_SITE)

    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onReceivedHttpAuthRequest(
            view: WebView?,
            handler: HttpAuthHandler?,
            host: String?,
            realm: String?
        ) {
            handler?.proceed("test2","123")
            super.onReceivedHttpAuthRequest(view, handler, host, realm)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            binding.noInternetLayout.visibility=View.GONE
            binding.progressBarId.visibility = View.VISIBLE
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            binding.progressBarId.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing=false
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String?,
            failingUrl: String
        ) {
            binding.noInternetLayout.visibility=View.VISIBLE
            binding.progressBarId.visibility = View.GONE
            binding.tv1.text="Error Code: $errorCode"
            binding.tv2.text="Connection error description : $description"
            view.loadUrl(BASE_URL_WEB_SITE)
        }

    }

    override fun onResume() {
        super.onResume()

        binding.webViewId.onResume()

    }

    override fun onPause() {
        super.onPause()
        binding.webViewId.onPause()
    }

    override fun onBackPressed() {
        if (!binding.webViewId.canGoBack()) {
            if (isBacked==2){
                super.onBackPressed()
            }else{
                Toast.makeText(this, "Chiqish uchun yana bir martta bosing", Toast.LENGTH_SHORT).show()
                isBacked = 2
            }
        }else{
            binding.webViewId.goBack()
        }
    }

    fun onClickRetry(view: View) {

        if (isNetworkAvailable){
            binding.noInternetLayout.visibility=View.GONE
            setViewData()
        }else{
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
        }

    }

}