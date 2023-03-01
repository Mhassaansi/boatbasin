package com.fictivestudios.getmefit.Networking

import com.fictivestudios.basinboatlighting.models.content.Content
import com.fictivestudios.basinboatlighting.models.login.LoginResponse
import com.fictivestudios.basinboatlighting.models.profile.ProfileMain
import com.fictivestudios.basinboatlighting.models.signup.SignupResponse
import com.fictivestudios.basinboatlighting.models.subscribe.SubscribeReponse
import com.fictivestudios.basinboatlighting.models.weather.Weather
import com.fictivestudios.basinboatlighting.ui.home.weather.WeatherScreen
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.ArrayList


interface ApiService {

///////////////////////////////////////////Registartion////////////////////////////////////////////
    @POST(Registration)
    suspend fun registration(@Body requestBody: RequestBody): Response<SignupResponse>


    @POST(Verification)
    suspend fun verifyOtp(@Body requestBody: RequestBody): Response<LoginResponse>


    @POST(Resend_Verification)
    suspend fun resend_verifyOtp(@Body requestBody: RequestBody): Response<CommonResponse>


    @POST(Login)
    suspend fun Login(@Body requestBody: RequestBody): Response<LoginResponse>


    @POST(Forget_Password)
    suspend fun forgetPassword(@Body requestBody: RequestBody): Response<SignupResponse>



    @POST(Logout)
    suspend fun logout(): Response<CommonResponse>


    @GET(Content_Data)
    suspend fun content(@Query("slug") type:String): Response<Content>



    @POST(Change_Password)
    suspend fun changePassword(@Body requestBody: RequestBody): Response<CommonResponse>

    @POST(RESET_PASSWORD)
    suspend fun resetPassword(@Query("email") email:String,@Query("reference_code") refrenceCode:String,
                               @Query("new_password") password:String): Response<CommonResponse>





/////////////////////////////////////////////CoreModule//////////////////////////////////////////


    @DELETE(DeleteProfile)
    suspend fun deleteProfile():Response<CommonResponse>

    @POST(AllowPermission)
    suspend fun allowPermission(@Body requestBody: RequestBody):Response<CommonResponse>

    @POST(SUBSCRIPTION)
    suspend fun subscription(@Body requestBody: RequestBody):Response<SubscribeReponse>

    @GET(ViewProfile)
    suspend fun profile():Response<ProfileMain>

    @POST(UpdateLocation)
    suspend fun updateLocation(@Body requestBody: RequestBody):Response<CommonResponse>

    @Multipart
    @POST(UpdateProfile)
    suspend fun updateProfile(@PartMap hashMap: HashMap<String, RequestBody>, @Part
    image:MultipartBody.Part?, @Part certifications: ArrayList<MultipartBody.Part>?)
    :Response<CommonResponse>

    @POST(DeleteLicenseImage)
    suspend fun deleteImage(@Body requestBody: RequestBody):Response<CommonResponse>


    @POST(SOS_NOTI)
    suspend fun sosNotification():Response<CommonResponse>



    //////////////////////////////////////////Weather Api //////////////////////////////////////////

    @GET("data/3.0/onecall")
    suspend fun weather(@Query("lat") lat:Float , @Query("lon") lon:Float
                        , @Query("appid") appid:String ,@Query("units") units:String):Response<Weather>









}