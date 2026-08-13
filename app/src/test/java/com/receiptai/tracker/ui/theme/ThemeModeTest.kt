package com.receiptai.tracker.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {
    @Test
    fun `unknown stored theme falls back to system default`() {
        assertEquals(
            ThemeMode.SYSTEM_DEFAULT,
            ThemeMode.fromStorageValue("unknown")
        )
    }

    @Test
    fun `theme modes keep stable storage values`() {
        assertEquals("system", ThemeMode.SYSTEM_DEFAULT.storageValue)
        assertEquals("light", ThemeMode.LIGHT.storageValue)
        assertEquals("dark", ThemeMode.DARK.storageValue)
    }
}
