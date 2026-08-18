package io.bashpsk.emptylibs.component.datastoreui

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore by preferencesDataStore(name = "DATASTORE-UI-PSK")

val Context.datastore2 by preferencesDataStore(name = "DATASTORE-UI-PSK-2")