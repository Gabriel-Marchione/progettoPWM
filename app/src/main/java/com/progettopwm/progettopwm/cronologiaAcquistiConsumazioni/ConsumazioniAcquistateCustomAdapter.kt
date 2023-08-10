package com.progettopwm.progettopwm.cronologiaAcquistiConsumazioni

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ConsumazioniAcquistateCardViewBinding
import com.progettopwm.progettopwm.utils.ClientNetwork
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ConsumazioniAcquistateCustomAdapter(private val mList: List<ConsumazioniAcquistateItemsViewModel>) : RecyclerView.Adapter<ConsumazioniAcquistateCustomAdapter.ViewHolder>(){

    class ViewHolder(binding : ConsumazioniAcquistateCardViewBinding) : RecyclerView.ViewHolder(binding.root){
        val numeroTransazione = binding.numeroTransazioneDaInserireTextView
        val dataTransazione = binding.dataTransazioneDaInserireTextView
        val nomeProdotto = binding.nomeProdottoDaInserireTextView
        val immagine = binding.prodottoImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ConsumazioniAcquistateCardViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.numeroTransazione.text = ItemsViewModel.numeroTransazione
        holder.dataTransazione.text = ItemsViewModel.dataTransazione
        holder.nomeProdotto.text = ItemsViewModel.nomeProdotto

        val immagineURL = ItemsViewModel.immagineURL!!
        restituisciImmagineProdotto(immagineURL, holder.immagine)

    }

    private fun restituisciImmagineProdotto(url: String, imageView: ImageView) {
        ClientNetwork.retrofit.getImage(url).enqueue(
            object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.body() != null) {
                        val immagine = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        imageView.setImageBitmap(immagine)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    imageView.setImageResource(R.drawable.consumazioni)
                }
            }
        )
    }

}