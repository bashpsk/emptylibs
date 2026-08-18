package io.bashpsk.emptylibs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.component.datastoreui.AppTheme
import io.bashpsk.emptylibs.component.datastoreui.datastore
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.screen.ActivityContent
import io.bashpsk.emptylibs.ui.theme.EmptyLibsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val getAppTheme by datastore.getPreference(
                key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
                initial = AppTheme.System.name
            ).collectAsStateWithLifecycle(initialValue = AppTheme.System.name)

            CompositionLocalProvider(LocalDatastore provides datastore) {

                EmptyLibsTheme(darkTheme = AppTheme.getTheme(theme = getAppTheme)) {

                    ActivityContent()
                }
            }
        }
    }
}