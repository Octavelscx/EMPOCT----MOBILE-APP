package com.example.empoct

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.UUID

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val rxBleClient = RxBleClient.create(application.applicationContext)
    private val compositeDisposable = CompositeDisposable()

    // -- Observables LiveData pour l'UI --------------------------------------
    private val _discoveredDevicesNames = MutableLiveData<List<String>>()
    val discoveredDevicesNames: LiveData<List<String>> = _discoveredDevicesNames

    private val _connectionState = MutableLiveData<ConnectionState>()
    val connectionState: LiveData<ConnectionState> = _connectionState

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Liste des devices BLE découverts
    val discoveredDevices = mutableListOf<com.polidea.rxandroidble3.RxBleDevice>()

    // Connexion BLE en cours
    private var currentConnection: RxBleConnection? = null

    // Buffer pour concaténer les données arrivant par paquets
    private var buffer = ""

    // -- (A) LiveData avec toutes les lignes brutes (si tu veux toujours les afficher)
    private val _receivedLines = MutableLiveData<List<String>>(emptyList())
    val receivedLines: LiveData<List<String>> = _receivedLines

    // -- (B) On prépare un tableau JSON en mémoire pour stocker (timestamp, ppb)
    private var measurementsJsonArray = JSONArray()

    /**
     * Scan des périphériques BLE à proximité
     */
    fun startScan() {
        val scanDisposable = rxBleClient
            .scanBleDevices(ScanSettings.Builder().build())
            .subscribe(
                { scanResult ->
                    val bleDevice = scanResult.bleDevice
                    val deviceName = bleDevice.name
                    // On filtre pour éviter les noms vides et doublons
                    if (!discoveredDevices.any { it.macAddress == bleDevice.macAddress }
                        && !deviceName.isNullOrBlank()
                    ) {
                        discoveredDevices.add(bleDevice)
                        _discoveredDevicesNames.postValue(discoveredDevices.map { it.name ?: "Inconnu" })
                    }
                },
                { throwable ->
                    Log.e("HomeViewModel", "Erreur pendant le scan BLE", throwable)
                    _toastMessage.postValue("Erreur scan : ${throwable.message}")
                }
            )
        compositeDisposable.add(scanDisposable)
    }

    /**
     * Établir une connexion BLE et souscrire aux notifications
     */
    fun connectToDevice(bleDevice: com.polidea.rxandroidble3.RxBleDevice) {
        val connectionDisposable = bleDevice
            .establishConnection(false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { connection ->
                currentConnection = connection
                _connectionState.postValue(ConnectionState.Connected(bleDevice.name ?: "Inconnu"))

                // Négociation du MTU avant de configurer la notification
                connection.requestMtu(64)
                    .flatMapObservable { mtuSize ->
                        Log.d("HomeViewModel", "MTU négocié à $mtuSize")
                        connection.setupNotification(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"))
                    }
            }
            // On récupère l'Observable<ByteArray> (flux des notifications)
            .flatMap { it }
            .subscribe(
                { bytes ->
                    val dataString = bytes.toString(Charsets.UTF_8)
                    Log.d("HomeViewModel", "Données reçues : $dataString")

                    // On concatène dans le buffer
                    buffer += dataString

                    // Tant qu'on trouve un "\n", on extrait la ligne
                    while (buffer.contains("\n")) {
                        val indexOfNewline = buffer.indexOf("\n")
                        var line = buffer.substring(0, indexOfNewline)
                        buffer = buffer.substring(indexOfNewline + 1)

                        // On "trim" la ligne pour enlever espaces / \r
                        line = line.trim()

                        // Si la ligne est vide, on skip
                        if (line.isBlank()) {
                            continue
                        }

                        if (line == "END") {
                            _toastMessage.postValue("Fin de la réception (END).")
                            writeMeasurementsToFile()
                            measurementsJsonArray = org.json.JSONArray()
                            break
                        } else {
                            // Ajouter dans la LiveData
                            val currentList = _receivedLines.value?.toMutableList() ?: mutableListOf()
                            currentList.add(line)
                            _receivedLines.postValue(currentList)

                            // Parser la ligne JSON
                            parseAndStoreLine(line)
                        }
                    }


                },
                { throwable ->
                    Log.e("HomeViewModel", "Erreur connexion/notification", throwable)
                    _toastMessage.postValue("Erreur : ${throwable.message}")
                }
            )
        compositeDisposable.add(connectionDisposable)
    }

    /**
     * Envoie la commande "<timestamp>_MEASURE\n"
     * par ex. "1675000000000_MEASURE\n"
     */
    fun syncData() {
        currentConnection?.let { connection ->
            val timestamp = System.currentTimeMillis()
            val command = "${timestamp}_MEASURE\n"
            val dataToSend = command.toByteArray(Charsets.UTF_8)

            val disposable = connection
                .writeCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"), dataToSend)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _toastMessage.postValue("Commande envoyée : $command")
                    },
                    { throwable ->
                        Log.e("HomeViewModel", "Erreur envoi commande", throwable)
                        _toastMessage.postValue("Erreur envoi : ${throwable.message}")
                    }
                )
            compositeDisposable.add(disposable)
        } ?: _toastMessage.postValue("Pas de connexion active")
    }

    /**
     * (E) Parse un JSON de la forme :
     * {"timestamp":"2147483667","ppb":-485,"temperature":2113,"humidity":4840}
     * On récupère seulement timestamp et ppb, et on stocke ça dans measurementsJsonArray
     */
    private fun parseAndStoreLine(line: String) {
        try {
            // On parse la ligne comme un JSON
            val jsonObject = JSONObject(line)
            // timestamp peut être une string, on le convertit en long
            val timestampStr = jsonObject.optString("timestamp", "0")
            val ppb = jsonObject.optInt("ppb", 0)

            val timestampLong = timestampStr.toLongOrNull() ?: 0L

            // On reconstruit un mini-objet JSON ne contenant que ces 2 champs
            val minimalObject = JSONObject()
            minimalObject.put("timestamp", timestampLong)
            minimalObject.put("ppb", ppb)

            // On l'ajoute dans notre tableau JSON en mémoire
            measurementsJsonArray.put(minimalObject)

            Log.d("HomeViewModel", "Mesure ajoutée : $minimalObject")
        } catch (e: JSONException) {
            Log.e("HomeViewModel", "Ligne reçue invalide : $line", e)
        }
    }

    /**
     * (C) Écrit le tableau measurementsJsonArray dans un fichier local
     * dans la mémoire interne de l'application (pas besoin de permission).
     */
    private fun writeMeasurementsToFile() {
        try {
            val context = getApplication<Application>().applicationContext
            // Fichier dans : /data/data/<package>/files/measurements.json
            val file = File(context.filesDir, "measurements.json")

            file.writeText(measurementsJsonArray.toString()) // On écrit le tableau JSON au complet

            _toastMessage.postValue("Fichier measurements.json mis à jour !")
            Log.d("HomeViewModel", "Fichier écrit : ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Erreur lors de l'écriture du fichier", e)
            _toastMessage.postValue("Erreur écriture fichier : ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    // État de connexion
    sealed class ConnectionState {
        data class Connected(val deviceName: String) : ConnectionState()
        object Disconnected : ConnectionState()
    }
}
