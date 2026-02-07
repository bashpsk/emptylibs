package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.bashpsk.emptylibs.imagekolor.svg.SvgKolor
import io.bashpsk.emptylibs.imagekolor.svg.rememberSvgKolorState

@Composable
fun SvgKolorScreen() {

    val context = LocalContext.current

    val svgString = rememberSaveable {
        context.assets.open("game_win.svg").bufferedReader().use { reader -> reader.readText() }
    }

    val svgKolorState = rememberSvgKolorState(source = svgString)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        SvgKolor(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = svgKolorState
        )
    }
}