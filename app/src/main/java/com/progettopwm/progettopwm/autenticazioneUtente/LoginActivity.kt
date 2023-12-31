package com.progettopwm.progettopwm.autenticazioneUtente

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
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.ClientNetwork
import com.progettopwm.progettopwm.homepage.HomepageActivity
import com.progettopwm.progettopwm.registrazioneUtente.RegistrazioneUtenteActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    lateinit var filePre : SharedPreferences
    var flagOcchioBarrato : Boolean = true //true occhio barrato, false aperto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            if (checkCampi()) {
                effettuaQuery()
            }
        }

        binding.registrazioneTextView.setOnClickListener {
            val intent = Intent(this, RegistrazioneUtenteActivity::class.java)
            startActivity(intent)
        }

        binding.cancellaTesto.setOnClickListener {
            binding.emailPlainText.text = null
        }

        binding.mostraNascondiPassword.setOnClickListener {
            if(flagOcchioBarrato){ //se l'occhio è barrato allora rendo visibile il testo e cambio immagine
                binding.mostraNascondiPassword.setImageResource(R.drawable.occhio_aperto)
                binding.passwordPlainText.transformationMethod = null
                flagOcchioBarrato = false

            }else{ //altrimenti, se l'occhio non è barrato, lo rendo nascosto e cambio immagine
                binding.mostraNascondiPassword.setImageResource(R.drawable.occhio_barrato)
                binding.passwordPlainText.transformationMethod = PasswordTransformationMethod.getInstance()
                flagOcchioBarrato = true
            }
        }


    }


    private fun checkCampi(): Boolean {
        val patternEmail = Regex("^[a-zA-Z0-9.]+@[a-zA-Z]+\\.([a-zA-Z]+)$")
        var check = false

        if (binding.emailPlainText.text.trim().isNotEmpty() && binding.passwordPlainText.text.trim().isNotEmpty()) {
            check = true
            if (!binding.emailPlainText.text.matches(patternEmail)) {
                check = false
                Toast.makeText(this@LoginActivity, "Inserire una email valida", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this@LoginActivity, "I campi sono vuoti", Toast.LENGTH_LONG).show()
        }
        return check
    }


    fun effettuaQuery(){
        val query = "SELECT * FROM Utente WHERE email = '${binding.emailPlainText.text.toString().trim()}' AND password = '${binding.passwordPlainText.text.toString().trim()}'"
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
                                val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
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
                    System.out.println(t.message)
                }
            }
        )
    }
}