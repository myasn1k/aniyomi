package eu.kanade.tachiyomi.tv

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class TvForkIdentityTest {

    @Test
    fun `fork has distinct package name and disables upstream updates`() {
        val buildFile = readText(Path.of("build.gradle.kts"))

        assertTrue("applicationId = \"xyz.jmir.tachiyomi.mi\"" in buildFile)
        assertTrue("buildConfigField(\"boolean\", \"UPDATER_ENABLED\", \"false\")" in buildFile)
    }

    @Test
    fun `fork has distinct user facing name and launcher artwork`() {
        val strings = readText(Path.of("../i18n/src/commonMain/moko-resources/base/strings.xml"))
        val launcher = readText(Path.of("src/main/res/mipmap/ic_launcher.xml"))

        assertTrue(">Aniyomi TV</string>" in strings)
        assertTrue("@drawable/ic_launcher_tv_foreground" in launcher)
        assertFalse("@drawable/ic_launcher_foreground\"/>" in launcher)
    }

    private fun readText(path: Path): String = String(Files.readAllBytes(path))
}
