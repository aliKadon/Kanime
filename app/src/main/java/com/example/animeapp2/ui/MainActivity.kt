package com.example.animeapp2.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.animeapp2.R
//import com.example.animeapp2.ui.fragments.FilterDialogFragment.Companion.newInstance
import com.example.animeapp2.databinding.ActivityMainBinding
import com.example.animeapp2.db.AnimeDatabase
//import com.example.animeapp2.db.AnimeDatabase
import com.example.animeapp2.repositories.AnimeRepository
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: AnimeViewModel
    private var pressedTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animeRepository = AnimeRepository(AnimeDatabase(this))
        val viewModelProviderFactory = AnimeViewHolderProviderFactory(application, animeRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(AnimeViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.meowBottom2.add(MeowBottomNavigation.Model(1,R.drawable.ic_home_black_24dp))
        binding.meowBottom2.add(MeowBottomNavigation.Model(2,R.drawable.ic_search_foreground))
        binding.meowBottom2.add(MeowBottomNavigation.Model(3,R.drawable.ic_not_favorite_foreground))

        val navView: BottomNavigationView = binding.navView



        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.animeListFragment2, R.id.animeSearchFragment2,R.id.animeFavoriteFragment
            )
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.meowBottom2.setOnClickMenuListener {
            when(it.id) {
                1 -> navController.navigate(R.id.animeListFragment2)
                2 -> navController.navigate(R.id.animeSearchFragment2)
                3 -> navController.navigate(R.id.animeFavoriteFragment)
            }
        }
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            finish()
        }
        else {
            Snackbar.make(binding.container,"Press back again to exit",Snackbar.LENGTH_LONG).show()
        }
        pressedTime = System.currentTimeMillis()

    }
}