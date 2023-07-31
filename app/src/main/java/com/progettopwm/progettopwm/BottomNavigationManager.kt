package com.progettopwm.progettopwm

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.example.progettopwm.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.progettopwm.progettopwm.registrazioneUtente.RegistrazioneUtenteActivity

class BottomNavigationManager(private val context: Context, private val bottomNavigationView: BottomNavigationView) {

    init {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            gestioneItem(menuItem)
        }
    }

    private fun gestioneItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homepageMenuItem -> {
                startActivity(RegistrazioneUtenteActivity::class.java)
                true
            }
            R.id.profiloMenuItem -> {
                startActivity(LoginActivity::class.java)
                true
            }
            else -> false
        }
    }

    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }

}