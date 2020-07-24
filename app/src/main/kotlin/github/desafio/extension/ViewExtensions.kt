package github.desafio.extension

import android.widget.ImageView
import com.squareup.picasso.Picasso
import github.desafio.R

fun ImageView.loadFromUrl(url: String) {
    Picasso.with(context).load(url).error(R.drawable.img_placeholder).into(this)
}