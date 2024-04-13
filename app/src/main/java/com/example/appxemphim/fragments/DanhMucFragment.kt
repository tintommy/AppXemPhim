package com.example.appxemphim.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appxemphim.R
import com.example.appxemphim.activities.MainActivity
import com.example.appxemphim.adapters.CategoryAdapter
import com.example.appxemphim.adapters.CountryAdapter
import com.example.appxemphim.databinding.DanhMucItemLayoutBinding
import com.example.appxemphim.databinding.FragmentDanhMucBinding
import com.example.appxemphim.util.Resource
import com.example.appxemphim.viewModel.CategoryCountryViewModel
import com.example.appxemphim.viewModel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DanhMucFragment : Fragment() {
    private lateinit var binding: FragmentDanhMucBinding
    private val categoryCountryViewModel by viewModels<CategoryCountryViewModel>()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var countryAdapter: CountryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDanhMucBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        categoryCountryViewModel.loadAllCategory()
        categoryCountryViewModel.loadAllCountry()


        lifecycleScope.launchWhenStarted {

            categoryCountryViewModel.category.collectLatest {

                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        categoryAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }

            }

        }

        lifecycleScope.launchWhenStarted {

            categoryCountryViewModel.country.collectLatest {

                when (it) {

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        countryAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }

            }

        }

    }

    private fun initAdapter(){
        categoryAdapter= CategoryAdapter()
        binding.rvTheLoai.adapter=categoryAdapter
        binding.rvTheLoai.layoutManager=GridLayoutManager(requireContext(),2)
        categoryAdapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener{
            override fun onItemClick(categoryId: Int,categoryName:String) {
                var b: Bundle = Bundle()
                b.putInt("categoryId", categoryId)
                b.putString("categoryName", categoryName)
                val pageFragment = PageFragment()
                pageFragment.arguments = b
                (activity as MainActivity).replaceFragment(pageFragment)
            }
        })



        countryAdapter=CountryAdapter()
        binding.rvQuocGia.adapter=countryAdapter
        binding.rvQuocGia.layoutManager=GridLayoutManager(requireContext(),2)
    }

}