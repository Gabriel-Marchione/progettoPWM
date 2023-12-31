package com.progettopwm.progettopwm.profiloUtente

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View.GONE
import android.view.Window
import android.webkit.RenderProcessGoneDetail
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.R
import com.progettopwm.progettopwm.utils.ClientNetwork
import com.example.progettopwm.databinding.ActivityVisualizzazioneProfiloBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.progettopwm.progettopwm.utils.BottomNavigationManager
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.BenvenutoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Boolean.FALSE
import java.util.*

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
        System.out.println("filePre" + filePre.all)
        fileAvatar = this.getSharedPreferences("File avatar", MODE_PRIVATE)

        //mi prendo l'id dell'immagine che si trova nel file, ovvero quella selezionata nel dialog o scelta nella fase di registrazione
        binding.avatarImageView.setImageResource(fileAvatar.getInt("idImmagineAvatar", R.drawable.avatar)) // R.drawable.avatar è l'avatar di default
        effettuaQuery()

        binding.logoutButton.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Conferma logout")
                .setMessage("Vuoi effettuare il logout? Perderai la preferenza dell'avatar scelto ma potrai sceglierlo nuovamente.")
                .setPositiveButton("Logout" +
                        "") { dialog, which ->
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

        binding.modificaDatiButton.setOnClickListener {
            val dialog = ModificaDatiCustomDialog(this, filePre.getString("Email", "")?.toString()?.trim(), binding.nomeTextView.text.toString(), binding.cognomeTextView.text.toString(),
                    binding.dataNascitaTextView.text.toString(), binding.telefonoTextView.text.toString(), binding.cartaCreditoTextView.text.toString())
            dialog.show()
        }

        binding.modificaPasswordButton.setOnClickListener {
            val dialog = ModificaPasswordCustomDialog(this, filePre.getString("Email", "")?.toString()?.trim(), binding.passwordTextView.text.toString())
            dialog.show()
        }

    }

    fun effettuaQuery(){
        val query = "SELECT email, nome, cognome, dataNascita, telefono, cartaCredito, password " +
                    "FROM Utente " +
                    "WHERE email = '${filePre.getString("Email", "")}' AND password = '${filePre.getString("Password", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            System.out.println(obj)
                            if(obj != null && obj.size() > 0) {
                                binding.emailTextView.text = obj.get(0)?.asJsonObject?.get("email")?.asString
                                binding.nomeTextView.text = obj.get(0)?.asJsonObject?.get("nome")?.asString
                                binding.cognomeTextView.text = obj.get(0)?.asJsonObject?.get("cognome")?.asString
                                val dataNascita = obj.get(0)?.asJsonObject?.get("dataNascita")?.asString
                                // Conversione delle date nel formato "dd-MM-yyyy"
                                val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val formattedDataNascita = sdfOutput.format(sdfInput.parse(dataNascita))
                                binding.dataNascitaTextView.text = formattedDataNascita
                                binding.telefonoTextView.text = obj.get(0)?.asJsonObject?.get("telefono")?.asString
                                binding.cartaCreditoTextView.text = obj.get(0)?.asJsonObject?.get("cartaCredito")?.asString
                                binding.passwordTextView.text = obj.get(0)?.asJsonObject?.get("password")?.asString
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@ProfiloUtenteActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.nomeTextView.visibility = GONE
                    binding.cognomeTextView.visibility = GONE
                    binding.avatarImageView.isClickable = FALSE
                    binding.cambiaAvatarButton.isClickable = FALSE
                    binding.modificaDatiButton.isClickable = FALSE
                    binding.modificaPasswordButton.isClickable = FALSE

                    val listaCampiBinding = listOf(binding.linearLayoutEmail, binding.linearLayoutDataNascita, binding.linearLayoutNumTel, binding.linearLayoutCartaCredito, binding.linearLayoutPassword)
                    for (oggetto in listaCampiBinding){
                        oggetto.visibility = GONE
                    }

                    val listaBinding = listOf(binding.avatarImageView, binding.cambiaAvatarButton, binding.modificaDatiButton,binding.modificaPasswordButton)

                    for(oggetto in listaBinding){
                        oggetto.setOnClickListener {
                            Toast.makeText(this@ProfiloUtenteActivity, "Operazione non disponibile in assenza di connessione", Toast.LENGTH_LONG).show()
                        }
                    }


                }

            }
        )
    }
}