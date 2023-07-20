package com.progettopwm.progettopwm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ActivityLoginBinding
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity
import com.google.gson.JsonObject
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

        if (!filePre.getString("Email", "").equals("")) {
            //da cambiare con la futura homepage
            val intent = Intent(this@LoginActivity, ProfiloUtenteActivity::class.java)
            startActivity(intent)
        } else {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)

            binding.loginButton.setOnClickListener {
                if (binding.emailPlainText.text.toString().trim().isEmpty() || binding.passwordPlainText.text.toString().trim().isEmpty()
                ) {
                    Toast.makeText(this, "Inserisci qualcosa nei campi", Toast.LENGTH_LONG).show()
                } else {
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
                    System.out.println(t.message)
                }
            }
        )
    }
}