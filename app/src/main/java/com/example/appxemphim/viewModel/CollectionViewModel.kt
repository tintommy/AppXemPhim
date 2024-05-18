package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.CollectionApiService
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.model.Collection
import com.example.appxemphim.request.Status
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(private var sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var collectionService: CollectionApiService
    private lateinit var token: String
    private lateinit var username: String

    private val _luuPhim = MutableStateFlow<Resource<Collection>>(Resource.Unspecified())
    val luuPhim = _luuPhim.asStateFlow()

    private val _xoaPhim = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val xoaPhim = _xoaPhim.asStateFlow()

    private val _phimDaLuu = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val phimDaLuu = _phimDaLuu.asStateFlow()
    private val _tatCaPhim = MutableStateFlow<Resource<List<Collection>>>(Resource.Unspecified())
    val tatCaPhim = _tatCaPhim.asStateFlow()

    init {
        initApiService()
    }

    private fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        collectionService = retrofit.create(CollectionApiService::class.java)
    }

    fun luuPhim(movieId: Int,ngay:String) {
        viewModelScope.launch {
            val collection = Collection(0, "", movieId, "", ngay, username)

            _luuPhim.emit(Resource.Loading())
            val response = collectionService.saveMovieCollection(collection);

            if (response.isSuccessful) {
                _luuPhim.emit(Resource.Success(response.body()!!.data))
            } else {
                _luuPhim.emit(Resource.Error("Có lỗi xảy ra khi lưu phim"))
            }
        }

    }

    fun xoaPhim(movieId: Int) {
        viewModelScope.launch {


            _xoaPhim.emit(Resource.Loading())
            val response = collectionService.deleteCollection(movieId, username);

            if (response.isSuccessful) {
                _xoaPhim.emit(Resource.Success(response.body()!!.data))
            } else {
                _xoaPhim.emit(Resource.Error("Có lỗi xảy ra khi xoá phim"))
            }
        }

    }


    fun kiemTraPhimDaLuu(movieId: Int) {
        viewModelScope.launch {
            val response = collectionService.checkExistCollection(movieId, username)
            if (response.isSuccessful) {
                _phimDaLuu.emit(Resource.Success(response.body()!!.data))
            } else {
                _phimDaLuu.emit(Resource.Error("Có lỗi xảy ra "))
            }
        }
    }

    fun layTatCaPhimDaLuu() {
        viewModelScope.launch {
            _tatCaPhim.emit(Resource.Loading())
            val response = collectionService.getAllCollection(username)
            if (response.isSuccessful) {
                _tatCaPhim.emit(Resource.Success(response.body()!!.data))
            } else {
                _tatCaPhim.emit(Resource.Error("Có lỗi xảy ra "))
            }
        }
    }
}