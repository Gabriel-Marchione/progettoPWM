package com.progettopwm.progettopwm.cronologiaAcquistiConsumazioni

import android.content.SharedPreferences
import android.database.CursorWindowAllocationException
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ActivityCronologiaPrenotazioniAcquistiBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.acquistoConsumazioni.ItemsViewModel
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
    lateinit var adapterLettini : CronologiaAcquistiConsumazioniCustomAdapter


    //todo aggiungere cronologia dentro il linear layout dei lettini
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityCronologiaPrenotazioniAcquistiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.cronologiaMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        binding.cronologiaLettiniRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cronologiaConsumazioniRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recuperaLettiniNoleggiatiPresente()

        binding.nascondiMostraCronologiaLettiniButton.setOnClickListener {
            if(binding.cronologiaLettiniRecyclerView.visibility == VISIBLE){
                binding.cronologiaLettiniRecyclerView.visibility = GONE
                binding.nascondiMostraCronologiaLettiniButton.text = "Mostra lettini prenotati"
            }else if (binding.cronologiaLettiniRecyclerView.visibility == GONE){
                binding.cronologiaLettiniRecyclerView.visibility = VISIBLE
                binding.nascondiMostraCronologiaLettiniButton.text = "Nascondi lettini prenotati"
            }
        }

        binding.nascondiMostraCronologiaConsumazioniButton.setOnClickListener {
            if(binding.cronologiaConsumazioniRecyclerView.visibility == VISIBLE){
                binding.cronologiaLettiniRecyclerView.visibility = GONE
                binding.nascondiMostraCronologiaConsumazioniButton.text = "Mostra cronologia consumazioni"
            }else if (binding.cronologiaLettiniRecyclerView.visibility == GONE){
                binding.cronologiaConsumazioniRecyclerView.visibility = VISIBLE
                binding.nascondiMostraCronologiaConsumazioniButton.text = "Nascondi cronologia consumazioni"
            }
        }

    }

    fun recuperaLettiniNoleggiatiPresente(){
        val query = "SELECT PL.idLettinoPrenotato, PL.idPrenotazione, PL.dataInizioPrenotazione, PL.dataFinePrenotazione, L.prezzo " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato AND PL.flagPrenotazione = 1 AND U.email = '${filePre.getString("Email", "")}'"
        val data = ArrayList<ItemsViewModelCronologiaAcquistiConsumazioni>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
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
                                    data.add(ItemsViewModelCronologiaAcquistiConsumazioni(idLettino, numeroPrenotazione, formattedDataInizio, formattedDataFine, prezzoLettino))
                                    }
                                adapterLettini = CronologiaAcquistiConsumazioniCustomAdapter(data)
                                binding.cronologiaLettiniRecyclerView.adapter = adapterLettini
                            }/*else{
                                //todo aggiungere testo per indicare che non c'è nessun lettino attualmente prenotato
                            }*/
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@CronologiaPrenotazioniAcquistiActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )

    }
}