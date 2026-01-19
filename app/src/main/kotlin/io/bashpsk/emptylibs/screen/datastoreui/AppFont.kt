package io.bashpsk.emptylibs.screen.datastoreui

import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.datastoreui.font.FontPreferenceItem
import kotlinx.collections.immutable.toImmutableList

enum class AppFont : FontPreferenceItem {

    ONE {

        override val label: String = "One"

        override val resId: Int = R.font.germania_one_regular
    },
    TWO {

        override val label: String = "Two"

        override val resId: Int = R.font.gemunu_libre_semi_bold
    },
    THREE {

        override val label: String = "Three"

        override val resId: Int = R.font.nova_flat_regular
    };

    companion object {

        val fontEntities = entries.toImmutableList()
    }
}