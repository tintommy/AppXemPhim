package com.example.appxemphim.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.appxemphim.R
import com.example.appxemphim.databinding.ActivityThanhToanBinding
import com.example.appxemphim.fragments.PhimFragment
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.CONFIG
import com.example.appxemphim.viewModel.MovieViewModel
import com.example.appxemphim.zalopay.CreateOrder
import com.vnpay.authentication.VNP_AuthenticationActivity
import com.vnpay.authentication.VNP_SdkCompletedCallback
import dagger.hilt.android.AndroidEntryPoint
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.util.Calendar

@AndroidEntryPoint
class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private lateinit var movie: Movie
    private val movieViewModel by viewModels<MovieViewModel>()
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0

    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)



        movie = (intent.getSerializableExtra("movie") as? Movie)!!
        if (movie != null) {
            binding.tvTenPhim.text = movie.name
            Glide.with(this).load(CONFIG.CLOUD_URL+movie.image).into(binding.ivHinh)
            binding.tvMoneyPrice.text= movie.price.toString()
            binding.btnBack.setOnClickListener {
                finish()
            }
            binding.btnConfirm.setOnClickListener {
                binding.progressBar.visibility=View.VISIBLE
                requestZalo(movie)
            }
//            binding.btnConfirmVnPay.setOnClickListener {
//                requestVnPay()
//            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
    private fun requestZalo(movie: Movie) {
        binding.progressBar.visibility=View.GONE
        val orderApi = CreateOrder()

        try {
            val data = orderApi.createOrder(movie.price.toString())


            val code = data!!.getString("return_code")
            Toast.makeText(this, "return_code: $code", Toast.LENGTH_LONG).show()
            if (code == "1") {

                val token: String = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(
                    this,
                    token,
                    "demozpdk://app",
                    object : PayOrderListener {
                        override fun onPaymentSucceeded(
                            transactionId: String,
                            transToken: String,
                            appTransID: String?
                        ) {
                            Toast.makeText(this@ThanhToanActivity, "Mời bạn xem phim", Toast.LENGTH_LONG).show()
                                movieViewModel.userBuyMovie(movie,dinhDangNgayAPI(ngay, thang, nam))
//                            var b: Bundle = Bundle()
//                            b.putInt("movieId", movie.movieId)
//                            val phimFragment = PhimFragment()
//                            phimFragment.arguments = b
//                            (activity as MainActivity).replaceFragment(phimFragment)
                            MainActivity.muaPhim.thanhToan=true
                            MainActivity.muaPhim.phim=movie
                            finish()
                        }

                        override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                            Toast.makeText(this@ThanhToanActivity, "Huỷ giao dịch", Toast.LENGTH_SHORT).show()
                            MainActivity.muaPhim.thanhToan=false
                        }

                        override fun onPaymentError(
                            zaloPayErrorCode: ZaloPayError?,
                            zpTransToken: String?,
                            appTransID: String?
                        ) {
                            Toast.makeText(this@ThanhToanActivity, "Xảy ra lỗi khi thanh toán", Toast.LENGTH_SHORT).show()
                            MainActivity.muaPhim.thanhToan=false
                        }


                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun requestVnPay() {
//        val intent = Intent(this, VNP_AuthenticationActivity::class.java).apply {
//            putExtra("url", "https://sandbox.vnpayment.vn/testsdk/") //bắt buộc, VNPAY cung cấp
//            putExtra("tmn_code", "FAHASA03") //bắt buộc, VNPAY cung cấp
//            putExtra("scheme", "thanhtoanactivity") //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
//            putExtra("is_sandbox", true) //bắt buộc, true <=> môi trường test, true <=> môi trường live
//        }
////        VNP_AuthenticationActivity.setSdkCompletedCallback(object : VNP_SdkCompletedCallback {
////            override fun sdkAction(action: String) {
////                Log.wtf("SplashActivity", "action: $action")
////                //action == AppBackAction
////                //Người dùng nhấn back từ sdk để quay lại
////
////                //action == CallMobileBankingApp
////                //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
////                //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.
////
////                //action == WebBackAction
////                //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi url có chứa: cancel.sdk.merchantbackapp
////
////                //action == FaildBackAction
////                //giao dịch thanh toán bị failed
////
////                //action == SuccessBackAction
////                //thanh toán thành công trên webview
////            }
////        })
//        startActivity(intent)
//    }
private fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
    var temp = ""
    temp += nam
    temp += "-"
    temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
    temp += "-"
    temp += if (ngay < 10) "0$ngay" else ngay.toString()
    return temp
}
}