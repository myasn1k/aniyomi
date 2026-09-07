package eu.kanade.tachiyomi.ui.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AniyomiMPVViewTest {

    @Test
    fun `physical devices retain direct hardware decoding`() {
        assertEquals("auto", initialHardwareDecoder(tryHardwareDecoding = true, hardware = "qcom"))
    }

    @Test
    fun `ranchu emulator uses copy mode to avoid green frames`() {
        assertEquals("auto-copy", initialHardwareDecoder(tryHardwareDecoding = true, hardware = "ranchu"))
    }

    @Test
    fun `goldfish emulator uses copy mode to avoid green frames`() {
        assertEquals("auto-copy", initialHardwareDecoder(tryHardwareDecoding = true, hardware = "goldfish"))
    }

    @Test
    fun `disabled hardware decoding remains disabled on emulators`() {
        assertEquals("no", initialHardwareDecoder(tryHardwareDecoding = false, hardware = "ranchu"))
    }
}
