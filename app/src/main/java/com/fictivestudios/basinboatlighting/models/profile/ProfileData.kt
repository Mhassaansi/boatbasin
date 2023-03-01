package com.fictivestudios.basinboatlighting.models.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ProfileData(
    var avatar: String?,
    var device_token: String?,
    var device_type: String?,
    var email: String?,
    var emergency_number: String?,
    var first_name: String?,
    var id: Int?,
    var is_allowed_flashy_bird: Int?,
    var is_allowed_light_bird: Int?,
    var is_allowed_location: Int?,
    var is_allowed_loud_bird: Int?,
    var is_allowed_nav_bird: Int?,
    var is_allowed_push_notification: Int?,
    var lang: String?,
    var last_name: String?,
    var lat: String?,
    var license_images: ArrayList<LicenseImage>?,
    var profile_completed: Int?
): Parcelable