import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progettopwm.R
import com.example.progettopwm.databinding.ModificaAvatarCustomDialogBinding
import com.example.progettopwm.databinding.SelezioneAvatarCardViewBinding
import com.progettopwm.progettopwm.profiloUtente.CustomAdapter
import com.progettopwm.progettopwm.profiloUtente.ProfiloUtenteActivity

class ModificaAvatarCustomDialog(context: Context, ) : Dialog(context) {
    private lateinit var binding: ModificaAvatarCustomDialogBinding
    private lateinit var binding2 : SelezioneAvatarCardViewBinding
    private lateinit var adapter: CustomAdapter
    lateinit var fileAvatar : SharedPreferences

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModificaAvatarCustomDialogBinding.inflate(layoutInflater)
        binding2 = SelezioneAvatarCardViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileAvatar = context.getSharedPreferences("File avatar", AppCompatActivity.MODE_PRIVATE)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapter = CustomAdapter(listaImmagini)
        binding.recyclerView.adapter = adapter

        adapter.setOnClickListener(object : CustomAdapter.OnClickListener {
            override fun onClick(position: Int, model: Int) {
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("Conferma")
                    .setMessage("Vuoi scegliere l'Avatar ${position+1}?")
                    .setPositiveButton("Conferma") { dialog, which ->
                        val editor = fileAvatar.edit()
                        editor.putInt("idImmagineAvatar", listaImmagini[position])
                        editor.apply()

                        val intent = Intent(context, ProfiloUtenteActivity::class.java)
                        context.startActivity(intent)
                    }
                    .setNegativeButton("Annulla", null)
                    .create()
                alertDialog.show()
            }
        })
    }
}