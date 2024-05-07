package com.example.appxemphim.zalopay



import com.example.appxemphim.zalopay.Helper.Helpers
import com.example.appxemphim.zalopay.HttpProvider.sendPost
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Date
import okhttp3.FormBody

class CreateOrder {

     class CreateOrderData(amount: String) {
        var AppId: String
        var AppUser: String
        var AppTime: String
        var Amount: String
        var AppTransId: String
        var EmbedData: String
        var Items: String
        var BankCode: String
        var Description: String
        var Mac: String

       init {
            val appTime = Date().time
            AppId = AppInfo.APP_ID.toString()
            AppUser = "Android_Demo"
            AppTime = appTime.toString()
            Amount = amount
            AppTransId = Helpers.getAppTransId()
            EmbedData = "{}"
            Items = "[]"
            BankCode = "zalopayapp"
            Description = "Merchant pay for order #" + Helpers.getAppTransId()
            val inputHMac = String.format(
                "%s|%s|%s|%s|%s|%s|%s",
                AppId,
                AppTransId,
                AppUser,
                Amount,
                AppTime,
                EmbedData,
                Items
            )
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }



    fun createOrder(amount: String): JSONObject? {
        val input = CreateOrder.CreateOrderData(amount)
        val formBody: RequestBody = FormBody.Builder()
            .add("app_id", input.AppId)
            .add("app_user", input.AppUser)
            .add("app_time", input.AppTime)
            .add("amount", input.Amount)
            .add("app_trans_id", input.AppTransId)
            .add("embed_data", input.EmbedData)
            .add("item", input.Items)
            .add("bank_code", input.BankCode)
            .add("description", input.Description)
            .add("mac", input.Mac)
            .build()
        return sendPost(AppInfo.URL_CREATE_ORDER, formBody)
    }
}