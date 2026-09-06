package com.godseye.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.godseye.mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        immersive()
        b.web.settings.javaScriptEnabled = true
        b.web.settings.domStorageEnabled = true
        b.web.settings.mediaPlaybackRequiresUserGesture = false
        b.web.settings.useWideViewPort = true
        b.web.settings.setSupportZoom(true)
        b.web.webViewClient = WebViewClient()
        b.web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) { runOnUiThread { request.grant(request.resources) } }
        }
        b.btnSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        b.btnHud.setOnClickListener { key("h") }
        b.btnReset.setOnClickListener { b.web.evaluateJavascript("document.getElementById('reset-globe-view')?.click()", null) }
        b.btnZoomIn.setOnClickListener { wheel(-500) }
        b.btnZoomOut.setOnClickListener { wheel(500) }
        load()
    }
    override fun onResume() { super.onResume(); immersive() }
    private fun load() { val p=getSharedPreferences("gev",MODE_PRIVATE); b.web.loadUrl(p.getString("server","http://127.0.0.1:4173")!!.trimEnd('/')) }
    private fun key(k:String){ b.web.evaluateJavascript("document.dispatchEvent(new KeyboardEvent('keydown',{key:'$k',bubbles:true}))",null) }
    private fun wheel(d:Int){ b.web.evaluateJavascript("document.getElementById('cesiumContainer')?.dispatchEvent(new WheelEvent('wheel',{deltaY:$d,bubbles:true,cancelable:true}))",null) }
    private fun immersive(){ window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE }
}
