package com.example.appxemphim.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private lateinit var movieService: MovieApiService

    private val _phimLe = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimLe = _phimLe.asStateFlow()
    private val _phimBo = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimBo = _phimBo.asStateFlow()
    private val _phimMoi = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimMoi = _phimMoi.asStateFlow()
    private val _phimSearch = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val phimSearch = _phimSearch.asStateFlow()
    private val _phim = MutableStateFlow<Resource<Movie>>(Resource.Unspecified())
    val phim = _phim.asStateFlow()

    init {
        initApiService()
    }

    private fun initApiService() {
        var token =
            "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJyciIsImlhdCI6MTcxMTcxODQ4MSwiZXhwIjoxNzEyMzIzMjgxfQ.uyKxi9H_47paeT5YUGfobaihsH-f1DbV7nBO9nuiHKVKYpiq0uymBoPeS9V70B17"
        var retrofit = API_Instance.getClient(token)
        movieService = retrofit.create(MovieApiService::class.java)

    }


     fun loadPhimMoi() {
        viewModelScope.launch {
            _phimMoi.emit(Resource.Loading())

            val response= movieService.getMovieHome("moi");

            if(response.isSuccessful){
                if(response.code()==200){
                    _phimMoi.emit(Resource.Success(response.body()!!))
                }
                else{
                    _phimMoi.emit(Resource.Error("Có lỗi xảy ra khi load phim mới"))
                }
            }
            else{
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

    fun loadPhim(id:Int){
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

    fun loadPhimSearch(name:String){
        viewModelScope.launch {
            _phimSearch.emit(Resource.Loading())

            val response = movieService.getMovieSearch("","DESC",name);

            if (response.code() == 200) {
                _phimSearch.emit(Resource.Success(response.body()!!))
            } else {
                _phimSearch.emit(Resource.Error("Có lỗi xảy ra khi tìm phim"))
            }

        }
    }
}