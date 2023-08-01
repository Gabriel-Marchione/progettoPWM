package com.progettopwm.progettopwm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.databinding.ActivityBenvenutoBinding
import com.progettopwm.progettopwm.autenticazioneUtente.LoginActivity
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity
import com.progettopwm.progettopwm.registrazioneUtente.RegistrazioneUtenteActivity


class BenvenutoActivity : AppCompatActivity() {
    lateinit var binding : ActivityBenvenutoBinding
    lateinit var filePre : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBenvenutoBinding.inflate(layoutInflater)
        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        if (!filePre.getString("Email", "").equals("")) {
            //da cambiare con la futura homepage
            val intent = Intent(this@BenvenutoActivity, ProfiloUtenteActivity::class.java)
            startActivity(intent)
        } else {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)

            binding.registratiButton.setOnClickListener {
                val intent = Intent(this, RegistrazioneUtenteActivity::class.java)
                startActivity(intent)
            }

            binding.accediButton.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}