package com.progettopwm.progettopwm.registrazioneUtente

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progettopwm.R
import com.example.progettopwm.databinding.FragmentRegistrazionePrimaParteBinding
import com.example.progettopwm.databinding.ModificaAvatarCustomDialogBinding
import com.example.progettopwm.databinding.SelezionaAvatarCustomDialogBinding
import com.example.progettopwm.databinding.SelezioneAvatarCardViewBinding
import com.progettopwm.progettopwm.profiloUtente.CustomAdapter
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity

class SelezioneAvatarCustomDialog(context: Context) : Dialog(context){
    private lateinit var binding: SelezionaAvatarCustomDialogBinding
    private lateinit var binding2 : SelezioneAvatarCardViewBinding
    private lateinit var adapter: CustomAdapter

    private val listaImmagini: List<Int> = listOf(
        R.drawable.avatar,
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4,
        R.drawable.avatar5,
        R.drawable.avatar6,
        R.drawable.avatar7,
        R.drawable.avatar8
    )

    interface AvatarSelectionListener{
        fun onAvatarSelected(avatarResId: Int)
    }

    private var avatarSelectionListener: AvatarSelectionListener? = null

    fun setAvatarSelectionListener(listener: AvatarSelectionListener) {
        this.avatarSelectionListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SelezionaAvatarCustomDialogBinding.inflate(layoutInflater)
        binding2 = SelezioneAvatarCardViewBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)


        binding.recyclerViewSceltaAvatar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapter = CustomAdapter(listaImmagini)
        binding.recyclerViewSceltaAvatar.adapter = adapter

        //aggiorno l'avatar in base alla posizione dell'immagine cliccata, salvando l'id dell'immagine nel file
        adapter.setOnClickListener(object : CustomAdapter.OnClickListener {
            override fun onClick(position: Int, model: Int) {
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("Conferma")
                    .setMessage("Vuoi scegliere l'Avatar ${position+1}?")
                    .setPositiveButton("Conferma") { dialog, which ->

                        avatarSelectionListener?.onAvatarSelected(listaImmagini[position])
                        dismiss()
                    }
                    .setNegativeButton("Annulla", null)
                    .create()
                alertDialog.show()
            }
        })
    }
}
