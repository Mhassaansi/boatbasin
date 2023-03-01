package com.fictivestudios.basinboatlighting.ui.home.inapppruchase

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.FragmentInAppPurchaseBinding
import com.fictivestudios.basinboatlighting.models.subscribe.SubscribeReponse
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class InAppPurchaseFragment : BaseFragment() {

    companion object {
        fun newInstance() = InAppPurchaseFragment()
    }

    private lateinit var viewModel: InAppPurchaseViewModel

    private lateinit var binding:FragmentInAppPurchaseBinding
    private var skuDetails: SkuDetails? =null

    private var bundleList: ArrayList<SkuDetails>? = ArrayList<SkuDetails>()

    private var purchasesUpdatedListener : PurchasesUpdatedListener?=null
    private var billingClient : BillingClient?=null

    private var purchasetoken: String?=null
    override fun setTitlebar(titlebar: Titlebar) {
        getActivityContext?.let { titlebar.setTitleHome(it,"Yearly Subscription") }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_in_app_purchase, container, false)

        setUpBilling()

        return binding.root
    }







    private fun setUpBilling() {
        requireActivity().runOnUiThread {
            purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.

                if (purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)


                        /*         billingClient?.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,object :PurchaseHistoryResponseListener{
                                     override fun onPurchaseHistoryResponse(
                                         billingResult: BillingResult,
                                         purchaseHistory: MutableList<PurchaseHistoryRecord>?
                                     ) {
                                         Log.d("billing response",purchaseHistory.toString())

                                         var bundleType:String


                                     }
                                 })*/
                    }
                }


            }
            billingClient = purchasesUpdatedListener?.let {
                BillingClient.newBuilder(requireContext())
                    .setListener(it)
                    .enablePendingPurchases()
                    .build()
            }


            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        Log.d("billing","billing ok")
                        // The BillingClient is ready. You can query purchases here.


                        queryAvaliableProducts()



                    }
                }
                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    Log.d("billing","billing disconnected")
                }
            })
        }
    }


    fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
        val purchase: Purchase = purchase
        purchasetoken = purchase.purchaseToken

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        //consumeable
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()
        val consumeResult = GlobalScope.launch(Dispatchers.IO) {
            billingClient?.consumePurchase(consumeParams)

            if (skuDetails!=null && purchasetoken!=null )
            {
                subscribeBundle(purchasetoken!!, skuDetails!!.sku)
            }
        /*    if (skuDetails!=null && purchasetoken!=null )
            {
                val string =skuDetails!!.description.substring(0,9);
                var limit   = string.filter { it.isDigit() }
                if (skuDetails!!.sku.contains("photo"))
                {

                //    buyBundle(skuDetails!!.sku.toString(), purchasetoken!!,"google",null,limit.toInt())
                }
                else
                {
                  //  buyBundle(skuDetails!!.sku.toString(), purchasetoken!!,"google",limit.toInt(),null)
                }

            }*/
        }





    }

    private fun queryAvaliableProducts() {
        Log.d("billing","queryAvaliableProducts")

        val skuList = ArrayList<String>()
     //

    //    skuList.add("yearly-subscription")
       // skuList.add("yearly-sub")
        skuList.add("com.fictivestudios.basinboat_year")

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.


            requireActivity().runOnUiThread {

                Log.d("billing",billingResult.responseCode.toString())
                Log.d("billing","skuList:"+skuDetailsList.toString())
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {

                   binding.packageName.text = skuDetailsList[0].title
                    binding.packageDes.text = skuDetailsList[0].description
                    binding.packagePrice.text = skuDetailsList[0].price

                  binding.btnAccept.setOnClickListener {
                       launchBillingFlow(skuDetailsList[0])

                    }
                    /* for (skuDetails in skuDetailsList) {
                         Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")

                     }*/


                }
            }


        }
    }
    private  fun launchBillingFlow(skuDetails:SkuDetails)
    {


            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                billingClient?.launchBillingFlow(requireActivity(), billingFlowParams)?.responseCode


            }?:"failed"

            this.skuDetails = skuDetails




    }


    private fun  subscribeBundle(purchaseToken:String,productId:String) {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<SubscribeReponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<SubscribeReponse> {

                                return apiInterFace.subscription(
                                    JSONObject().apply {
                                        put("receipt_token", purchaseToken)
                                        put("product_id", productId)

                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<SubscribeReponse>) {

                                if (response.body()?.status == 1) {

                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )



                                    PreferenceData.storeProfileData(requireActivity(),response.body()?.data!!)


                                    val actiontoInappFragment = InAppPurchaseFragmentDirections.actionInAppPurchaseFragmentToConnectblueThooth2()

                                    findNavController().navigate(actiontoInappFragment)


                                } else {
                                    showToast(response.body()?.message!!, requireActivity())

                                }

                            }

                            override fun error(message: String) {
                                //RetrofitSetup().hideLoader()
                                //Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                                showToast(message, requireActivity())
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast(R.string.internetconnection.toString(), getActivityContext)

            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InAppPurchaseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}