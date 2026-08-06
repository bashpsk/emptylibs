package io.bashpsk.emptylibs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.screen.datastoreui.AppTheme
import io.bashpsk.emptylibs.screen.datastoreui.datastore
import io.bashpsk.emptylibs.ui.theme.EmptyLibsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val getAppTheme by datastore.getPreference(
                key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
                initial = AppTheme.SYSTEM.name
            ).collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM.name)

            CompositionLocalProvider(LocalDatastore provides datastore) {

                EmptyLibsTheme(darkTheme = AppTheme.getTheme(theme = getAppTheme)) {

//                    AnalogClockScreen()
//                    BasicTextEditorScreen()
//                    BottomOptionBarScreen()
                    CanvasSlateScreen()
//                    ColorPickerDialogScreen()
//                    ColorPickerScreen()
//                    DatastoreUIScreen()
//                    DialClockScreen()
//                    DialTextPickerScreen()
//                    DigitalClockScreen()
//                    FileCopySpeedScreen()
//                    FileWriteSpeedScreen()
//                    FormatterScreen()
//                    ImageColorPickerScreen()
//                    ImageColorPickerDialogScreen()
//                    ImageCropScreen()
//                    ImageEditScreen()
//                    ImageFilterScreen()
//                    ImageKolorScreen()
//                    ImageWallpaperScreen()
//                    InkSlateScreen()
//                    JoyStickScreen()
//                    LazyTextViewerScreen()
//                    LruCacheScreen()
//                    MusicPlayingAnimationScreen()
//                    PathShapeScreen()
//                    PdfTemplateScreen()
//                    PdfViewerScreen()
//                    ScrollBarLazyGridScreen()
//                    ScrollBarLazyListScreen()
//                    SevenSegmentDisplayScreen()
//                    StorageScreen()
//                    StorageSearchScreen()
//                    SvgKolorScreen()
//                    TileImageViewScreen()
//                    TransformImageScreen()
//                    ZoomableLayoutScreen()
//                    VideoGestureScreen()
//                    WaveAnimationScreen()
//                    WheelTextPickerScreen()
                }
            }
        }
    }
}