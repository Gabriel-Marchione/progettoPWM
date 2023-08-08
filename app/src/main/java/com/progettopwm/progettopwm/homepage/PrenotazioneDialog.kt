package com.progettopwm.progettopwm.homepage

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.progettopwm.databinding.PrenotazioneLettinoCustomDialogBinding
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PrenotazioneDialog(context : Context, indiceLettino : Int, dataCorrente : String, dataCorrenteFormattata : String, dataDB : String) : Dialog(context) {
    lateinit var binding: PrenotazioneLettinoCustomDialogBinding
    lateinit var filePre: SharedPreferences

    var numeroLettino = indiceLettino
    var dataCorrenteDialog = dataCorrente
    var dataCorrenteFormattata = dataCorrenteFormattata
    var dataDaInserireDB = dataDB

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PrenotazioneLettinoCustomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePre = context.getSharedPreferences("Credenziali", AppCompatActivity.MODE_PRIVATE)

        creaSpinner()

        binding.numeroLettinoTextView.setText(numeroLettino.toString())
        recuperaPrezzoLettino(numeroLettino.toString())

        binding.prenotaButton.setOnClickListener {
            controllaDisponibilitaLettinoPeriodo(
                dataDaInserireDB,
                calcolaData(binding.spinnerPrenota.selectedItem.toString().trim()),
                numeroLettino
            )
        }

    }


    private fun creaSpinner() {
        val spinner = binding.spinnerPrenota
        val giorni = arrayOf("Solo oggi", 1, 2, 3, 4, 5, 6, 7)

        val adapterSpinner = ArrayAdapter(context, R.layout.simple_spinner_item, giorni)
        adapterSpinner.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapterSpinner
    }

    private fun calcolaData(giorni: String): String {
        var dataCalcolata = ""

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dataInserireDBLocalDate = LocalDate.parse(dataDaInserireDB, formatter)

        if (giorni.equals("Solo oggi")) {
            dataCalcolata = dataInserireDBLocalDate.toString().trim()
        } else {
            dataCalcolata = dataInserireDBLocalDate.plusDays(giorni.toLong()).toString().trim()
        }
        return dataCalcolata
    }

    fun prenotaLettino(
        idLettinoBottone: Int,
        dataInizioPrenotazione: String,
        dataFinePrenotazione: String
    ) {
        val query =
            "INSERT INTO PrenotazioneLettino(idLettinoPrenotato, emailPrenotante, dataInizioPrenotazione, dataFinePrenotazione, flagPrenotazione) " +
                    "VALUES ('${idLettinoBottone}', '${
                        filePre.getString(
                            "Email",
                            ""
                        )
                    }', '${dataInizioPrenotazione}', '${dataFinePrenotazione}', 1)"
        System.out.println(query + "inserimento")
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            //aggiornaFlagLettino(idLettinoBottone)
                            Toast.makeText(
                                context,
                                "Lettino $idLettinoBottone prenotato",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(context, HomepageActivity::class.java)
                            startActivity(context, intent, null)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Si è verificato un problema con il server, riprova",
                            Toast.LENGTH_LONG
                        ).show()
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

    fun controllaDisponibilitaLettinoPeriodo(dataInizio: String, dataFine: String, idLettino: Int) {
        val query =
            "SELECT PL.idPrenotazione, PL.idLettinoPrenotato, PL.dataInizioPrenotazione, PL.dataFinePrenotazione " +
                    "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                    "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato " +
                    "AND PL.flagPrenotazione = 1 " +
                    "AND PL.idLettinoPrenotato = '${idLettino}' " +
                    "AND ('${dataInizio}' BETWEEN PL.dataInizioPrenotazione AND PL.dataFinePrenotazione " +
                    "OR '${dataFine}' BETWEEN PL.dataInizioPrenotazione AND PL.dataFinePrenotazione)"
        System.out.println("disponibilita " + query)
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if (obj != null && obj.size() == 0) {
                                prenotaLettino(idLettino, dataInizio, dataFine)
                            } else {
                                val dataInizioPrenotazione = obj?.get(0)?.asJsonObject?.get("dataInizioPrenotazione")?.toString()?.trim('"')
                                val dataFinePrenotazione = obj?.get(0)?.asJsonObject?.get("dataFinePrenotazione")?.toString()?.trim('"')
                                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                val dataInizioFormatted = LocalDate.parse(dataInizioPrenotazione, DateTimeFormatter.ISO_LOCAL_DATE).format(formatter)
                                val dataFineFormatted = LocalDate.parse(dataFinePrenotazione, DateTimeFormatter.ISO_LOCAL_DATE).format(formatter)
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Errore prenotazione")
                                    .setMessage(
                                        "Il lettino scelto è occupato in questo periodo:\n" +
                                                "Dal ${dataInizioFormatted} " +
                                                "al ${dataFineFormatted}" +
                                                "\nRiprova a scegliere un altro periodo o un altro lettino."
                                    )
                                    .setPositiveButton("Riprova") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()
                                alertDialog.show()
                            }
                        }
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

    fun recuperaPrezzoLettino(idLettino: String){
        val query = "SELECT L.prezzo " +
                "FROM Lettino L " +
                "WHERE L.idLettino = $idLettino"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null) {
                            val obj = response.body()?.getAsJsonArray("queryset")
                            binding.prezzoLettinoTextView.setText(obj?.get(0)?.asJsonObject?.get("prezzo")?.toString()?.trim('"'))
                        }
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