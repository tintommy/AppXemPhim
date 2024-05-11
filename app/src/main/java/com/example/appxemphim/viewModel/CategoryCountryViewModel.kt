package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.CategoryCountryApiService
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Country
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryCountryViewModel @Inject constructor(private var sharedPref: SharedPreferences) :ViewModel() {
    private lateinit var categoryCountryApiService: CategoryCountryApiService
    private lateinit var token: String
    private lateinit var username: String


    private val _category = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    val category = _category.asStateFlow()
    private val _country = MutableStateFlow<Resource<List<Country>>>(Resource.Unspecified())
    val country = _country.asStateFlow()
    init {
        initApiService()
    }
    private fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        categoryCountryApiService = retrofit.create(CategoryCountryApiService::class.java)

    }


     fun loadAllCategory(){
        viewModelScope.launch {
            _category.emit(Resource.Loading())

            val response= categoryCountryApiService.getAllCategory()
            if (response.isSuccessful){
                _category.emit(Resource.Success(response.body()!!.data))
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
                _country.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _country.emit(Resource.Error("Lỗi khi load country"))
            }
        }
    }
}