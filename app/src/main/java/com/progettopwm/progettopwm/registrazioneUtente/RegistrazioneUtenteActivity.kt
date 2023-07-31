package com.progettopwm.progettopwm.registrazioneUtente

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.databinding.ActivityRegistrazioneBinding
import com.progettopwm.progettopwm.profiloUtente.ModificaAvatarCustomDialog

class RegistrazioneUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegistrazioneBinding
    lateinit var fileAvatar : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

}