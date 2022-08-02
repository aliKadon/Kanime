package com.example.animeapp2.ui.fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.animeapp2.enums.GenreEnum
import com.example.animeapp2.R
import com.example.animeapp2.databinding.DialogFragmentFilterBinding
import com.example.animeapp2.enums.StatusEnum
import com.example.animeapp2.ui.AnimeViewModel
import com.example.animeapp2.ui.MainActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar


class FilterDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentFilterBinding
    lateinit var viewModel: AnimeViewModel
    var rateNumbre = ""
    var statusName = ""




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner)
        val rootView = inflater.inflate(R.layout.dialog_fragment_filter, container, false)
        dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding = DialogFragmentFilterBinding.bind(view)

        val genreList : MutableList<String> = mutableListOf()
        var genreNumber = ""
        getSeekbarProgress()


        for (status in StatusEnum.values()) {
            addChip(status.name,binding.chipsGroupStatus, true, true)
        }

        for (genre in GenreEnum.values()){
            addChip(genre.name, binding.chipsGroup, false, false)
        }

        binding.chipsGroup.forEach { child ->
            (child as Chip)?.setOnCheckedChangeListener { View, isChecked ->
                child?.let {
                    val name = it.text
                    val genre = GenreEnum.valueOf(name.toString()).genreNumber.toString()

                    if (isChecked){
                        genreList.add(genre)
                        genreNumber = genreList.joinToString(" ")
                    }
                    if (!isChecked){
                        genreList.remove(genre)
                        genreNumber = genreList.joinToString( " ")
                    }
                }

            }
        }

        binding.chipsGroupStatus.forEach { child ->
            (child as Chip)?.setOnCheckedChangeListener { view, isChecked ->
                child?.let {
                    statusName = it.text.toString()

                }

            }
        }
        binding.bvSelect.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("genre",genreNumber)
            bundle.putString("score", rateNumbre)
            bundle.putString("status", statusName)
            bundle.putString("year","${binding.editYear.text}-01-01")


            val fragment = AnimeSearchFragment()
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()?.replace(R.id.SearchFragment,fragment)?.commit()//?.replace(R.id.SearchFragment,fragment)?.commit()
            dismiss()
        }

        binding.bvCancel.setOnClickListener {
            dismiss()
        }
    }



    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    private fun getSeekbarProgress(){
        binding.seekBarRateSeekBar?.setOnSeekBarChangeListener( object :
            SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rateNumbre = binding.seekBarRateSeekBar.progress.toString()
                binding.textViewSeekbarProgress.text = rateNumbre
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                rateNumbre = binding.seekBarRateSeekBar.progress.toString()
                binding.textViewSeekbarProgress.text = rateNumbre
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                rateNumbre = binding.seekBarRateSeekBar.progress.toString()
                binding.textViewSeekbarProgress.text = rateNumbre
            }
        })

    }

    fun addChip(lable : String, view: ChipGroup, isHorizontal : Boolean, isSengleSelection : Boolean) {
        var chip = LayoutInflater.from(context).inflate(R.layout.chips_genre,null) as Chip
        chip.id = View.generateViewId()
        chip.text = lable
        chip.isClickable = true
        chip.isFocusable = true
        view.isSingleSelection = isSengleSelection
        view.isSingleLine = isHorizontal
        view.addView(chip)

    }
}
