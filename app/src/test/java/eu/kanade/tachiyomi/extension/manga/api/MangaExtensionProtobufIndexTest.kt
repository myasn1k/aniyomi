package eu.kanade.tachiyomi.extension.manga.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class MangaExtensionProtobufIndexTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `gzip protobuf index maps extension resources and sources`() {
        val index = ExtensionIndexProto(
            extensionList = ExtensionListProto(
                extensions = listOf(
                    ExtensionProto(
                        name = "Example",
                        packageName = "eu.kanade.tachiyomi.extension.en.example",
                        resources = ExtensionResourcesProto(
                            apkUrl = "https://cdn.example/example.apk",
                            iconUrl = "https://cdn.example/example.png",
                        ),
                        extensionLib = "1.4",
                        versionCode = 12,
                        versionName = "1.4.12",
                        contentWarning = 3,
                        sources = listOf(
                            ExtensionSourceProto(
                                id = 42,
                                name = "Example source",
                                language = "en",
                                homeUrl = "https://example.org",
                            ),
                        ),
                    ),
                ),
            ),
        )
        val encoded = ProtoBuf.encodeToByteArray(ExtensionIndexProto.serializer(), index)
        val compressed = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write(encoded) }
            output.toByteArray()
        }

        val extension = decodeMangaExtensionIndex(compressed, "https://repo.example").single()

        assertEquals("Example", extension.name)
        assertEquals("en", extension.lang)
        assertEquals(12, extension.versionCode)
        assertTrue(extension.isNsfw)
        assertEquals("https://cdn.example/example.png", extension.iconUrl)
        assertEquals("https://cdn.example/example.apk", resolveMangaExtensionApkUrl(extension))
        assertEquals(42, extension.sources.single().id)
        assertEquals("https://example.org", extension.sources.single().baseUrl)
    }
}
