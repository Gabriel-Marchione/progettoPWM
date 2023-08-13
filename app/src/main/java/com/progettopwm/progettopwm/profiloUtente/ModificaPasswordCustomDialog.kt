package com.progettopwm.progettopwm.profiloUtente

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.example.progettopwm.databinding.CustomDialogModificaPasswordBinding
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModificaPasswordCustomDialog (context: Context, val emailActivity : String?, val passwordActivity : String) : Dialog(context) {
    lateinit var binding : CustomDialogModificaPasswordBinding
    lateinit var filePre : SharedPreferences
    val email = emailActivity
    val password = passwordActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogModificaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePre = context.getSharedPreferences("Credenziali", AppCompatActivity.MODE_PRIVATE)

        binding.confermaModificaPasswordButton.setOnClickListener {
            if(checkCampi()){
                aggiornaPassword()
                dismiss()
            }
        }
    }

    private fun checkCampi() : Boolean{
        var check = false

        if(
           binding.passwordVecchiaModificaPasswordPlainText.text.trim().isNotEmpty() &&
                   binding.passwordNuovaModificaPasswordPlainText.text.trim().isNotEmpty()
        ){
            check = true
            if (!binding.passwordVecchiaModificaPasswordPlainText.text.toString().equals(password, ignoreCase = true)) {
                check = false
                Toast.makeText(context, "La vecchia password non corrisponde", Toast.LENGTH_LONG).show()
            }
        }else
            Toast.makeText(context, "I campi sono vuoti", Toast.LENGTH_LONG).show()
        return check
    }

    fun aggiornaPassword(){
        val query = "UPDATE Utente " +
                "SET password = '${binding.passwordNuovaModificaPasswordPlainText.text.trim()}' " +
                "WHERE email = '${email}'"
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        val editor = filePre.edit()
                        editor.putString("Password", binding.passwordNuovaModificaPasswordPlainText.text.toString().trim())
                        editor.apply()
                        Toast.makeText(context, "Dati aggiornati correttamente", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, ProfiloUtenteActivity::class.java)
                        ContextCompat.startActivity(context, intent, null)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }
}