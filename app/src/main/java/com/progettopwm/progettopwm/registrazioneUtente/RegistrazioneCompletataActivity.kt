package com.progettopwm.progettopwm.registrazioneUtente

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.databinding.ActivityRegistrazioneCompletataBinding
import com.progettopwm.progettopwm.homepage.HomepageActivity
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity

class RegistrazioneCompletataActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegistrazioneCompletataBinding
    lateinit var filePre : SharedPreferences
    lateinit var fileAvatarRegistrazione : SharedPreferences
    lateinit var fileAvatar : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneCompletataBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)
        fileAvatarRegistrazione = this.getSharedPreferences("File avatar registrazione", MODE_PRIVATE)
        fileAvatar = this.getSharedPreferences("File avatar", MODE_PRIVATE)

        val editor = filePre.edit()
        editor.putString("Email", intent.extras?.getString("Email"))
        editor.putString("Password", intent.extras?.getString("Password"))
        editor.apply()

        val editorAvatar = fileAvatarRegistrazione.edit()
        editorAvatar.clear()
        editorAvatar.apply()

        val editorProfiloUtente = fileAvatar.edit()
        intent.extras?.getInt("idImmagineAvatarRegistrazione")
            ?.let { editorProfiloUtente.putInt("idImmagineAvatar", it) }
        editorProfiloUtente.apply()

        binding.completaRegistrazioneButton.setOnClickListener{
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }
}