package com.godseye.mobile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.godseye.mobile.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b=ActivitySettingsBinding.inflate(layoutInflater); setContentView(b.root)
        val p=getSharedPreferences("gev",MODE_PRIVATE)
        b.serverUrl.setText(p.getString("server","http://127.0.0.1:4173")); b.cesiumKey.setText(p.getString("cesium","")); b.googleKey.setText(p.getString("google","")); b.openAiKey.setText(p.getString("openai",""))
        b.save.setOnClickListener {
            p.edit().putString("server",b.serverUrl.text.toString().trim()).putString("cesium",b.cesiumKey.text.toString()).putString("google",b.googleKey.text.toString()).putString("openai",b.openAiKey.text.toString()).apply()
            Toast.makeText(this,"Settings saved on this device",Toast.LENGTH_SHORT).show(); finish()
        }
    }
}
