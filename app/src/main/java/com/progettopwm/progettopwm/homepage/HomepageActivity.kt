package com.progettopwm.progettopwm.homepage


import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ActivityHomepageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.Utils.BottomNavigationManager
import com.progettopwm.progettopwm.Utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class HomepageActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomepageBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences

    private var listaLettiniPrenotatiAltriUtenti : MutableList<Int> = mutableListOf()
    private var listaLettiniPrenotatiUtenteCorrente : MutableList<Int> = mutableListOf()
    private val dataCorrente = LocalDate.now()
    //lateinit var fileAvatar : SharedPreferences

    val listaBottoni = listOf(
        R.id.lettino1, R.id.lettino2, R.id.lettino3, R.id.lettino4, R.id.lettino5,
        R.id.lettino6, R.id.lettino7, R.id.lettino8, R.id.lettino9, R.id.lettino10,
        R.id.lettino11, R.id.lettino12, R.id.lettino13, R.id.lettino14, R.id.lettino15,
        R.id.lettino16, R.id.lettino17, R.id.lettino18, R.id.lettino19, R.id.lettino20,
        R.id.lettino21, R.id.lettino22, R.id.lettino23, R.id.lettino24, R.id.lettino25,
        R.id.lettino26, R.id.lettino27, R.id.lettino28, R.id.lettino29, R.id.lettino30
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.homepageMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        //creare un calendario dove la data inizialmente è impostata ad oggi
        recuperaLettiniPrenotatiAltriUtenti()

        binding.aiutoButton.setOnClickListener {
            mostraDialogAiutoPrenotazione()
        }

        listaBottoni.forEachIndexed { index, bottoneId ->
            val bottone = findViewById<Button>(bottoneId)
            bottone.setOnClickListener {
                if(listaLettiniPrenotatiAltriUtenti.contains(index+1)){
                    Toast.makeText(this, "Lettino già prenotato", Toast.LENGTH_LONG).show()
                }else if (listaLettiniPrenotatiUtenteCorrente.contains(index+1)){
                    mostraDialog()
                }else if(listaLettiniPrenotatiUtenteCorrente.size >= 3){
                    Toast.makeText(this, "Non puoi prenotare più di 3 lettini!", Toast.LENGTH_LONG).show()
                }else{
                    prenotaLettino(index+1)
                    listaLettiniPrenotatiUtenteCorrente.add(index+1)
                    System.out.println("lettini miei" + listaLettiniPrenotatiUtenteCorrente)
                }

            }
        }


    }

    //da mettere nella data di oggi

    //tramite questo metodo mi recupero l'id dei lettini prenoati, e coloro di rosso il bottone con id recuperato-1
    fun recuperaLettiniPrenotatiAltriUtenti(){
        val query = "SELECT L.idLettino " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato " +
                "AND L.flagPrenotazione = 1 AND PL.dataInizioPrenotazione = '${dataCorrente}' " +
                "AND U.email != '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null && obj.size() > 0){
                                var idLettino : Int? = 0
                                for (i in 0 until obj.size()){
                                    idLettino = obj[i].asJsonObject?.get("idLettino")?.toString()?.trim('"')?.toInt()
                                    listaBottoni[idLettino?.minus(1)!!]
                                    listaLettiniPrenotatiAltriUtenti.add(idLettino)
                                    findViewById<Button>(listaBottoni[idLettino.minus(1)]).setBackgroundColor(Color.RED)
                                }
                            }
                            recuperaLettiniPrenotatiDaUtenteCorrente()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@HomepageActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    fun recuperaLettiniPrenotatiDaUtenteCorrente(){
        val query = "SELECT L.idLettino " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato " +
                "AND L.flagPrenotazione = 1 AND PL.dataInizioPrenotazione = '${dataCorrente}' " +
                "AND U.email = '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null){
                                var idLettino : Int? = 0
                                for (i in 0 until obj.size()){
                                    idLettino = obj[i].asJsonObject?.get("idLettino")?.toString()?.trim('"')?.toInt()
                                    listaBottoni[idLettino?.minus(1)!!]
                                    listaLettiniPrenotatiUtenteCorrente.add(idLettino)
                                    findViewById<Button>(listaBottoni[idLettino.minus(1)]).setBackgroundColor(Color.parseColor("#3BB85E"))
                                }
                            }
                            System.out.println("prenotati query" + listaLettiniPrenotatiUtenteCorrente)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@HomepageActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    fun prenotaLettino(idLettinoBottone : Int){
        val query = "INSERT INTO PrenotazioneLettino(idLettinoPrenotato, emailPrenotante, dataInizioPrenotazione, dataFinePrenotazione) " +
                "VALUES ('${idLettinoBottone}', '${filePre.getString("Email", "")}', '${dataCorrente}', '${dataCorrente}')"
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        aggiornaFlagLettino(idLettinoBottone)
                        val intent = Intent(this@HomepageActivity, HomepageActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@HomepageActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    fun aggiornaFlagLettino(idLettinoBottone: Int){
        val query = "UPDATE Lettino SET flagPrenotazione = 1 WHERE idLettino = $idLettinoBottone"
        ClientNetwork.retrofit.update(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Toast.makeText(this@HomepageActivity,"Lettino $idLettinoBottone prenotato", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@HomepageActivity,
                        "Errore del Database o assenza di connessione",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    fun mostraDialog(){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Annulla prenotazione")
            .setMessage("Annullare la prenotazione?")
            .setPositiveButton("Conferma") {dialog , _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Annulla", null)
            .create()
        dialog.show()
    }

    fun mostraDialogAiutoPrenotazione(){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Guida prenotazione")
            //aggiungere tutorial per terminare in anticipo la prenotazione
            .setMessage("La schermata rappresenta una fila di lettini che sono disposti in fila di 5 sulla spiaggia. " +
                    "\nOgni lettino ha un prezzo associato, e la posizione della fila determina il prezzo: più il numero della fila è basso, più il lettino si trova vicino al mare, " +
                    "quindi il prezzo potrebbe essere maggiore. \nPer esempio i lettini 1-2-3-4-5 sono quelli più vicino al mare ed hanno un costo maggiore rispetto a quelli delle altre file." +
                    "\nOgni lettino può trovarsi in uno dei tre stati: libero (blu), occupato da altre persone (rosso) o occupato da te (verde).")
            .setPositiveButton("Ho capito!"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

}