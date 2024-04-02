package com.example.appxemphim.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.CategoryCountryApiService
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Country
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryCountryViewModel:ViewModel() {
    private lateinit var categoryCountryApiService: CategoryCountryApiService
    private val _category = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    val category = _category.asStateFlow()
    private val _country = MutableStateFlow<Resource<List<Country>>>(Resource.Unspecified())
    val country = _country.asStateFlow()
    init {
        initApiService()
    }
    private fun initApiService() {
        var token =
            "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJyciIsImlhdCI6MTcxMTcxODQ4MSwiZXhwIjoxNzEyMzIzMjgxfQ.uyKxi9H_47paeT5YUGfobaihsH-f1DbV7nBO9nuiHKVKYpiq0uymBoPeS9V70B17"
        var retrofit = API_Instance.getClient(token)
        categoryCountryApiService = retrofit.create(CategoryCountryApiService::class.java)

    }


     fun loadAllCategory(){
        viewModelScope.launch {
            _category.emit(Resource.Loading())

            val response= categoryCountryApiService.getAllCategory()
            if (response.isSuccessful){
                _category.emit(Resource.Success(response.body()!!))
            }
            else{
                _category.emit(Resource.Error("Lỗi khi load category"))
            }
        }
    }

     fun loadAllCountry(){
        viewModelScope.launch {
            _country.emit(Resource.Loading())

            val response= categoryCountryApiService.getAllCountry()
            if (response.isSuccessful){
                _country.emit(Resource.Success(response.body()!!))
            }
            else{
                _country.emit(Resource.Error("Lỗi khi load country"))
            }
        }
    }
}