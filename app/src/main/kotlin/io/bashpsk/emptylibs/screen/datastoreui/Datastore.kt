package io.bashpsk.emptylibs.screen.datastoreui

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore by preferencesDataStore(name = "DATASTORE-UI-PSK")