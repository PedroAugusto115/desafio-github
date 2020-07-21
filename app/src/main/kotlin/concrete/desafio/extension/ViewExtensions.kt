package concrete.desafio.extension

import android.widget.ImageView
import com.squareup.picasso.Picasso
import concrete.desafio.R

fun ImageView.loadFromUrl(url: String) {
    Picasso.with(context).load(url).error(R.drawable.img_placeholder).into(this)
}