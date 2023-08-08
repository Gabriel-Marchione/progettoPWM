package com.progettopwm.progettopwm.acquistoConsumazioni

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.progettopwm.R
import com.example.progettopwm.databinding.AcquistoConsumazioniCardViewBinding
import com.progettopwm.progettopwm.utils.ClientNetwork
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class AcquistoConsumazioniCustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<AcquistoConsumazioniCustomAdapter.ViewHolder>(){
    private var onClickListener: OnClickListener? = null

    class ViewHolder(binding : AcquistoConsumazioniCardViewBinding) : RecyclerView.ViewHolder(binding.root){
        val denominazione = binding.denominazioneDaInserireTextView
        val prezzo = binding.prezzoDaInserireTextView
        val ingredienti = binding.ingredientiTextView
        val immagine = binding.immagineCiboBevandeImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AcquistoConsumazioniCardViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.denominazione.text = ItemsViewModel.denominazione
        holder.prezzo.text = ItemsViewModel.prezzo
        holder.ingredienti.text = ItemsViewModel.ingredienti

        val immagineURL = ItemsViewModel.immagineURL!!
        restituisciImmagineProdotto(immagineURL, holder.immagine)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, ItemsViewModel)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: ItemsViewModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
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