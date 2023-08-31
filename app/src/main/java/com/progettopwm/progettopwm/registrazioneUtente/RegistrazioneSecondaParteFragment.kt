package com.progettopwm.progettopwm.registrazioneUtente

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.progettopwm.R
import com.example.progettopwm.databinding.FragmentRegistrazionePrimaParteBinding
import com.example.progettopwm.databinding.FragmentRegistrazioneSecondaParteBinding
import com.google.gson.JsonObject
import com.progettopwm.progettopwm.utils.ClientNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrazioneSecondaParteFragment : Fragment(R.layout.fragment_registrazione_seconda_parte) {
    lateinit var binding : FragmentRegistrazioneSecondaParteBinding
    lateinit var binding2 : FragmentRegistrazionePrimaParteBinding
    var flagOcchioBarrato : Boolean = true //true occhio barrato, false aperto
    private val TAG = "Fragment 2"
    lateinit var email : String
    lateinit var nome : String
    lateinit var cognome : String
    lateinit var dataNascita : String
    var avatar : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrazioneSecondaParteBinding.inflate(inflater)
        binding2 = FragmentRegistrazionePrimaParteBinding.inflate(inflater)
        val parentManager = parentFragmentManager
        if(arguments!=null) {
            System.out.println("fragment 2 " + arguments)
            avatar = arguments?.getInt("idImmagineAvatarRegistrazione")!!
            email = arguments?.getString("email").toString().trim()
            nome = arguments?.getString("nome").toString().trim()
            cognome = arguments?.getString("cognome").toString().trim()
            dataNascita = arguments?.getString("dataNascitaDB").toString().trim()

            binding.telefonoRegistrazionePlainText.setText(arguments?.getString("telefono"))
            binding.cartaCreditoRegistrazionePlainText.setText(arguments?.getString("cartaCredito"))
            binding.passwordRegistrazionePlainText.setText(arguments?.getString("password"))

            binding.confermaRegistrazione.setOnClickListener {
                if (checkCampi()) {
                    effettuaQuery()
                }
            }
        }else {
            Toast.makeText(context, "Problemi con i fragment", Toast.LENGTH_LONG).show()
        }


        binding.tornaPrimaParte.setOnClickListener{
            val transaction = parentManager.beginTransaction()
            val newFrag = RegistrazionePrimaParteFragment()
            newFrag.arguments = addArgs(arguments)
            transaction.setCustomAnimations(R.anim.exit_to_left, R.anim.enter_from_left)
            transaction.replace(R.id.fragmentContainerView, newFrag, "Fragment 1").commit()
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

    private fun effettuaQuery(){
        val query = "INSERT INTO Utente (email, nome, cognome, dataNascita, telefono, cartaCredito, password) " +
                "VALUES ('${email}', '${nome}', '${cognome}', '${dataNascita}', '${binding.telefonoRegistrazionePlainText.text.toString().trim()}', " +
                "'${binding.cartaCreditoRegistrazionePlainText.text.toString().trim()}', '${binding.passwordRegistrazionePlainText.text.toString().trim()}' )"
        ClientNetwork.retrofit.insert(query).enqueue(
            object : Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful){
                        val intent = Intent(this@RegistrazioneSecondaParteFragment.requireContext(), RegistrazioneCompletataActivity::class.java)
                        intent.putExtra("Email", email)
                        intent.putExtra("Password", binding.passwordRegistrazionePlainText.text.toString().trim())
                        intent.putExtra("idImmagineAvatarRegistrazione", avatar)
                        startActivity(intent)
                    }else{
                        Toast.makeText(
                            this@RegistrazioneSecondaParteFragment.requireContext(),
                            "Errore del Database",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@RegistrazioneSecondaParteFragment.requireContext(),
                        "Errore del Database o di connessione, riprova ad effettuare la registrazione",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun addArgs(arguments : Bundle?) : Bundle? {
        arguments?.putString("telefono", binding.telefonoRegistrazionePlainText.text.trim().toString())
        arguments?.putString("cartaCredito", binding.cartaCreditoRegistrazionePlainText.text.trim().toString())
        arguments?.putString("password", binding.passwordRegistrazionePlainText.text.trim().toString())
        return arguments
    }
}