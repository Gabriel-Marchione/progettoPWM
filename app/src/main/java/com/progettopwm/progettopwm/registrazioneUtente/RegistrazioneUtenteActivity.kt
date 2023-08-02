package com.progettopwm.progettopwm.registrazioneUtente

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ActivityRegistrazioneBinding
import com.example.progettopwm.databinding.FragmentRegistrazionePrimaParteBinding
import com.progettopwm.progettopwm.profiloUtente.ModificaAvatarCustomDialog

class RegistrazioneUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegistrazioneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}