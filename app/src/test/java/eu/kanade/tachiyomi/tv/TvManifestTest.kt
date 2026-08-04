package eu.kanade.tachiyomi.tv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

class TvManifestTest {

    private val androidNamespace = "http://schemas.android.com/apk/res/android"
    private val manifest = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = true
    }.newDocumentBuilder().parse(Path.of("src/main/AndroidManifest.xml").toFile())

    @Test
    fun `APK is discoverable by both TV and handheld launchers`() {
        val categories = manifest.getElementsByTagName("category")
        val names = (0 until categories.length).map {
            categories.item(it).attributes.getNamedItemNS(androidNamespace, "name").nodeValue
        }

        assertTrue("android.intent.category.LAUNCHER" in names)
        assertTrue("android.intent.category.LEANBACK_LAUNCHER" in names)
    }

    @Test
    fun `TV compatibility does not exclude touch devices or require a touchscreen`() {
        val features = manifest.getElementsByTagName("uses-feature")
        val requiredByName = (0 until features.length).associate {
            val attributes = features.item(it).attributes
            attributes.getNamedItemNS(androidNamespace, "name").nodeValue to
                attributes.getNamedItemNS(androidNamespace, "required").nodeValue
        }

        assertEquals("false", requiredByName["android.software.leanback"])
        assertEquals("false", requiredByName["android.hardware.touchscreen"])
    }

    @Test
    fun `TV launcher banner is declared and present`() {
        val application = manifest.getElementsByTagName("application").item(0)
        assertEquals(
            "@drawable/tv_banner",
            application.attributes.getNamedItemNS(androidNamespace, "banner").nodeValue,
        )
        assertTrue(Files.isRegularFile(Path.of("src/main/res/drawable/tv_banner.xml")))
    }
}
