package concrete.desafio.model

import com.google.gson.annotations.SerializedName
import java.util.*

class PullRequest(@SerializedName("id") val id: Long,
                  @SerializedName("title") val title: String,
                  @SerializedName("body") val description: String = "",
                  @SerializedName("user") val user: Person,
                  @SerializedName("created_at") val dateCreated: Date,
                  @SerializedName("html_url") val pullRequestUrl: String)