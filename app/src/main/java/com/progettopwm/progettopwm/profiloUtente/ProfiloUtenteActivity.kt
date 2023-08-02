package com.progettopwm.progettopwm.profiloUtente

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.R
import com.progettopwm.progettopwm.Utils.ClientNetwork
import com.example.progettopwm.databinding.ActivityVisualizzazioneProfiloBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.progettopwm.progettopwm.Utils.BottomNavigationManager
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.BenvenutoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfiloUtenteActivity : AppCompatActivity() {
    lateinit var binding : ActivityVisualizzazioneProfiloBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences
    lateinit var fileAvatar : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizzazioneProfiloBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.profiloMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)
        fileAvatar = this.getSharedPreferences("File avatar", MODE_PRIVATE)

        //mi prendo l'id dell'immagine che si trova nel file, ovvero quella selezionata nel dialog o scelta nella fase di registrazione
        binding.avatarImageView.setImageResource(fileAvatar.getInt("idImmagineAvatar", R.drawable.avatar)) // R.drawable.avatar è l'avatar di default
        effettuaQuery()

        binding.logoutButton.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Conferma")
                .setMessage("Vuoi effettuare il logout? Perderai la preferenza dell'avatar scelto ma potrai sceglierlo nuovamente.")
                .setPositiveButton("Conferma") { dialog, which ->
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
                .setNegativeButton("Annulla", null)
                .create()
            alertDialog.show()
        }

        binding.cambiaAvatarButton.setOnClickListener {
            val dialog = ModificaAvatarCustomDialog(this)
            dialog.show()
        }

        binding.avatarImageView.setOnClickListener {
            val dialog = ModificaAvatarCustomDialog(this)
            dialog.show()
        }

    }

    fun effettuaQuery(){
        val query = "SELECT email, nome, cognome, telefono, password " +
                    "FROM Utente " +
                    "WHERE email = '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            binding.emailTextView.text = obj?.get(0)?.asJsonObject?.get("email")?.asString
                            binding.nomeTextView.text = obj?.get(0)?.asJsonObject?.get("nome")?.asString
                            binding.cognomeTextView.text = obj?.get(0)?.asJsonObject?.get("cognome")?.asString
                            binding.telefonoTextView.text = obj?.get(0)?.asJsonObject?.get("telefono")?.asString
                            binding.passwordTextView.text = obj?.get(0)?.asJsonObject?.get("password")?.asString
                        }
                    }
                    //System.out.println("response" + response)
                    //System.out.println("response body" + response.body())
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