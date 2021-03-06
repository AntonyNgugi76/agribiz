package com.salor.ventgo.ui.activity.barang_assets.detail_barang

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.See
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.assets_barang_list.BarangAssets
import com.salor.ventgo.obj.assets_barang_list.detail.AssetBarangDetail
import com.salor.ventgo.obj.assets_barang_list.detail.WarehouseAssets
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.barang_assets.detail_list_gudang.ListGudangAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_list_barang_detail.*
import kotlinx.android.synthetic.main.dialog_failure_custom.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.IOException

class DetailBarangActivity : BaseActivity() {

    var warehouseAssets: ArrayList<WarehouseAssets> = ArrayList()
    lateinit var barangAssets: BarangAssets
    lateinit var listGudangAdapter: ListGudangAdapter
    var str_json : String = ""

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_barang_detail)

        setStatusBarGradiantListSearch(this)

        str_json = intent.getStringExtra(Cons.JSON)
        barangAssets = Gson().fromJson(str_json, BarangAssets::class.java)


        setData()

        rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        getDataListWarehouse(lLoading)

    }


    private fun getDataListWarehouse(pbLoading: LinearLayout) {
        pbLoading.visibility = View.VISIBLE
        val service = ApiClient.getClient()

        val call = service.assetBarangListDetail(barangAssets.id)

        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pbLoading.visibility = View.GONE
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_barang_asset", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            cvParent.visibility = View.VISIBLE

                            val listProduk = Gson().fromJson(json.toString(),AssetBarangDetail::class.java)

                            tvTitle.text = listProduk.name
                            tvQty.text = listProduk.stock + " " + resources.getString(R.string.label_pcs)
                            tvDate.text = DateTimeMasker.changeToNameMonthCustom(listProduk.createdAt)

                            val jsonDataArray = json.getJSONArray(Cons.ITEMS_WAREHOUSE)

                            val listData = Gson().fromJson(jsonDataArray.toString(), Array<WarehouseAssets>::class.java).toList()

                            warehouseAssets.addAll(listData)

                            listGudangAdapter.notifyDataSetChanged()

                            setAnimHeader()

                        } else {
                            pbLoading.visibility = View.GONE
                            See.toast(this@DetailBarangActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.visibility = View.GONE
                    dialogFailure("list",resources.getString(R.string.label_failure_content_server_title),resources.getString(R.string.label_failure_content_server_content))

                    //         See.toast(this@DetailBarangActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                pbLoading.visibility = View.GONE
                dialogFailure("list",resources.getString(R.string.label_failure_content1),resources.getString(R.string.label_failure_content2))
            }
        })

    }

    fun setData() {

        listGudangAdapter = ListGudangAdapter(this, warehouseAssets,this)
        rvListGudang.setAdapter(listGudangAdapter)
        val layoutManager = LinearLayoutManager(this)
        rvListGudang.setLayoutManager(layoutManager)
        rvListGudang.isNestedScrollingEnabled = false

    }

    fun setAnimHeader() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(lParentContent)
                tvDetailGudang.visibility = View.VISIBLE

            }, 700)
        } catch (e: Exception) {
            e.printStackTrace()
            tvDetailGudang.visibility = View.VISIBLE
        }


    }

    fun dialogFailure(type: String,title : String,subTitle : String) {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window!!.attributes.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_failure_custom)
            dialog.setCancelable(false)

            val btnBack: Button = dialog.btnBack
            val btnRefresh: Button = dialog.btnRefresh

            val tv_Content: TextView = dialog.tv_Content
            val tvContent2: TextView = dialog.tvContent2

            tv_Content.text = title
            tvContent2.text = subTitle
            btnBack.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                onBackPressed()
            })

            btnRefresh.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                if (type == "list") {

                    getDataListWarehouse(lLoading)
                }

            })

            val size = Point()
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            display.getSize(size)
            val mWidth = size.x

            val window = dialog.window
            val wlp = window!!.attributes

            wlp.gravity = Gravity.CENTER
            wlp.x = 0
            wlp.y = 0
            wlp.width = mWidth
            window.attributes = wlp
            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@DetailBarangActivity)
    }
}
