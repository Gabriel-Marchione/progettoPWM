package com.progettopwm.progettopwm.profiloUtente

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.progettopwm.databinding.CustomDialogModificaDatiBinding
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ModificaDatiCustomDialog(context: Context, emailActivity : String?, nomeActivity : String, cognomeActivity : String, dataNascitaActivity : String,
                               telefonoActivity : String, cartaCreditoActivity : String) : Dialog(context) {

    lateinit var binding : CustomDialogModificaDatiBinding
    lateinit var filePre : SharedPreferences
    lateinit var dataDaInserireDB : String
    val email = emailActivity
    val nome = nomeActivity
    val cognome = cognomeActivity
    val dataNascita = dataNascitaActivity
    val telefono = telefonoActivity
    val cartaCredito = cartaCreditoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogModificaDatiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePre = context.getSharedPreferences("Credenziali", AppCompatActivity.MODE_PRIVATE)
        System.out.println("custom dialog" + filePre.all)
        binding.emailModificaProfiloPlainText.setText(email)
        binding.nomeModificaProfiloPlainText.setText(nome)
        binding.cognomeModificaProfiloPlainText.setText(cognome)
        binding.dataNascitaModificaPlainText.setText(dataNascita)
        binding.telefonoModificaProfiloPlainText.setText(telefono)
        binding.cartaCreditoModificaProfiloPlainText.setText(cartaCredito)

        binding.dataNascitaModificaPlainText.isClickable = false
        binding.dataNascitaModificaPlainText.isFocusable = false

        val calendar = Calendar.getInstance()

        // creiamo l'oggetto datepicker e inizializziamo un listener
        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar)
        }
        binding.selezionaDataNascitaModifica.setOnClickListener {
            DatePickerDialog(context, datePicker, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dataDaInserireDB = parseData(binding.dataNascitaModificaPlainText.text.toString())

        binding.confermaModificaButton.setOnClickListener {
            if(checkCampi()) {
                aggiornaDatiProfilo()
                dismiss()
            }
        }

        binding.annullaButton.setOnClickListener {
            dismiss()
        }
    }

    private fun updateLable(calendar: Calendar) {
        val selectedDate = calendar.time
        val oggi = Calendar.getInstance()
        val isDateValid = selectedDate.before(oggi.time)

        if (!isDateValid) {
            Toast.makeText(
                context,
                "Errore nell'inserimento della data",
                Toast.LENGTH_LONG
            ).show()
        }else{
            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)
            binding.dataNascitaModificaPlainText.setText(formattedDate)

            val format2 = "yyyy-MM-dd"
            val sdf2 = SimpleDateFormat(format2, Locale.getDefault())
            dataDaInserireDB = sdf2.format(calendar.time)
        }
    }

    private fun parseData(dataDaFormattare : String) : String{
        val sdfInput = android.icu.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdfOutput = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDataNascita = sdfOutput.format(sdfInput.parse(dataDaFormattare))

        return formattedDataNascita
    }

    fun aggiornaDatiProfilo(){
        val query = "UPDATE Utente " +
                "SET email = '${binding.emailModificaProfiloPlainText.text.toString().trim()}', " +
                "nome = '${binding.nomeModificaProfiloPlainText.text.toString().trim()}', " +
                "cognome = '${binding.cognomeModificaProfiloPlainText.text.toString().trim()}', " +
                "dataNascita = '${dataDaInserireDB}', " +
                "telefono = '${binding.telefonoModificaProfiloPlainText.text.toString().trim()}', " +
                "cartaCredito = '${binding.cartaCreditoModificaProfiloPlainText.text.toString().trim()}' " +
                "WHERE email = '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        val editor = filePre.edit()
                        editor.putString("Email", binding.emailModificaProfiloPlainText.text.toString().trim())
                        editor.apply()
                        Toast.makeText(context, "Dati aggiornati correttamente", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, ProfiloUtenteActivity::class.java)
                        startActivity(context, intent, null)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                    System.out.println(t.message)
                }

            }
        )
    }

    private fun checkCampi() : Boolean{
        val patterNomeCognomeEmail = Regex("^[0-9]+")
        val patternTelefono = Regex("^[0-9]{10}")
        val patternCartaDiCredito = Regex("^[0-9]{16}")
        var check = false

        if(
            binding.emailModificaProfiloPlainText.text.trim().isNotEmpty() && binding.nomeModificaProfiloPlainText.text.trim().isNotEmpty()
            && binding.cognomeModificaProfiloPlainText.text.trim().isNotEmpty() && binding.dataNascitaModificaPlainText.text.trim().isNotEmpty()
            && binding.telefonoModificaProfiloPlainText.text.trim().isNotEmpty() && binding.cartaCreditoModificaProfiloPlainText.text.trim().isNotEmpty()
        ){
            check = true
            if(binding.emailModificaProfiloPlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(context, "Inserire una email valida", Toast.LENGTH_LONG).show()
            }else if(binding.nomeModificaProfiloPlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(context, "Inserire un nome valido", Toast.LENGTH_LONG).show()
            }else if(binding.cognomeModificaProfiloPlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(context, "Inserire un cognome valido", Toast.LENGTH_LONG).show()
            }else if(!binding.telefonoModificaProfiloPlainText.text.matches(patternTelefono)){
                check = false
                Toast.makeText(context, "Inserire un numero di telefono valido", Toast.LENGTH_LONG).show()
            }else if(!binding.cartaCreditoModificaProfiloPlainText.text.matches(patternCartaDiCredito)){
                check = false
                Toast.makeText(context, "Inserire una numero di carta valido", Toast.LENGTH_LONG).show()
            }
        }else
            Toast.makeText(context, "I campi sono vuoti", Toast.LENGTH_LONG).show()
        return check
    }
}