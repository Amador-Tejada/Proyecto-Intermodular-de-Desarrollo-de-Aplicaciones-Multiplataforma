package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma

import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // appBarConfiguration: contiene la configuración de la barra de la aplicación y
    // define qué destinos del NavController se consideran 'top level' (no muestran la flecha atrás).
    private lateinit var appBarConfiguration: AppBarConfiguration

    // binding: instancia generada por ViewBinding para acceder a las vistas del layout
    // activity_main.xml (ActivityMainBinding). Usamos view binding en lugar de findViewById.
    private lateinit var binding: ActivityMainBinding

    // onCreate: punto de entrada de la Activity. Aquí inicializamos la IU y el NavController.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos el binding usando el layout inflater de la Activity.
        // Esto prepara las referencias tipadas a las vistas definidas en activity_main.xml.
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Establecemos la vista de contenido de la Activity con la raíz del binding.
        setContentView(binding.root)

        /** Configuramos la toolbar de la App usando la toolbar incluida en el layout
        // (probablemente definida dentro de app_bar_main.xml). Esto permite usar
        // funciones de AppCompatActivity relacionadas con la barra superior.
        */
        setSupportActionBar(binding.appBarMain.toolbar)

        /** Obtenemos referencias a los componentes del DrawerLayout y NavigationView
        // definidos en el layout principal. Se usan para integrar el Navigation Component
        // con el drawer lateral (hamburger menu).
        */
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        /** findNavController busca el NavController asociado al NavHostFragment que
        // debe estar presente en el layout con el id nav_host_fragment_content_main.
        // El NavController gestiona la navegación entre destinos (fragments) definidos
        // en el grafo de navegación (nav_graph.xml).
        */
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        /** Aquí definimos qué destinos se consideran 'top level' para la AppBar.
        // Los destinos incluidos en este set no mostrarán la flecha de 'up', sino
        // el icono de hamburguesa para abrir el DrawerLayout.
        */
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_inventario,
                R.id.nav_clientes
            ), drawerLayout
        )

        /** Conecta la ActionBar (Toolbar) con el NavController y la configuración
        // de la AppBar para que el título y el icono (hamburguesa/flecha) se actualicen
        // automáticamente al navegar entre destinos.
        */
        setupActionBarWithNavController(navController, appBarConfiguration)

        /// Conecta el NavigationView (menu del Drawer) con el NavController para que
        // los elementos del menú realicen la navegación definida en el grafo cuando se seleccionen.
        navView.setupWithNavController(navController)

        /** Nota: si quieres mostrar un FloatingActionButton (FAB) para añadir tareas,
        // lo habitual es definirlo en el layout del fragmento (por ejemplo en el fragmento
        // del calendario) y manejar su visibilidad/acción desde el fragmento o desde aquí
        // escuchando cambios de navegación con navController.addOnDestinationChangedListener(..).
         Dejarlo aquí comentado ayuda a entender dónde integrarlo.
        */
    }

    // onCreateOptionsMenu: aquí podrías inflar un menú de opciones para la barra superior.
    // Actualmente no se infla ningún menú, por eso devuelve true sin modificar el objeto.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Si deseas añadir items de menú (por ejemplo, ajustes), llama a
        // menuInflater.inflate(R.menu.tu_menu, menu)
        // y devuelve true.

        return true
    }

    // onSupportNavigateUp: maneja la pulsación de la flecha 'up' en la ActionBar.
    // Delegamos la acción al NavController para navegar hacia arriba dentro del grafo
    // o bien llamamos al comportamiento por defecto si el NavController no lo maneja.
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}