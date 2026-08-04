package eu.kanade.presentation.more.onboarding

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eu.kanade.presentation.more.settings.screen.SettingsDataScreen
import eu.kanade.tachiyomi.util.system.isTvBox
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

internal class StorageStep : OnboardingStep {

    private val storagePref = Injekt.get<StoragePreferences>().baseStorageDirectory()
    private val folderProvider = Injekt.get<AndroidStorageFolderProvider>()

    private var _isComplete by mutableStateOf(false)

    override val isComplete: Boolean
        get() = _isComplete

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val handler = LocalUriHandler.current

        val isTvBox = isTvBox(LocalContext.current)

        fun hasTvStorageAccess(): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()
        }

        fun createDefaultTvFolder() {
            val storage = folderProvider.directory()
            if (!storage.exists()) {
                storage.mkdirs()
            }
            if (storage.exists()) {
                storagePref.set(storagePref.defaultValue())
                _isComplete = true
            }
        }

        val storageAccessLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (hasTvStorageAccess()) {
                    createDefaultTvFolder()
                }
            },
        )

        val pickStorageLocation = SettingsDataScreen.storageLocationPicker(storagePref)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        ) {
            Text(
                stringResource(
                    MR.strings.onboarding_storage_info,
                    stringResource(MR.strings.app_name),
                    SettingsDataScreen.storageLocationText(storagePref),
                ),
            )

            if (isTvBox) {
                if (!storagePref.isSet()) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (hasTvStorageAccess()) {
                                createDefaultTvFolder()
                            } else {
                                storageAccessLauncher.launch(
                                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                        data = "package:${context.packageName}".toUri()
                                    },
                                )
                            }
                        },
                    ) {
                        Text(stringResource(AYMR.strings.onboarding_storage_action_create_folder))
                    }
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        try {
                            pickStorageLocation.launch(null)
                        } catch (e: ActivityNotFoundException) {
                            context.toast(MR.strings.file_picker_error)
                        }
                    },
                ) {
                    Text(stringResource(MR.strings.onboarding_storage_action_select))
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(stringResource(MR.strings.onboarding_storage_help_info, stringResource(MR.strings.app_name)))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { handler.openUri(SettingsDataScreen.HELP_URL) },
            ) {
                Text(stringResource(MR.strings.onboarding_storage_help_action))
            }
        }

        LaunchedEffect(Unit) {
            storagePref.changes()
                .collectLatest {
                    _isComplete = isTvStorageReady(
                        isTvBox = isTvBox,
                        storagePreferenceSet = storagePref.isSet(),
                        hasExternalStorageManager = hasTvStorageAccess(),
                    )
                }
        }
    }
}

internal fun isTvStorageReady(
    isTvBox: Boolean,
    storagePreferenceSet: Boolean,
    hasExternalStorageManager: Boolean,
): Boolean {
    return storagePreferenceSet && (!isTvBox || hasExternalStorageManager)
}
