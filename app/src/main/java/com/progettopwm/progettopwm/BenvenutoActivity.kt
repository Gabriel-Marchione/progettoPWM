package com.progettopwm.progettopwm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.databinding.ActivityBenvenutoBinding
import com.progettopwm.progettopwm.registrazioneUtente.RegistrazioneUtenteActivity


class BenvenutoActivity : AppCompatActivity() {
    lateinit var binding : ActivityBenvenutoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBenvenutoBinding.inflate(layoutInflater)
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