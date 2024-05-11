package com.example.appxemphim.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.api.API_Instance
import com.example.appxemphim.api.apiService.CommentApiService
import com.example.appxemphim.api.apiService.MovieApiService
import com.example.appxemphim.model.Comment
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var commentApiService: CommentApiService
    private lateinit var token: String
    private lateinit var username: String

    private val _luuBinhLuan = MutableStateFlow<Resource<Comment>>(Resource.Unspecified())
    val luuBinhLuan = _luuBinhLuan.asStateFlow()
    private val _taiBinhLuan = MutableStateFlow<Resource<List<Comment>>>(Resource.Unspecified())
    val taiBinhLuan = _taiBinhLuan.asStateFlow()

    init {
        initApiService()
    }

    private fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()
        var retrofit = API_Instance.getClient(token)
        commentApiService = retrofit.create(CommentApiService::class.java)
    }

    fun saveComment(content: String, movieId: Int, value: Int, date: String) {
        viewModelScope.launch {
            val comment: Comment = Comment("", content, date, 0, movieId, "", username, value)
            _luuBinhLuan.emit(Resource.Loading())
            val response = commentApiService.saveMovieComment(comment)
            if (response.isSuccessful) {
                _luuBinhLuan.emit(Resource.Success(response.body()!!.data))
            } else {
                _luuBinhLuan.emit(Resource.Error("Xảy ra lỗi khi lưu bình luận"))

            }
        }
    }

    fun loadCmt(offset:Int,movieId: Int){
        viewModelScope.launch {
            _taiBinhLuan.emit(Resource.Loading())
            val reponse = commentApiService.getPageComment(offset,5,movieId)
            if(reponse.isSuccessful){
                _taiBinhLuan.emit(Resource.Success(reponse.body()!!.data))
            }
            else{

                _taiBinhLuan.emit(Resource.Error("Lỗi khi tải bình luận"))
            }
        }

    }

}