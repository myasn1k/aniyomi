package eu.kanade.tachiyomi.util.system

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import eu.kanade.domain.ui.UiPreferences
import eu.kanade.domain.ui.model.TvUiMode
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

fun isTvBox(context: Context): Boolean {
    val pm: PackageManager = context.packageManager

    // TV for sure
    if (context.isTelevision()) {
        return true
    }

    // Missing Files app (DocumentsUI) means box (some boxes still have non functional app or stub)
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.setType("video/*")
    if (intent.resolveActivity(pm) == null) {
        return true
    }

    // Legacy storage no longer works on Android 11 (level 30)
    if (Build.VERSION.SDK_INT < 30) {
        // (Some boxes still report touchscreen feature)
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            return true
        }
        if (pm.hasSystemFeature("android.hardware.hdmi.cec")) {
            return true
        }
        if (Build.MANUFACTURER.equals("zidoo", ignoreCase = true)) {
            return true
        }
    }

    // Default: No TV - use SAF
    return false
}

/**
 * Returns whether this is a real Android TV device.
 *
 * Keep this stricter than [isTvBox]: missing storage apps are useful for choosing a storage
 * workflow, but are not enough evidence to replace touch interactions with D-pad interactions.
 */
fun Context.isTelevision(): Boolean {
    val modeType = getSystemService(UiModeManager::class.java)?.currentModeType
    return isTelevision(
        uiModeType = modeType,
        hasLeanbackFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK),
    )
}

internal fun isTelevision(uiModeType: Int?, hasLeanbackFeature: Boolean): Boolean {
    return uiModeType == Configuration.UI_MODE_TYPE_TELEVISION || hasLeanbackFeature
}

/** UI policy is user-selectable; storage capability detection must remain independent. */
fun Context.isTvUiEnabled(): Boolean = resolveTvUiMode(
    mode = Injekt.get<UiPreferences>().tvUiMode().get(),
    television = isTelevision(),
)

internal fun resolveTvUiMode(mode: TvUiMode, television: Boolean): Boolean = when (mode) {
    TvUiMode.AUTOMATIC -> television
    TvUiMode.ALWAYS -> true
    TvUiMode.NEVER -> false
}
