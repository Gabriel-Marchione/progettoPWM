package com.progettopwm.progettopwm.profiloUtente

import com.example.progettopwm.databinding.SelezioneAvatarCardViewBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<Int>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    class ViewHolder(binding : SelezioneAvatarCardViewBinding) : RecyclerView.ViewHolder(binding.root){
        val testoAvatar = binding.avatarTextView
        val immagine = binding.immagineAvatarImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SelezioneAvatarCardViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.testoAvatar.text = "Avatar ${position+1}"
        holder.immagine.setImageResource(ItemsViewModel)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, ItemsViewModel)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}