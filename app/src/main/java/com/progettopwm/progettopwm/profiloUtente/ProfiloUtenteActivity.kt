package com.progettopwm.progettopwm.profiloUtente

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.R
import com.progettopwm.progettopwm.ClientNetwork
import com.progettopwm.progettopwm.LoginActivity
import com.example.progettopwm.databinding.ActivityVisualizzazioneProfiloBinding
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.BenvenutoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfiloUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityVisualizzazioneProfiloBinding
    lateinit var filePre : SharedPreferences
    lateinit var fileAvatar : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneProfiloBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)
        fileAvatar = this.getSharedPreferences("File avatar", MODE_PRIVATE)

        //mi prendo l'id dell'immagine che si trova nel file, ovvero quella selezionata nel dialog o scelta nella fase di registrazione
        binding.avatarImageView.setImageResource(fileAvatar.getInt("idImmagineAvatar", R.drawable.avatar)) // R.drawable.avatar è l'avatar di default
        effettuaQuery()

        binding.logoutButton.setOnClickListener {
            val editor = filePre.edit()
            editor.clear()
            editor.apply()
            val editor2 = fileAvatar.edit()
            editor2.clear()
            editor2.apply()
            Toast.makeText(this@ProfiloUtenteActivity,"Disconnessione avvenuta con succeso", Toast.LENGTH_LONG).show()
            val intent = Intent(this, BenvenutoActivity::class.java)
            startActivity(intent)
        }

        binding.cambiaAvatarButton.setOnClickListener {
            val dialog = ModificaAvatarCustomDialog(this)
            dialog.show()
        }

        binding.avatarImageView.setOnClickListener {
            //TODO modificare?
        }

    }

    fun effettuaQuery(){
        val query = "SELECT nome, cognome, email, telefono, password " +  //manca la posizione
                    "FROM Utente " +
                    "WHERE email = '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            binding.nomeTextView.text = obj?.get(0)?.asJsonObject?.get("nome")?.asString
                            binding.cognomeTextView.text = obj?.get(0)?.asJsonObject?.get("cognome")?.asString
                            binding.emailTextView.text = obj?.get(0)?.asJsonObject?.get("email")?.asString
                            binding.telefonoTextView.text = obj?.get(0)?.asJsonObject?.get("telefono")?.asString
                            binding.passwordTextView.text = obj?.get(0)?.asJsonObject?.get("password")?.asString
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@ProfiloUtenteActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }
}