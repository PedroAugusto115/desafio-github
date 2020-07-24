package github.desafio.extension

import java.text.SimpleDateFormat
import java.util.*

val dateFormat by lazy { SimpleDateFormat("dd/MM/yyy", Locale("PT","BR")) }

fun Date.toSimpleDate(): String = dateFormat.format(this)