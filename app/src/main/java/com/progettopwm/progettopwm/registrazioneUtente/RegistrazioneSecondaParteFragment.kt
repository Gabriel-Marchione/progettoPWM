package com.progettopwm.progettopwm.registrazioneUtente

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.progettopwm.R
import com.example.progettopwm.databinding.FragmentRegistrazioneSecondaParteBinding

class RegistrazioneSecondaParteFragment : Fragment(R.layout.fragment_registrazione_seconda_parte) {
    lateinit var binding : FragmentRegistrazioneSecondaParteBinding
    var flagOcchioBarrato : Boolean = true //true occhio barrato, false aperto
    private val TAG = "Fragment 2"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrazioneSecondaParteBinding.inflate(inflater)
        val parentManager = parentFragmentManager
        if(arguments!=null) {
            val email = arguments?.getString("email")
            val nome = arguments?.getString("nome")
            val cognome = arguments?.getString("cognome")
            val dataNascita = arguments?.getString("dataNascita")
        }
        if(checkCampi()){
            //query per inserimento dell'utente
        }

        binding.tornaPrimaParte.setOnClickListener{
            val transaction = parentManager.beginTransaction()
            val newFrag = RegistrazionePrimaParteFragment()
            newFrag.arguments = arguments
            transaction.setCustomAnimations(R.anim.exit_to_left, R.anim.enter_from_left)
            transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 2").commit()
        }

        binding.cancellaTelefonoPlainText.setOnClickListener {
            binding.telefonoRegistrazionePlainText.text = null
        }

        binding.cancellaCartaPlainText.setOnClickListener {
            binding.cartaCreditoRegistrazionePlainText.text = null
        }

        binding.mostraNascondiPasswordRegistrazione.setOnClickListener {
            if(flagOcchioBarrato){ //se l'occhio è barrato allora rendo visibile il testo e cambio immagine
                binding.mostraNascondiPasswordRegistrazione.setImageResource(R.drawable.occhio_aperto)
                binding.passwordRegistrazionePlainText.transformationMethod = null
                flagOcchioBarrato = false

            }else{ //altrimenti, se l'occhio non è barrato, lo rendo nascosto e cambio immagine
                binding.mostraNascondiPasswordRegistrazione.setImageResource(R.drawable.occhio_barrato)
                binding.passwordRegistrazionePlainText.transformationMethod = PasswordTransformationMethod.getInstance()
                flagOcchioBarrato = true
            }
        }

        return binding.root
    }

    private fun checkCampi() : Boolean{
        val patternTelefono = Regex("^[0-9]{10}")
        val patternCartaDiCredito = Regex("^[0-9]{16}")
        var check = false

        if(
            binding.telefonoRegistrazionePlainText.text.trim().isNotEmpty() && binding.cartaCreditoRegistrazionePlainText.text.trim().isNotEmpty()
            && binding.passwordRegistrazionePlainText.text.trim().isNotEmpty()
        ) {
            check = true
            if (!binding.telefonoRegistrazionePlainText.text.matches(patternTelefono)) {
                check = false
                Toast.makeText(
                    this.requireContext(),
                    "Inserire un numero di telefono valido",
                    Toast.LENGTH_LONG
                ).show()
            } else if (!binding.cartaCreditoRegistrazionePlainText.text.matches(patternCartaDiCredito)) {
                check = false
                Toast.makeText(
                    this.requireContext(),
                    "Inserire una numero di carta valido",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else
            Toast.makeText(this.requireContext(), "I campi sono vuoti", Toast.LENGTH_LONG).show()
        return check
    }
}