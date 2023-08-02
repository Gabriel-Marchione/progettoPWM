package com.progettopwm.progettopwm.registrazioneUtente

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.progettopwm.R
import com.example.progettopwm.databinding.FragmentRegistrazionePrimaParteBinding
import com.progettopwm.progettopwm.autenticazioneUtente.LoginActivity
import com.progettopwm.progettopwm.profiloUtente.ModificaAvatarCustomDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RegistrazionePrimaParteFragment : Fragment(R.layout.fragment_registrazione_prima_parte),
    SelezioneAvatarCustomDialog.AvatarSelectionListener {

    lateinit var binding: FragmentRegistrazionePrimaParteBinding
    lateinit var dataDaInserireDB : String
    lateinit var fileAvatarRegistrazione : SharedPreferences
    private val TAG = "Fragment 1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrazionePrimaParteBinding.inflate(inflater)
        val parentManager = parentFragmentManager

        fileAvatarRegistrazione = context?.getSharedPreferences("File avatar registrazione", AppCompatActivity.MODE_PRIVATE)!!

        if(arguments != null){
            binding.selezioneAvatarRegistrazioneImageView.setImageResource(fileAvatarRegistrazione.getInt("idImmagineAvatarRegistrazione", R.drawable.avatar))
            binding.emailRegistrazionePlainText.setText(arguments?.getString("email")?.trim())
            binding.nomeRegistrazionePlainText.setText(arguments?.getString("nome")?.trim())
            binding.cognomeRegistrazionePlainText.setText(arguments?.getString("cognome")?.trim())
            binding.dataNascitaPlainText.setText(arguments?.getString("dataNascita")?.trim())
            dataDaInserireDB = arguments?.getString("dataNascitaDB").toString().trim()
        }
        binding.continuaRegistrazioneButton.setOnClickListener {
            if (checkCampi()) {
                if (!fragmentExists(parentManager, "Fragment 2")) {
                    val transaction = parentManager.beginTransaction()
                    val newFrag = RegistrazioneSecondaParteFragment()
                    newFrag.arguments = addArgs()
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 2").commit()
                } else {
                    val frag2 = parentManager.findFragmentByTag("Fragment 2")
                    if (frag2 != null) {
                        val transaction = parentManager.beginTransaction()
                        val newFrag = RegistrazioneSecondaParteFragment()
                        newFrag.arguments = addArgs()
                        transaction.setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_right
                        )
                        transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 2")
                            .commit()
                    }
                }
            }
        }
        binding.dataNascitaPlainText.isClickable = false
        binding.dataNascitaPlainText.isFocusable = false
        // inizializziamo un oggetto di tipo calendar da utilizzare per il date picker
        val calendar = Calendar.getInstance()

        // creiamo l'oggetto datepicker e inizializziamo un listener
        val datePicker = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(calendar)
        }
        binding.selezionaDataNascita.setOnClickListener {
            DatePickerDialog(this.requireContext(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.selezioneAvatarRegistrazioneImageView.setOnClickListener {
            val dialog = SelezioneAvatarCustomDialog(requireContext())
            dialog.setAvatarSelectionListener(this) // Imposta il listener su questo frammento
            dialog.show()
        }

        binding.sceltaAvatarTextView.setOnClickListener {
            val dialog = SelezioneAvatarCustomDialog(requireContext())
            dialog.setAvatarSelectionListener(this) // Imposta il listener su questo frammento
            dialog.show()
        }

        binding.cancellaEmail.setOnClickListener {
            binding.emailRegistrazionePlainText.text = null
        }

        binding.cancellaNome.setOnClickListener {
            binding.nomeRegistrazionePlainText.text = null
        }

        binding.cancellaCognome.setOnClickListener {
            binding.cognomeRegistrazionePlainText.text = null
        }

        binding.cancellaData.setOnClickListener {
            binding.dataNascitaPlainText.text = null
            dataDaInserireDB = ""
        }

        binding.vaiALoginRegistrazioneFragmentTextView.setOnClickListener {
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun updateLable(calendar: Calendar) {
        val format = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val formattedDate = sdf.format(calendar.time)
        binding.dataNascitaPlainText.setText(formattedDate)

        val format2 = "yyyy-MM-dd"
        val sdf2 = SimpleDateFormat(format2, Locale.getDefault())
        dataDaInserireDB = sdf2.format(calendar.time)
    }

    private fun checkCampi() : Boolean{
        val patterNomeCognomeEmail = Regex("^[0-9]+")
        var check = false

        if(
            binding.emailRegistrazionePlainText.text.trim().isNotEmpty() && binding.nomeRegistrazionePlainText.text.trim().isNotEmpty()
            && binding.cognomeRegistrazionePlainText.text.trim().isNotEmpty() && binding.dataNascitaPlainText.text.trim().isNotEmpty()
        ){
            check = true
            if(binding.emailRegistrazionePlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this.requireContext(), "Inserire una email valida", Toast.LENGTH_LONG).show()
            }else if(binding.nomeRegistrazionePlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this.requireContext(), "Inserire un nome valido", Toast.LENGTH_LONG).show()
            }
            else if(binding.cognomeRegistrazionePlainText.text.matches(patterNomeCognomeEmail)){
                check = false
                Toast.makeText(this.requireContext(), "Inserire un cognome valido", Toast.LENGTH_LONG).show()
            }
        }else
            Toast.makeText(this.requireContext(), "I campi sono vuoti", Toast.LENGTH_LONG).show()
        return check
    }

    private fun addArgs() : Bundle{
        val message = Bundle()
        message.putInt("idImmagineAvatarRegistrazione", fileAvatarRegistrazione.getInt("idImmagineAvatarRegistrazione", R.drawable.avatar))
        message.putString("email", binding.emailRegistrazionePlainText.text.toString().trim())
        message.putString("nome", binding.nomeRegistrazionePlainText.text.toString().trim())
        message.putString("cognome", binding.cognomeRegistrazionePlainText.text.toString().trim())
        message.putString("dataNascita", binding.dataNascitaPlainText.text.toString().trim())
        message.putString("dataNascitaDB", dataDaInserireDB.trim())
        return message
    }

    private fun fragmentExists(parentManager: FragmentManager, tag: String): Boolean {
        val fragment = parentManager.findFragmentByTag(tag)
        return fragment != null
    }
    override fun onAvatarSelected(avatarResId: Int) {
        // Aggiorna l'immagine nel frammento con l'avatar selezionato
        binding.selezioneAvatarRegistrazioneImageView.setImageResource(avatarResId)
        val editor = fileAvatarRegistrazione.edit()
        editor.putInt("idImmagineAvatarRegistrazione", avatarResId)
        editor.apply()
    }
}