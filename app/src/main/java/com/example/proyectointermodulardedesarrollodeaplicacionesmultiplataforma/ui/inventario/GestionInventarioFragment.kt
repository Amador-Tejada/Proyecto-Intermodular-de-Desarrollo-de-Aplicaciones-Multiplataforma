package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.inventario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.databinding.FragmentGestionInventarioBinding

class GestionInventarioFragment : Fragment() {

    private var _binding: FragmentGestionInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GestionInventarioModel::class.java)

        _binding = FragmentGestionInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}