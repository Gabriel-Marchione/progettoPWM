package com.progettopwm.progettopwm1.profiloUtente

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.progettopwm.progettopwm1.ClientNetwork
import com.progettopwm.progettopwm1.LoginActivity
import com.example.progettopwm.databinding.ActivityVisualizzazioneProfiloBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfiloUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityVisualizzazioneProfiloBinding
    lateinit var filePre : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneProfiloBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        effettuaQuery()

        binding.logoutButton.setOnClickListener {
            val editor = filePre.edit()
            editor.clear()
            editor.apply()
            Toast.makeText(this@ProfiloUtenteActivity,"Disconnessione avvenuta con succeso", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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