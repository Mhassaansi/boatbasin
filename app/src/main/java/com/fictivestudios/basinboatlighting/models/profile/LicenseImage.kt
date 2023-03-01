package com.fictivestudios.basinboatlighting.models.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LicenseImage(
    var id: Int?,
    var license_image: String?
): Parcelable