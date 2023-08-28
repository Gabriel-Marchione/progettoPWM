package com.progettopwm.progettopwm.acquistoConsumazioni

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progettopwm.R
import com.example.progettopwm.databinding.AcquistoConsumazioniCardViewBinding
import com.example.progettopwm.databinding.ActivityAcquistoConsumazioniBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.BottomNavigationManager
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import java.time.LocalDate

class AcquistoConsumazioniActivity : AppCompatActivity() {
    lateinit var binding : ActivityAcquistoConsumazioniBinding
    lateinit var binding2 : AcquistoConsumazioniCardViewBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences
    lateinit var adapterBevande: ConsumazioniCustomAdapter
    lateinit var adapterCibo: ConsumazioniCustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcquistoConsumazioniBinding.inflate(layoutInflater)
        binding2 = AcquistoConsumazioniCardViewBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.consumazioniMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        binding.bevandeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.ciboRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        restituisciConsumazioni()
    }

    fun restituisciConsumazioni(){
        val query = "SELECT idProdottoAlimentare, denominazione, flagCibo, ingredienti, prezzo, imgProdotto FROM ProdottoAlimentare"
        val dataBevande = ArrayList<ConsumazioniItemsViewModel>()
        val dataCibo = ArrayList<ConsumazioniItemsViewModel>()
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null && obj.size() > 0){
                                for (i in 0 until obj.size()) {
                                    val idProdotto = obj[i].asJsonObject?.get("idProdottoAlimentare")?.asInt
                                    val denominazione = obj[i].asJsonObject?.get("denominazione")?.asString?.trim('"')
                                    val flagCibo = obj[i].asJsonObject?.get("flagCibo")?.asInt
                                    val ingredienti = obj[i].asJsonObject?.get("ingredienti")?.asString?.trim('"')
                                    val prezzo = obj[i].asJsonObject?.get("prezzo")?.asString?.trim('"')
                                    val immagine = obj[i].asJsonObject?.get("imgProdotto")?.asString?.trim('"')
                                    System.out.println(immagine)
                                    if (flagCibo == 1) {
                                        dataCibo.add(ConsumazioniItemsViewModel(idProdotto, denominazione, "€ " + prezzo, ingredienti, immagine))
                                    } else {
                                        dataBevande.add(ConsumazioniItemsViewModel(idProdotto, denominazione, "€ " + prezzo, ingredienti, immagine))
                                    }
                                }
                                adapterBevande = ConsumazioniCustomAdapter(dataBevande)
                                binding.bevandeRecyclerView.adapter = adapterBevande

                                adapterBevande.setOnClickListener(object : ConsumazioniCustomAdapter.OnClickListener{
                                    override fun onClick(position: Int, model: ConsumazioniItemsViewModel) {
                                        mostraDialogoConfermaAcquisto(model.denominazione, model.idProdotto, model.prezzo)
                                    }

                                })

                                adapterCibo = ConsumazioniCustomAdapter(dataCibo)
                                binding.ciboRecyclerView.adapter = adapterCibo

                                adapterCibo.setOnClickListener(object : ConsumazioniCustomAdapter.OnClickListener{
                                    override fun onClick(position: Int, model: ConsumazioniItemsViewModel) {
                                        mostraDialogoConfermaAcquisto(model.denominazione, model.idProdotto, model.prezzo)
                                    }

                                })
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@AcquistoConsumazioniActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    fun mostraDialogoConfermaAcquisto(denominazioneProdotto : String?, idProdotto: Int?, prezzoProdotto : String?){
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Conferma acquisto")
            .setMessage("Vuoi comprare ${denominazioneProdotto}? \n" +
                    "Costo: ${prezzoProdotto}")
            .setPositiveButton("Conferma") {dialog , _ ->
                effettuaQueryAcquistoProdotto(idProdotto)
                dialog.dismiss()

            }
            .setNegativeButton("Annulla", null)
            .create()
        dialog.show()
    }

    fun effettuaQueryAcquistoProdotto(idProdotto : Int?){
        val query = "INSERT INTO AcquistoProdottoAlimentare (idProdottoAcquistato, emailUtente, dataAcquisto) " +
                "VALUES (${idProdotto}, '${filePre.getString("Email", "")}', '${LocalDate.now().toString().trim()}')"
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@AcquistoConsumazioniActivity, "Acquisto effettuato.", Toast.LENGTH_LONG).show()
                    }else{
                        System.out.println(response.message())
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@AcquistoConsumazioniActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }
}