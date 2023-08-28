package com.progettopwm.progettopwm.cronologiaAcquistiConsumazioni

import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View.GONE
import android.view.View.combineMeasuredStates
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.progettopwm.R
import com.example.progettopwm.databinding.AcquistoConsumazioniCardViewBinding
import com.example.progettopwm.databinding.ActivityCronologiaPrenotazioniAcquistiBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.acquistoConsumazioni.ConsumazioniCustomAdapter
import com.progettopwm.progettopwm.acquistoConsumazioni.ConsumazioniItemsViewModel
import com.progettopwm.progettopwm.utils.BottomNavigationManager
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class CronologiaPrenotazioniAcquistiActivity : AppCompatActivity() {
    lateinit var binding : ActivityCronologiaPrenotazioniAcquistiBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences
    lateinit var adapterLettiniPrenotatiPresente : LettiniCustomAdapter
    lateinit var adapterLettiniPrenotatiPassato : LettiniCustomAdapter
    lateinit var adapterCronologiaAcquisti : ConsumazioniAcquistateCustomAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityCronologiaPrenotazioniAcquistiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.cronologiaMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        binding.lettiniPrenotatiRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cronologiaLettiniPrenotatiRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cronologiaConsumazioniRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        recuperaLettiniNoleggiatiPresente()
        //recuperaLettiniNoleggiatiInPassato()
        //recuperaCronologiaAcquistiConsumazioni()

        mostraNascondiLettiniAttuali()
        mostraNascondiLettiniPassati()
        mostraNascondiCronologiaAcquisti()

    }


    fun mostraNascondiLettiniAttuali(){
        binding.nascondiMostraLettiniButton.setOnClickListener {
            if(binding.lettiniPrenotatiRecyclerView.visibility == VISIBLE){
                binding.lettiniPrenotatiRecyclerView.visibility = GONE
                binding.nascondiMostraLettiniButton.text = "Mostra lettini prenotati"
            }else if (binding.lettiniPrenotatiRecyclerView.visibility == GONE){
                binding.lettiniPrenotatiRecyclerView.visibility = VISIBLE
                binding.nascondiMostraLettiniButton.text = "Nascondi lettini prenotati"
            }
        }
    }

    fun mostraNascondiLettiniPassati(){
        binding.nascondiMostraCronologiaLettiniPrenotatiButton.setOnClickListener {
            if(binding.cronologiaLettiniPrenotatiRecyclerView.visibility == VISIBLE){
                binding.cronologiaLettiniPrenotatiRecyclerView.visibility = GONE
                binding.nascondiMostraLettiniButton.text = "Mostra lettini prenotati in passato"
            }else if (binding.cronologiaLettiniPrenotatiRecyclerView.visibility == GONE){
                binding.cronologiaLettiniPrenotatiRecyclerView.visibility = VISIBLE
                binding.nascondiMostraLettiniButton.text = "Nascondi lettini prenotati in passato"
            }
        }
    }

    fun mostraNascondiCronologiaAcquisti(){
        binding.nascondiMostraCronologiaConsumazioniButton.setOnClickListener {
            if(binding.cronologiaConsumazioniRecyclerView.visibility == VISIBLE){
                binding.cronologiaConsumazioniRecyclerView.visibility = GONE
                binding.nascondiMostraCronologiaConsumazioniButton.text = "Mostra cronologia consumazioni"
            }else if (binding.cronologiaConsumazioniRecyclerView.visibility == GONE){
                binding.cronologiaConsumazioniRecyclerView.visibility = VISIBLE
                binding.nascondiMostraCronologiaConsumazioniButton.text = "Nascondi cronologia consumazioni"
            }
        }
    }

    fun recuperaLettiniNoleggiatiPresente(){
        /*val query = "SELECT PL.idLettinoPrenotato, PL.idPrenotazione, PL.dataInizioPrenotazione, PL.dataFinePrenotazione, L.prezzo " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato AND PL.flagPrenotazione = 1 AND U.email = '${filePre.getString("Email", "")}'"*/
        val query = "SELECT PL.idLettinoPrenotato, PL.idPrenotazione, PL.dataInizioPrenotazione, PL.dataFinePrenotazione, L.prezzo, PL.flagPrenotazione " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato AND PL.flagPrenotazione = 1 AND '${LocalDate.now().toString().trim()}' <= PL.dataFinePrenotazione AND U.email = '${filePre.getString("Email", "")}'"
        System.out.println(query + "ciao1 ")
        val data = ArrayList<LettiniItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            recuperaLettiniNoleggiatiInPassato()
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if (obj != null && obj.size() > 0){
                                for (i in 0 until obj.size()) {
                                    val idLettino = obj[i].asJsonObject?.get("idLettinoPrenotato")?.asString?.trim('"')
                                    val numeroPrenotazione = obj[i]?.asJsonObject?.get("idPrenotazione")?.asString?.trim('"')
                                    //convertire in gg-mm-aaaa
                                    val dataInizioPrenotazione = obj[i].asJsonObject?.get("dataInizioPrenotazione")?.asString?.trim('"')
                                    val dataFinePrenotazione = obj[i].asJsonObject?.get("dataFinePrenotazione")?.asString?.trim('"')
                                    // Conversione delle date nel formato "dd-MM-yyyy"
                                    val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                    val formattedDataInizio = sdfOutput.format(sdfInput.parse(dataInizioPrenotazione))
                                    val formattedDataFine = sdfOutput.format(sdfInput.parse(dataFinePrenotazione))

                                    val prezzoLettino = obj[i].asJsonObject?.get("prezzo")?.asString?.trim('"')
                                    data.add(LettiniItemsViewModel(idLettino, numeroPrenotazione, formattedDataInizio, formattedDataFine, "€ " + prezzoLettino))
                                    }
                                adapterLettiniPrenotatiPresente = LettiniCustomAdapter(data)
                                binding.lettiniPrenotatiRecyclerView.adapter = adapterLettiniPrenotatiPresente

                                adapterLettiniPrenotatiPresente.setOnClickListener(object : LettiniCustomAdapter.OnClickListener{
                                    override fun onClick(position: Int, model: LettiniItemsViewModel) {
                                        mostraDialogoConfermaTerminazionePrenotazione(model.numeroPrenotazione, model.idLettino)
                                    }
                                })

                            }else{
                                binding.noLettiniPrenotatiTextView.visibility = VISIBLE
                                binding.nascondiMostraLettiniButton.visibility = GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@CronologiaPrenotazioniAcquistiActivity,
                        "Errore del Database o assenza di connessione 1",
                        Toast.LENGTH_LONG
                    ).show()
                    System.out.println("messaggio errrore1 : " + t.message + " , causa:" + t.cause + " \n completo: " + t.toString())
                }

            }
        )
    }

    fun recuperaLettiniNoleggiatiInPassato(){
        /*val query = "SELECT PL.idLettinoPrenotato, PL.idPrenotazione, PL.dataInizioPrenotazione, PL.dataFinePrenotazione, L.prezzo, PL.flagPrenotazione " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato AND (PL.flagPrenotazione = 0 OR '${LocalDate.now().toString().trim()}' > PL.dataFinePrenotazione) AND U.email = '${filePre.getString("Email", "")}'"*/
        val query = "SELECT PL.idLettinoPrenotato, PL.idPrenotazione, PL.dataInizioPrenotazione, PL.dataFinePrenotazione, L.prezzo, PL.flagPrenotazione " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato AND (PL.flagPrenotazione = 0 OR '${LocalDate.now().toString().trim()}' >= PL.dataFinePrenotazione) AND U.email = '${filePre.getString("Email", "")}'"
        System.out.println(query + "ciao2 ")
        val data = ArrayList<LettiniItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            recuperaCronologiaAcquistiConsumazioni()
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if (obj != null && obj.size() > 0){
                                for (i in 0 until obj.size()) {
                                    val idLettino = obj[i].asJsonObject?.get("idLettinoPrenotato")?.asString?.trim('"')
                                    val numeroPrenotazione = obj[i]?.asJsonObject?.get("idPrenotazione")?.asString?.trim('"')
                                    //convertire in gg-mm-aaaa
                                    val dataInizioPrenotazione = obj[i].asJsonObject?.get("dataInizioPrenotazione")?.asString?.trim('"')
                                    val dataFinePrenotazione = obj[i].asJsonObject?.get("dataFinePrenotazione")?.asString?.trim('"')
                                    // Conversione delle date nel formato "dd-MM-yyyy"
                                    val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                    val formattedDataInizio = sdfOutput.format(sdfInput.parse(dataInizioPrenotazione))
                                    val formattedDataFine = sdfOutput.format(sdfInput.parse(dataFinePrenotazione))

                                    val prezzoLettino = obj[i].asJsonObject?.get("prezzo")?.asString?.trim('"')
                                    data.add(LettiniItemsViewModel(idLettino, numeroPrenotazione, formattedDataInizio, formattedDataFine, "€ " + prezzoLettino))
                                }
                                adapterLettiniPrenotatiPassato = LettiniCustomAdapter(data)
                                binding.cronologiaLettiniPrenotatiRecyclerView.adapter = adapterLettiniPrenotatiPassato
                            }else{
                                binding.noLettiniPrenotatiCronologiaTextView.visibility = VISIBLE
                                binding.nascondiMostraCronologiaLettiniPrenotatiButton.visibility = GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@CronologiaPrenotazioniAcquistiActivity,
                        "Errore del Database o assenza di connessione 2",
                        Toast.LENGTH_LONG
                    ).show()
                    System.out.println("messaggio errrore2 : " + t.message + " , causa:" + t.cause + " \n completo: " + t.toString())
                }

            }
        )
    }

    fun recuperaCronologiaAcquistiConsumazioni(){
        val query = "SELECT APA.idTransazione, PA.denominazione, PA.ingredienti, PA.prezzo, PA.imgProdotto, APA.dataAcquisto " +
                "FROM Utente U, ProdottoAlimentare PA, AcquistoProdottoAlimentare APA " +
                "WHERE U.email = APA.emailUtente AND PA.idProdottoAlimentare = APA.idProdottoAcquistato AND APA.emailUtente = '${filePre.getString("Email", "")}' " +
                "ORDER BY APA.dataAcquisto DESC"
        val data = ArrayList<ConsumazioniAcquistateItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if (response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if (obj != null && obj.size() > 0){
                                for (i in 0 until obj.size()) {
                                    val idProdotto = obj[i].asJsonObject?.get("idTransazione")?.asString
                                    val denominazione = obj[i].asJsonObject?.get("denominazione")?.asString
                                    val immagineURL = obj[i].asJsonObject?.get("imgProdotto")?.asString
                                    val dataAcquisto = obj[i].asJsonObject?.get("dataAcquisto")?.asString
                                    val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                    val formattedDataAcquisto = sdfOutput.format(sdfInput.parse(dataAcquisto))
                                    data.add(ConsumazioniAcquistateItemsViewModel(idProdotto, formattedDataAcquisto, denominazione, immagineURL))
                                }
                                adapterCronologiaAcquisti = ConsumazioniAcquistateCustomAdapter(data)
                                binding.cronologiaConsumazioniRecyclerView.adapter = adapterCronologiaAcquisti
                            }else{
                                binding.noConsumazioniCronologiaTextView.visibility = VISIBLE
                                binding.nascondiMostraCronologiaConsumazioniButton.visibility = GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@CronologiaPrenotazioniAcquistiActivity,
                        "Errore del Database o assenza di connessione 3",
                        Toast.LENGTH_LONG
                    ).show()
                    System.out.println("messaggio errrore3 : " + t.message + " , causa:" + t.cause + " \n completo: " + t.toString())
                }

            }
        )
    }

    fun mostraDialogoConfermaTerminazionePrenotazione(numeroPrenotazione : String?, idLettino : String?){
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Termina prenotazione")
            .setMessage("Vuoi terminare la prenotazione #${numeroPrenotazione} del lettino N° ${idLettino}?")
            .setPositiveButton("Termina") {dialog , _ ->
                queryterminaPrenotazione(numeroPrenotazione, idLettino)
                dialog.dismiss()
            }
            .setNegativeButton("Annulla", null)
            .create()
        dialog.show()
    }

    fun queryterminaPrenotazione(numeroPrenotazione: String?, idLettino: String?){
        val query = "UPDATE PrenotazioneLettino SET flagPrenotazione = 0 WHERE idPrenotazione = ${numeroPrenotazione}"
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@CronologiaPrenotazioniAcquistiActivity, "Prenotazione #${numeroPrenotazione} del lettino ${idLettino} terminata", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@CronologiaPrenotazioniAcquistiActivity, CronologiaPrenotazioniAcquistiActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@CronologiaPrenotazioniAcquistiActivity,
                        "Errore del Database o assenza di connessione 4",
                        Toast.LENGTH_LONG
                    ).show()
                    System.out.println("messaggio errrore4: " + t.message + " , causa:" + t.cause + " \n completo: " + t.toString())
                }

            }
        )
    }


}