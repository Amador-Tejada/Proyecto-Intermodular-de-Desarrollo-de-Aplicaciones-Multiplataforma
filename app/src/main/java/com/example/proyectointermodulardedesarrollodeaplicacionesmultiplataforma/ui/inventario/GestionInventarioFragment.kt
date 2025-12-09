package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.inventario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.databinding.FragmentGestionInventarioBinding


class GestionInventarioFragment : Fragment() {

    private var _binding: FragmentGestionInventarioBinding? = null

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
         * Si necesitas el ViewModel para mantener estado o acceder a datos,
         * descomenta la siguiente línea y usa `gestionInventarioViewModel` según convenga:
         *
         * val gestionInventarioViewModel =
         *     ViewModelProvider(this).get(GestionInventarioModel::class.java)
         */

        /**
         * Inflamos el layout usando el binding generado y obtenemos la raíz
         * (root) que será retornada como la vista del fragmento.
         */
        _binding = FragmentGestionInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /**
         * Liberamos la referencia al binding para evitar fugas de memoria.
         * Después de esta llamada `binding` ya no es válido y no debe usarse.
         */
        _binding = null
    }
}