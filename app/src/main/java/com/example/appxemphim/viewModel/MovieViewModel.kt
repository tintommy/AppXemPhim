package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.api.apiService.UserApiService
import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var movieService: MovieApiService
    private lateinit var token: String
    private lateinit var username: String

    private val _phimLe = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimLe = _phimLe.asStateFlow()
    private val _phimBo = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimBo = _phimBo.asStateFlow()
    private val _phimMoi = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimMoi = _phimMoi.asStateFlow()
    private val _phimNgauNhien = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimNgauNhien = _phimNgauNhien.asStateFlow()
    private val _phimSearch = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimSearch = _phimSearch.asStateFlow()
    private val _phim = MutableStateFlow<Resource<Movie>>(Resource.Unspecified())
    val phim = _phim.asStateFlow()

    private val _trangPhim= MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val trangPhim=_trangPhim.asStateFlow()


    private val _tatCaPhimtheoTheLoai= MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val tatCaPhimtheoTheLoai=_tatCaPhimtheoTheLoai.asStateFlow()




    init {
        initApiService()
    }

    private fun initApiService() {


        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        movieService = retrofit.create(MovieApiService::class.java)
    }


    fun loadPhimMoi() {
        viewModelScope.launch {
            _phimMoi.emit(Resource.Loading())

            val response = movieService.getMovieHome("moi");

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    _phimMoi.emit(Resource.Success(response.body()!!))
                } else {
                    _phimMoi.emit(Resource.Error("Có lỗi xảy ra khi load phim mới"))
                }
            } else {
                _phimMoi.emit(Resource.Error("Mất kết nối tới server"))
            }

        }
    }

    fun loadPhimBo() {
        viewModelScope.launch {
            _phimBo.emit(Resource.Loading())

            val response = movieService.getMovieHome("bo");


            if (response.code() == 200) {
                _phimBo.emit(Resource.Success(response.body()!!))
            } else {
                _phimBo.emit(Resource.Error("Có lỗi xảy ra khi load phim bộ"))
            }

        }
    }

    fun loadPhimLe() {
        viewModelScope.launch {
            _phimLe.emit(Resource.Loading())

            val response = movieService.getMovieHome("le");

            if (response.code() == 200) {
                _phimLe.emit(Resource.Success(response.body()!!))
            } else {
                _phimLe.emit(Resource.Error("Có lỗi xảy ra khi load phim lẻ"))
            }


        }
    }

    fun loadPhimNgauNhien() {
        viewModelScope.launch {
            _phimNgauNhien.emit(Resource.Loading())

            val response = movieService.getRandomMovie(10);

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    _phimNgauNhien.emit(Resource.Success(response.body()!!))
                } else {
                    _phimNgauNhien.emit(Resource.Error("Có lỗi xảy ra khi load phim ngẫu nhiên"))
                }
            } else {
                _phimNgauNhien.emit(Resource.Error("Mất kết nối tới server"))
            }

        }
    }
    fun loadPhim(id: Int) {
        viewModelScope.launch {
            _phim.emit(Resource.Loading())

            val response = movieService.getMovie(id);

            if (response.code() == 200) {
                _phim.emit(Resource.Success(response.body()!!))
            } else {
                _phim.emit(Resource.Error("Có lỗi xảy ra khi load phim"))
            }

        }
    }

    fun loadPhimSearch(name: String) {
        viewModelScope.launch {
            _phimSearch.emit(Resource.Loading())

            val response = movieService.getMovieSearch("", "DESC", name);

            if (response.code() == 200) {
                _phimSearch.emit(Resource.Success(response.body()!!))
            } else {
                _phimSearch.emit(Resource.Error("Có lỗi xảy ra khi tìm phim"))
            }

        }
    }

    fun loadTrangPhim(offset:Int, categoryId:Int){
        viewModelScope.launch {
            _trangPhim.emit(Resource.Loading())
            val response = movieService.getPageMovieByCategory(offset, 9,"","",categoryId);

            if (response.code() == 200) {
                _trangPhim.emit(Resource.Success(response.body()!!))
            } else {
                _trangPhim.emit(Resource.Error("Có lỗi xảy ra khi load phim"))
            }
        }

    }

    fun loadTatCaPhimTheoTheLoai(categoryId: Int){
        viewModelScope.launch {
            _tatCaPhimtheoTheLoai.emit(Resource.Loading())
            val response = movieService.getAllMovieByCategory("","","",categoryId);

            if (response.code() == 200) {
                _tatCaPhimtheoTheLoai.emit(Resource.Success(response.body()!!))
            } else {
                _tatCaPhimtheoTheLoai.emit(Resource.Error("Có lỗi xảy ra khi load phim"))
            }
        }
    }
}