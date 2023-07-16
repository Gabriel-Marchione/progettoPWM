package com.progettopwm.progettopwm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Window
import android.widget.Toast
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ActivityLoginBinding
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    lateinit var filePre : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        if (!filePre.getString("Email", "").equals("")) {
            //da cambiare con la futura homepage
            val intent = Intent(this@LoginActivity, ProfiloUtenteActivity::class.java)
            startActivity(intent)
        } else {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)

            binding.loginButton.setOnClickListener {
                if (binding.emailPlainText.text.toString().trim()
                        .isEmpty() || binding.passwordPlainText.text.toString().trim().isEmpty()
                ) {
                    Toast.makeText(this, "Inserisci qualcosa nei campi", Toast.LENGTH_LONG).show()
                } else {
                    effettuaQuery()
                }
            }

            binding.registrazioneTextView.setOnClickListener {
                Toast.makeText(this, "Hai premuto registrazione", Toast.LENGTH_LONG).show()
            }

            binding.mostraNascondiPassword.setOnClickListener {
                val immagineCorrente = binding.mostraNascondiPassword.drawable
                val immagineVecchia = resources.getDrawable(R.drawable.occhio_barrato, null)
                val immagineNuova = resources.getDrawable(R.drawable.occhio_aperto, null)

                if (immagineCorrente.constantState == immagineNuova.constantState) {
                    binding.mostraNascondiPassword.setImageDrawable(immagineVecchia)
                    binding.passwordPlainText.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                } else {
                    binding.mostraNascondiPassword.setImageDrawable(immagineNuova)
                    binding.passwordPlainText.transformationMethod = null
                }
            }

        }
    }

    fun effettuaQuery(){
        val query = "SELECT * FROM Utente WHERE email = '${binding.emailPlainText.text.toString().trim()}' AND password = '${binding.passwordPlainText.text.toString().trim()}'"
        System.out.println(query)
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj?.size() != 0 && obj?.get(0)?.asJsonObject?.get("email")?.equals("null") == false){
                                // cambiamo activity e salviamo le credenziali
                                val email = binding.emailPlainText.text.toString()//.trim()
                                val password = binding.passwordPlainText.text.toString()//.trim()

                                filePre = this@LoginActivity.getSharedPreferences("Credenziali", Context.MODE_PRIVATE)
                                val editor = filePre.edit()
                                editor.putString("Email", email)
                                editor.putString("Password", password)
                                editor.apply()
                                val intent = Intent(this@LoginActivity, ProfiloUtenteActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@LoginActivity, "Credenziali errate", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}