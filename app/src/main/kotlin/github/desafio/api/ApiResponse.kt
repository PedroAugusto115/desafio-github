package github.desafio.api

class ApiResponse<out T>(val errorMessage: String? = null, val data: T? = null)

fun <T> success(data: T?) = ApiResponse(data = data)

fun <T> error(msg: String?, data: T?) =
        ApiResponse(data = data, errorMessage = msg)
