package com.example.animeapp2.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.etebarian.meowbottomnavigation.MeowBottomNavigationCell
import com.example.animeapp2.R
import com.example.animeapp2.databinding.ActivityMainDetailBinding
import com.example.animeapp2.db.AnimeDatabase
import com.example.animeapp2.repositories.AnimeRepository
import com.google.gson.Gson


class MainActivityDetail : AppCompatActivity() {

    private lateinit var binding: ActivityMainDetailBinding
    lateinit var viewModel: AnimeDetailViewModel
    private val args : MainActivityDetailArgs by navArgs()
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animeData = args.animeSer
        val gson = Gson()
        val dataString = gson.toJson(animeData)

        val pref : SharedPreferences = this.getSharedPreferences("prefID", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("dataString",dataString)
        editor.commit()

        val animeRepository = AnimeRepository(AnimeDatabase(this))
        val viewModelProviderFactory = AnimeDetailViewHolderProviderFactory(application, animeRepository, animeData)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AnimeDetailViewModel::class.java)

        binding = ActivityMainDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.meowBottom.add(MeowBottomNavigation.Model(1, R.drawable.ic_dashboard_black_24dp))
        binding.meowBottom.add(MeowBottomNavigation.Model(2, R.drawable.ic_character_foreground))

        navController = findNavController(R.id.nav_host_fragment_activity_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.animeDetailFragment, R.id.charectersFragments
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView2.setupWithNavController(navController)


        binding.meowBottom.setOnShowListener {
            when(it.id) {
                1 -> navController.navigate(R.id.animeDetailFragment)
                2 -> navController.navigate(R.id.charectersFragments)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}