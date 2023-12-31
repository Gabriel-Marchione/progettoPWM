package com.progettopwm.progettopwm.homepage


import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.progettopwm.progettopwm.utils.BottomNavigationManager
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class HomepageActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomepageBinding
    lateinit var navigationManager: BottomNavigationManager
    lateinit var filePre : SharedPreferences

    var dataCorrente = restituisciDataCorrente()
    var dataCorrenteFormattata = restituisciDataCorrenteFormattata(dataCorrente)
    lateinit var dataDaInserireDB : String

    private var listaLettiniPrenotatiAltriUtenti : MutableList<Int> = mutableListOf()
    private var listaLettiniPrenotatiUtenteCorrente : MutableList<Int> = mutableListOf()


    val listaBottoni = listOf(
        R.id.lettino1, R.id.lettino2, R.id.lettino3, R.id.lettino4, R.id.lettino5,
        R.id.lettino6, R.id.lettino7, R.id.lettino8, R.id.lettino9, R.id.lettino10,
        R.id.lettino11, R.id.lettino12, R.id.lettino13, R.id.lettino14, R.id.lettino15,
        R.id.lettino16, R.id.lettino17, R.id.lettino18, R.id.lettino19, R.id.lettino20,
        R.id.lettino21, R.id.lettino22, R.id.lettino23, R.id.lettino24, R.id.lettino25,
        R.id.lettino26, R.id.lettino27, R.id.lettino28, R.id.lettino29, R.id.lettino30
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        val currentDate = Calendar.getInstance() // Ottieni la data corrente
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.homepageMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        filePre = this.getSharedPreferences("Credenziali", MODE_PRIVATE)

        binding.localDateTextView.setText(dataCorrenteFormattata)
        dataDaInserireDB = dataCorrente

        val calendar = Calendar.getInstance()

        // creiamo l'oggetto datepicker e inizializziamo un listener
        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar, currentDate)
        }

        binding.selezionaDataVisualizzazioneLettiniButton.setOnClickListener {
            DatePickerDialog(this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        recuperaLettiniPrenotatiAltriUtenti(dataDaInserireDB)

        binding.aiutoButton.setOnClickListener {
            mostraDialogAiutoPrenotazione()
        }

        listaBottoni.forEachIndexed { index, bottoneId ->
            val bottone = findViewById<Button>(bottoneId)
            bottone.setOnClickListener {
                if (listaLettiniPrenotatiAltriUtenti.contains(index + 1)) {
                    Toast.makeText(this, "Lettino già prenotato", Toast.LENGTH_LONG).show()
                } else if (listaLettiniPrenotatiUtenteCorrente.contains(index + 1)) {
                    Toast.makeText(this, "Lettino già prenotato da te", Toast.LENGTH_LONG).show()
                } else if (listaLettiniPrenotatiUtenteCorrente.size >= 3) {
                    Toast.makeText(this, "Non puoi prenotare più di 3 lettini!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    val dialog = PrenotazioneDialog(this, index+1, dataCorrente, dataCorrenteFormattata, dataDaInserireDB)
                    dialog.show()
                }

            }
        }
    }

    private fun updateLable(calendar: Calendar, currentDate : Calendar) {
        if (calendar >= currentDate) {
            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)

            dataCorrente = formattedDate
            binding.localDateTextView.text = dataCorrente

            val format2 = "yyyy-MM-dd"
            val sdf2 = SimpleDateFormat(format2, Locale.getDefault())
            dataDaInserireDB = sdf2.format(calendar.time)
            listaLettiniPrenotatiUtenteCorrente.clear()
            listaLettiniPrenotatiAltriUtenti.clear()
            resettaColoriBottoni()
            recuperaLettiniPrenotatiAltriUtenti(dataDaInserireDB)
        } else {
            Toast.makeText(this@HomepageActivity, "Seleziona una data valida", Toast.LENGTH_LONG).show()
        }
    }

    //tramite questo metodo mi recupero l'id dei lettini prenotati, e coloro di rosso il bottone con id recuperato-1
    fun recuperaLettiniPrenotatiAltriUtenti(dataInizioPrenotazione : String){
        val query = "SELECT PL.idPrenotazione, PL.idLettinoPrenotato " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato " +
                "AND PL.flagPrenotazione = 1 " +
                "AND '${dataInizioPrenotazione}' BETWEEN PL.dataInizioPrenotazione AND PL.dataFinePrenotazione " +
                "AND U.email != '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null && obj.size() > 0){
                                var idLettino : Int?
                                for (i in 0 until obj.size()){
                                    idLettino = obj[i].asJsonObject?.get("idLettinoPrenotato")?.toString()?.trim('"')?.toInt()
                                    listaLettiniPrenotatiAltriUtenti.add(idLettino!!)
                                    val bottone = findViewById<Button>(listaBottoni[idLettino.minus(1)])
                                    bottone.setBackgroundColor(Color.RED)
                                }
                            }
                            recuperaLettiniPrenotatiDaUtenteCorrente(dataInizioPrenotazione)
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

    fun recuperaLettiniPrenotatiDaUtenteCorrente(dataInizioPrenotazione : String){
        val query = "SELECT PL.idPrenotazione, PL.idLettinoPrenotato " +
                "FROM Utente U, Lettino L, PrenotazioneLettino PL " +
                "WHERE U.email = PL.emailPrenotante AND L.idLettino = PL.idLettinoPrenotato " +
                "AND PL.flagPrenotazione = 1 " +
                "AND '${dataInizioPrenotazione}' BETWEEN PL.dataInizioPrenotazione AND PL.dataFinePrenotazione " +
                "AND U.email = '${filePre.getString("Email", "")}'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null){
                                var idLettino : Int?
                                for (i in 0 until obj.size()){
                                    idLettino = obj[i].asJsonObject?.get("idLettinoPrenotato")?.toString()?.trim('"')?.toInt()
                                    listaBottoni[idLettino?.minus(1)!!]
                                    listaLettiniPrenotatiUtenteCorrente.add(idLettino)
                                    findViewById<Button>(listaBottoni[idLettino.minus(1)]).setBackgroundColor(Color.parseColor("#3BB85E"))
                                }
                            }
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

    /*fun aggiornaFlagLettino(idLettinoBottone: Int){
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
    }*/


    fun mostraDialogAiutoPrenotazione(){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Guida prenotazione")
            //aggiungere tutorial per terminare in anticipo la prenotazione
            .setMessage("La schermata rappresenta una fila di lettini che sono disposti in fila di 5 sulla spiaggia. " +
                    "\nOgni lettino ha un prezzo associato, e la posizione della fila determina il prezzo: più il numero della fila è basso, più il lettino si trova vicino al mare, " +
                    "quindi il prezzo potrebbe essere maggiore. \nPer esempio i lettini 1-2-3-4-5 sono quelli più vicino al mare ed hanno un costo maggiore rispetto a quelli delle altre file." +
                    "\nOgni lettino può trovarsi in uno dei tre stati: libero (blu), occupato da altre persone (rosso) o occupato da te (verde)." +
                    "\nPer terminare una prenotazione, andare sulla cronologia degli acquisti, cliccare sul lettino desiderato e confermare l'operazione.")
            .setPositiveButton("Ho capito!"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    fun restituisciDataCorrente() : String{
        return LocalDate.now().toString().trim()
    }

    fun restituisciDataCorrenteFormattata(dataCorrente : String) : String{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val localDate = LocalDate.parse(dataCorrente) // Effettua il parsing della stringa in LocalDate
        val formattedDate = localDate.format(formatter)

        return formattedDate
    }

    fun resettaColoriBottoni(){
        for (buttonId in listaBottoni) {
            val button = findViewById<Button>(buttonId)
            button.setBackgroundColor(Color.parseColor("#01A4FF")) // Cambia con il tuo colore predefinito
        }
    }

}