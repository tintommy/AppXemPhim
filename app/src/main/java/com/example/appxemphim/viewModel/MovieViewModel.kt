package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.BuyMovieApiService
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.api.apiService.UserApiService
import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.Movie
import com.example.appxemphim.model.MovieBuy
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class MovieViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var movieService: MovieApiService
    private lateinit var buyMovieApiService: BuyMovieApiService
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

    private val _trangPhim = MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val trangPhim = _trangPhim.asStateFlow()


    private val _tatCaPhimtheoTheLoai =
        MutableStateFlow<Resource<List<Movie>>>(Resource.Unspecified())
    val tatCaPhimtheoTheLoai = _tatCaPhimtheoTheLoai.asStateFlow()


    private val _buyMovie = MutableSharedFlow<Resource<Boolean>>()
    val buyMovie = _buyMovie.asSharedFlow()

    private val _existBuyMovie = MutableSharedFlow<Resource<Boolean>>()
    val existBuyMovie = _existBuyMovie.asSharedFlow()


    private val _tatCaPhimDaMua = MutableStateFlow<Resource<List<MovieBuy>>>(Resource.Unspecified())
    val tatCaPhimDaMua = _tatCaPhimDaMua.asStateFlow()


    init {
        initApiService()
    }

    private fun initApiService() {


        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        movieService = retrofit.create(MovieApiService::class.java)
        buyMovieApiService = retrofit.create(BuyMovieApiService::class.java)
    }


    fun loadPhimMoi() {
        viewModelScope.launch {
            _phimMoi.emit(Resource.Loading())

            val response = movieService.getMovieHome("moi");

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    _phimMoi.emit(Resource.Success(response.body()!!.data))
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
                _phimBo.emit(Resource.Success(response.body()!!.data))
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
                _phimLe.emit(Resource.Success(response.body()!!.data))
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
                    _phimNgauNhien.emit(Resource.Success(response.body()!!.data))
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
                _phim.emit(Resource.Success(response.body()!!.data))
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
                _phimSearch.emit(Resource.Success(response.body()!!.data))
            } else {
                _phimSearch.emit(Resource.Error("Có lỗi xảy ra khi tìm phim"))
            }

        }
    }

    fun loadTrangPhim(offset: Int, categoryId: Int) {
        viewModelScope.launch {
            _trangPhim.emit(Resource.Loading())
            val response = movieService.getPageMovieByCategory(offset, 9, "", "", categoryId);

            if (response.code() == 200) {
                _trangPhim.emit(Resource.Success(response.body()!!.data))
            } else {
                _trangPhim.emit(Resource.Error("Có lỗi xảy ra khi load phim"))
            }
        }

    }

    fun loadTatCaPhimTheoTheLoai(categoryId: Int) {
        viewModelScope.launch {
            _tatCaPhimtheoTheLoai.emit(Resource.Loading())
            val response = movieService.getAllMovieByCategory("", "", "", categoryId);

            if (response.code() == 200) {
                _tatCaPhimtheoTheLoai.emit(Resource.Success(response.body()!!.data))
            } else {
                _tatCaPhimtheoTheLoai.emit(Resource.Error("Có lỗi xảy ra khi load phim"))
            }
        }
    }

    fun userBuyMovie(movie: Movie, date: String) {
        val movieBuy = MovieBuy(0, "", movie.movieId, "", date, username)
        viewModelScope.launch {
            val response = buyMovieApiService.buyMovie(movieBuy)
            if (response.isSuccessful)
                _buyMovie.emit(Resource.Success(true))
            else
                _buyMovie.emit(Resource.Error("false"))
        }

    }

    fun checkExistMovieBuy(movie: Movie) {
        viewModelScope.launch {
            _existBuyMovie.emit(Resource.Loading())
            val response = buyMovieApiService.checkExistBuy(movie.movieId, username)
            if (response.isSuccessful)
                _existBuyMovie.emit(Resource.Success(response.body()!!.data))
            else {
                _existBuyMovie.emit(Resource.Error("Lỗi"))

            }

        }
    }

    fun getAllMovieBought() {
        viewModelScope.launch {
            _tatCaPhimDaMua.emit(Resource.Loading())
            val response = buyMovieApiService.getAllByUser(username)
            if (response.isSuccessful)
                _tatCaPhimDaMua.emit(Resource.Success(response.body()!!.data))
            else {
                if (response.code() == 404)
                    _tatCaPhimDaMua.emit(Resource.Error("404"))
                else
                    _tatCaPhimDaMua.emit(Resource.Error("Lỗi"))
            }

        }
    }

    suspend fun performTask(): Boolean = suspendCoroutine { continuation ->
        // Thực hiện các hoạt động cần đợi trong viewModelScope
        viewModelScope.launch {
            // Thực hiện công việc ở đây
            delay(1000) // Ví dụ: giả sử đợi 1 giây
            // Kết thúc công việc và trả về kết quả
            if(1+1==2)
            continuation.resume(true) // Trả về true nếu công việc hoàn thành thành công
            else
            {

            }
        }
    }
}