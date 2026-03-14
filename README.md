# Progetto PWM - Sistema di Gestione Servizi Balneari

## 1. Analisi Dettagliata e Professionale
Il progetto **Progetto PWM** è un'applicazione mobile Android sviluppata in **Kotlin** finalizzata alla digitalizzazione e ottimizzazione dei servizi offerti da uno stabilimento balneare. L'architettura segue il pattern **Model-View-Controller (MVC)** con l'utilizzo di **ViewBinding** per una gestione sicura ed efficiente delle componenti della UI.

L'applicazione si distingue per un approccio orientato ai dati, dove la logica di business è strettamente integrata con un backend remoto tramite chiamate API REST. Una particolarità tecnica rilevante è l'utilizzo di un'interfaccia API generica che permette l'invio di query SQL grezze dal client al server, facilitando una prototipazione rapida ma richiedendo attenzione alla sicurezza lato server.

### Tecnologie Core:
- **Linguaggio:** Kotlin
- **Networking:** Retrofit 2 & OkHttp
- **Parsing Dati:** GSON
- **Persistenza Locale:** SharedPreferences per la gestione della sessione utente.
- **UI Components:** Material Design, RecyclerView, Custom Dialogs.

---

## 2. Alberatura del Progetto e Descrizione File

```text
app/src/main/java/com/progettopwm/progettopwm/
├── BenvenutoActivity.kt              # Entry point dell'app, gestisce l'accoglienza.
│
├── autenticazioneUtente/             # Modulo gestione accesso
│   └── LoginActivity.kt              # Gestione login e validazione credenziali.
│
├── registrazioneUtente/              # Modulo creazione nuovi account
│   ├── RegistrazioneUtenteActivity.kt # Controller principale del workflow di registrazione.
│   ├── RegistrazioneP1Fragment.kt     # Primo step: Dati anagrafici.
│   ├── RegistrazioneP2Fragment.kt     # Secondo step: Credenziali e avatar.
│   └── SelezioneAvatarDialog.kt       # Dialog personalizzato per la scelta dell'avatar.
│
├── homepage/                         # Core dell'applicazione
│   ├── HomepageActivity.kt           # Mappa interattiva dei lettini e prenotazioni in tempo reale.
│   └── PrenotazioneDialog.kt         # Gestione della conferma prenotazione lettino.
│
├── acquistoConsumazioni/             # Modulo e-commerce interno
│   ├── AcquistoConsumazioniActivity.kt # Catalogo prodotti (cibo/bevande) con acquisto diretto.
│   ├── ConsumazioniCustomAdapter.kt  # Bridge tra dati prodotti e RecyclerView.
│   └── ConsumazioniItemsViewModel.kt # Modello dati per il singolo prodotto.
│
├── cronologiaAcquistiConsumazioni/   # Gestione storico e stato servizi
│   ├── CronologiaActivity.kt         # Visualizzazione riepilogativa di prenotazioni e acquisti.
│   ├── LettiniCustomAdapter.kt       # Gestione visualizzazione storico lettini.
│   └── ConsumazioniAcquistateAdapter.kt # Gestione visualizzazione storico consumazioni.
│
├── profiloUtente/                    # Area personale
│   ├── ProfiloUtenteActivity.kt      # Visualizzazione e gestione account.
│   ├── ModificaDatiDialog.kt         # Dialog per l'aggiornamento dati personali.
│   └── ModificaPasswordDialog.kt     # Dialog per il cambio password.
│
└── utils/                            # Classi di utilità e configurazione
    ├── ClientNetwork.kt              # Configurazione Singleton di Retrofit.
    ├── UserAPI.kt                    # Definizione degli endpoint (Select, Insert, Update, Remove).
    └── BottomNavigationManager.kt    # Centralizzazione della logica di navigazione tra moduli.

### Risorse (app/src/main/res/)
- **layout/**: File XML che definiscono la struttura visiva di ogni Activity, Fragment e Custom Dialog (es. `activity_homepage.xml`, `prenotazione_lettino_custom_dialog.xml`).
- **drawable/**: Asset grafici, icone vettoriali (SVG/XML) e sfondi personalizzati (es. icone menu, loghi, avatar).
- **values/**: Definizioni centralizzate di colori (`colors.xml`), stringhe multilingua (`strings.xml`) e stili grafici (`themes.xml`).
- **menu/**: Configurazione della Bottom Navigation Bar (`menu_layout.xml`).
```

---

## 3. Flusso dei Dati
Il flusso informativo dell'applicazione è di tipo **Client-Server Reattivo**:

1.  **Richiesta (Request):** Quando l'utente compie un'azione (es. prenota un lettino o acquista un caffè), l'Activity di riferimento costruisce una stringa di query SQL (SELECT/INSERT/UPDATE).
2.  **Trasmissione:** Tramite il `ClientNetwork` e l'interfaccia `UserAPI`, la query viene inviata come campo di un modulo POST all'endpoint dedicato (es. `postInsert/`).
3.  **Elaborazione Backend:** Il server riceve la query, la esegue sul database e restituisce un oggetto JSON contenente un `queryset` (array di risultati) o un flag di successo.
4.  **Ricezione (Response):** Retrofit deserializza il JSON in un `JsonObject`. L'Activity, tramite un `Callback`, riceve il risultato.
5.  **Aggiornamento UI:** I dati vengono smistati agli Adapter (per le liste) o direttamente ai componenti grafici tramite ViewBinding, aggiornando lo stato visuale dell'app (es. il lettino appena prenotato diventa verde).

---

## 4. Interfaccia Utente e Interaction Design
L'utente interagisce con l'applicazione attraverso un'interfaccia intuitiva e color-coded:

- **Autenticazione:** Un workflow guidato permette l'accesso o la creazione di un profilo con avatar personalizzato.
- **Prenotazione Spiaggia (Homepage):** Una griglia interattiva rappresenta i lettini. I colori indicano lo stato:
    - **Blu:** Disponibile.
    - **Rosso:** Occupato da altri utenti.
    - **Verde:** Prenotato dall'utente corrente.
- **Navigazione:** Una `BottomNavigationView` permette lo switch rapido tra i quattro pilastri dell'app (Home, Shop, Cronologia, Profilo).
- **Feedback Visivo:** L'uso di `Toast` e `AlertDialog` garantisce che l'utente riceva sempre conferma delle operazioni effettuate (es. "Acquisto effettuato", "Credenziali errate").
- **Personalizzazione:** Tramite i Dialog nel profilo, l'utente può modificare in tempo reale i propri dati, mantenendo un senso di controllo totale sul proprio account.

---
*Documentazione generata analiticamente per il progetto PWM.*
