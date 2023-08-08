package com.progettopwm.progettopwm.cronologiaAcquistiConsumazioni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progettopwm.databinding.CronologiaLettiniCardViewBinding

class CronologiaAcquistiConsumazioniCustomAdapter(private val mList: List<ItemsViewModelCronologiaAcquistiConsumazioni>) : RecyclerView.Adapter<CronologiaAcquistiConsumazioniCustomAdapter.ViewHolder>(){
    private var onClickListener: OnClickListener? = null

    class ViewHolder(binding : CronologiaLettiniCardViewBinding) : RecyclerView.ViewHolder(binding.root){
        val idLettino = binding.numeroLettinoCronologiaTextView
        val numeroPrenotazione = binding.numeroPrenotazioneCronologiaDaInserire
        val dataInizioPrenotazione = binding.dataInizioPrenotazioneCronologiaDaInserire
        val dataFinePrenotazione = binding.dataFinePrenotazioneCronologiaDaInserire
        val prezzo = binding.prezzoLettinoCronologiaDaInserire
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CronologiaLettiniCardViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.idLettino.text = ItemsViewModel.idLettino
        holder.numeroPrenotazione.text = ItemsViewModel.numeroPrenotazione
        holder.dataInizioPrenotazione.text = ItemsViewModel.dataInizioPrenotazione
        holder.dataFinePrenotazione.text = ItemsViewModel.dataFinePrenotazione
        holder.prezzo.text = ItemsViewModel.prezzo

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, ItemsViewModel)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: ItemsViewModelCronologiaAcquistiConsumazioni)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}