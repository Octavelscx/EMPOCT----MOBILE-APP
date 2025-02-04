package com.example.empoct

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.empoct.databinding.FragmentBluetestBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentBluetestBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var devicesAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // On gonfle le layout avec le Binding généré
        _binding = FragmentBluetestBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        devicesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        binding.listViewDevices.adapter = devicesAdapter

        setupObservers()
        setupUI()

        // On lance le scan automatiquement
        checkPermissionsAndScan()
    }

    private fun setupUI() {

        // Lors du clic sur un device dans la liste
        binding.listViewDevices.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = viewModel.discoveredDevices[position]
            Toast.makeText(requireContext(), "Connexion à ${selectedDevice.macAddress}", Toast.LENGTH_SHORT).show()
            viewModel.connectToDevice(selectedDevice)
        }

        // Bouton SYNC
        binding.buttonSync.setOnClickListener {
            viewModel.syncData()
        }

        // Bouton retour1
        binding.retour1.setOnClickListener {
            val secondFragment = Bluetooth() // Remplace par le nom complet si besoin
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .replace(R.id.main, secondFragment)
                .addToBackStack(null)
                .commit()
        }

        // Bouton retour2

    }

    private fun setupObservers() {
        // Observateur sur la liste des noms de devices découverts
        viewModel.discoveredDevicesNames.observe(viewLifecycleOwner) { devices ->
            devicesAdapter.clear()
            devicesAdapter.addAll(devices)
        }

        // Observateur sur l'état de connexion
        viewModel.connectionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeViewModel.ConnectionState.Connected -> {

                    binding.connectedSection.visibility = View.VISIBLE
                    binding.textViewConnected.text = "Connecté à : ${state.deviceName}"
                }
                HomeViewModel.ConnectionState.Disconnected -> {

                    binding.connectedSection.visibility = View.GONE
                }
            }
        }

        // Observateur sur les messages toast
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Observateur sur les lignes reçues : on les affiche dans le textViewResultatMesure
        viewModel.receivedLines.observe(viewLifecycleOwner) { lines ->
            // Joint toutes les lignes reçues avec un saut de ligne
            binding.textViewResultatMesure.text = lines.joinToString(separator = "\n")
        }
    }

    /**
     * Vérifier les permissions et lancer le scan si OK
     */
    private fun checkPermissionsAndScan() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            // Toutes les permissions sont déjà accordées
            viewModel.startScan()
        } else {
            // Demander les permissions manquantes
            requestPermissions(permissionsToRequest.toTypedArray(), 123)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
