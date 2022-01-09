package com.example.t04

import android.graphics.Movie
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.t04.databinding.FragmentDetailBinding
import java.text.DateFormat
import java.text.SimpleDateFormat


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val v = binding.root
        val viewModel:MovieViewModel by activityViewModels()
        val day:SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val strDate: String = day.format(viewModel.getMovie().release_date)

        if(viewModel.getMovie().popularity == 1F){
            binding.button.setText("UNLIKE")
        }
        else{
            binding.button.setText("LIKE")
        }
        binding.button.setOnClickListener {
            if(binding.button.getText().equals("LIKE")){
                viewModel.getMovie().popularity = 1F
                binding.button.setText("UNLIKE")
            }
            else{
                viewModel.getMovie().popularity = 0F
                binding.button.setText("LIKE")
            }
        }


        binding.Title.setText(viewModel.getMovie().title)
        binding.Date.setText(strDate)
        Glide.with(this@DetailFragment).load(resources.getString(R.string.picture_base_url)+viewModel.getMovie().poster_path).apply( RequestOptions().override(128, 128)).into(binding.imageView)

        binding.Overview.setText(viewModel.getMovie().overview)


        return v
    }


}