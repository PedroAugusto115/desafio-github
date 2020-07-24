package github.desafio.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Person(@SerializedName("login") val name: String,
             @SerializedName("avatar_url") val photoPath: String) : Parcelable