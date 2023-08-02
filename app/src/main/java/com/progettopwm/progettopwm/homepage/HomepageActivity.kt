package com.progettopwm.progettopwm.homepage

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

class HomepageActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomepageBinding
    lateinit var navigationManager: BottomNavigationManager
    //lateinit var fileAvatar : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        val listaBottoni = listOf(binding.lettino1, binding.lettino2, binding.lettino3, binding.lettino4, binding.lettino5,
                    binding.lettino6, binding.lettino7, binding.lettino8, binding.lettino9, binding.lettino10,
                    binding.lettino11, binding.lettino12, binding.lettino13, binding.lettino14, binding.lettino15,
                    binding.lettino16, binding.lettino17, binding.lettino18, binding.lettino19, binding.lettino20,
                    binding.lettino21, binding.lettino22, binding.lettino23, binding.lettino24, binding.lettino25,
                    binding.lettino26, binding.lettino27, binding.lettino28, binding.lettino29, binding.lettino30
            )

        // Creiamo la navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.selectedItemId = R.id.homepageMenuItem
        navigationManager = BottomNavigationManager(this, bottomNavigationView)

        effettuaQuery(listaBottoni)

        listaBottoni.forEachIndexed { index, bottone ->
            bottone.setOnClickListener {
                //bottone.setBackgroundColor(Color.RED)
                val contenutoBottone = bottone.text.toString().trim()

            }
        }
    }

    fun effettuaQuery(listaBottoni : List<Button>){
        val query = "SELECT idLettino, flagPrenotazione FROM Lettino WHERE flagPrenotazione = '1'"
        ClientNetwork.retrofit.select(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        if(response.body() != null){
                            System.out.println(response.body())
                            val obj = response.body()?.getAsJsonArray("queryset")
                            if(obj != null){
                                var numero : Int? = 0
                                for (i in 0 until obj.size()){
                                    numero = obj[i].asJsonObject?.get("idLettino")?.toString()?.trim('"')?.toInt()
                                    System.out.println(numero)
                                    System.out.println(listaBottoni[numero!!])
                                    listaBottoni[numero?.minus(1)!!].setBackgroundColor(Color.RED)
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
                    System.out.println(t.message)
                }

            }
        )
    }


}