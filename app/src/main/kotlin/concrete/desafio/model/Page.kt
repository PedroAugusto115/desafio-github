package concrete.desafio.model

import com.google.gson.annotations.SerializedName

class Page(@SerializedName("items") val items: MutableList<Repository>,
           var nextPage: Int? = 1)