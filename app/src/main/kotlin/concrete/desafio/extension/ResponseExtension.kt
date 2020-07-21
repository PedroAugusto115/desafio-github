package concrete.desafio.extension

import concrete.desafio.model.Page
import retrofit2.Response

internal fun Response<Page>.getNextPage(): Int? {
    val nextUrl = headers().get("Link")?.split(',')?.firstOrNull { it.contains("next") }
    return nextUrl?.substring(nextUrl.indexOf("page=") + 5)?.split(">")?.firstOrNull()?.toInt()
}