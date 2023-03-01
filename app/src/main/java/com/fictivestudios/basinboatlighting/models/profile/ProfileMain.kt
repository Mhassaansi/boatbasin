package com.fictivestudios.basinboatlighting.models.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileMain(
    var `data`: ProfileData,
    var message: String,
    var status: Int
): Parcelable