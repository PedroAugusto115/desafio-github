package github.desafio.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Repository(@SerializedName("id") val id: Long,
                 @SerializedName("name") val name: String,
                 @SerializedName("description") val description: String,
                 @SerializedName("forks_count") val numberOfForks: Long,
                 @SerializedName("stargazers_count") val numberOfWatchers: Long,
                 @SerializedName("open_issues_count") val numberOfOpenIssues: Long,
                 @SerializedName("owner") val owner: Person) : Parcelable