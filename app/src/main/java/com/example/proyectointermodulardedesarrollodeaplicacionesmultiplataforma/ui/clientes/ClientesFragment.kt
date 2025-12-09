package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.clientes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.databinding.FragmentClientesBinding


class ClientesFragment : Fragment() {

    private var _binding: FragmentClientesBinding? = null

    /**
     * Esta propiedad solo es válida entre onCreateView y onDestroyView.
     * Proporciona acceso seguro (no nulo) al binding mientras la vista exista.
     */
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /**
         * Si necesitas un ViewModel para este fragmento, descomenta y usa la siguiente línea:
         *
         * val clientesViewModel =
         *     ViewModelProvider(this).get(ClientesViewModel::class.java)
         *
         * Se dejó comentado para evitar advertencias de variable sin usar.
         */

        _binding = FragmentClientesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}