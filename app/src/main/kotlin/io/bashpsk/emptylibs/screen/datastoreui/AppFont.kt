package io.bashpsk.emptylibs.screen.datastoreui

import io.bashpsk.emptylibs.R

enum class AppFont(val resId: Int) {

    ONE(resId = R.font.gemunu_libre_semi_bold),
    TWO(resId = R.font.germania_one_regular),
    THREE(resId = R.font.nova_flat_regular)
}

val fontEntities = AppFont.entries.associate { font -> font.resId to font.name }