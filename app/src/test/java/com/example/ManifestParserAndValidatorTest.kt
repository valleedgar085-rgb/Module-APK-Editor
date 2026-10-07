package com.example

import com.example.model.ManifestActivity
import com.example.model.ManifestData
import com.example.model.ManifestValidator
import com.example.util.ManifestXmlParser
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ManifestParserAndValidatorTest {

    @Test
    fun testSerializationAndParsing() {
        val initial = ManifestData(
            packageName = "com.test.app",
            versionCode = 42,
            versionName = "2.3.4",
            appLabel = "Test Application",
            minSdk = 24,
            targetSdk = 35,
            compileSdk = 35,
            isDebuggable = true,
            allowBackup = false,
            permissions = listOf("android.permission.INTERNET", "android.permission.CAMERA"),
            activities = listOf(
                ManifestActivity(
                    name = ".MainActivity",
                    label = "Main Screen",
                    exported = true,
                    isLauncher = true
                )
            )
        )

        val xml = ManifestXmlParser.serialize(initial)
        assertTrue(xml.contains("package=\"com.test.app\""))
        assertTrue(xml.contains("versionCode=\"42\""))
        assertTrue(xml.contains("versionName=\"2.3.4\""))
        assertTrue(xml.contains("android:label=\"Test Application\""))
        assertTrue(xml.contains("android.permission.INTERNET"))
        assertTrue(xml.contains("android.permission.CAMERA"))

        val parsed = ManifestXmlParser.parse(xml)
        assertEquals("com.test.app", parsed.packageName)
        assertEquals(42, parsed.versionCode)
        assertEquals("2.3.4", parsed.versionName)
        assertEquals("Test Application", parsed.appLabel)
        assertEquals(24, parsed.minSdk)
        assertEquals(35, parsed.targetSdk)
        assertEquals(true, parsed.isDebuggable)
        assertEquals(false, parsed.allowBackup)
        assertEquals(2, parsed.permissions.size)
        assertEquals(1, parsed.activities.size)
        assertEquals(".MainActivity", parsed.activities[0].name)
        assertTrue(parsed.activities[0].isLauncher)
        assertTrue(parsed.activities[0].exported)
    }

    @Test
    fun testValidationRules() {
        // Valid data
        val valid = ManifestData(
            packageName = "com.example.myapp",
            versionCode = 1,
            versionName = "1.0",
            appLabel = "MyApp",
            minSdk = 24,
            targetSdk = 34
        )
        val res = ManifestValidator.validate(valid)
        assertTrue(res.isValid)
        assertTrue(res.errors.isEmpty())

        // Invalid package and negative version code
        val invalid = valid.copy(
            packageName = "invalid_pkg_without_dots",
            versionCode = -5
        )
        val resInvalid = ManifestValidator.validate(invalid)
        assertFalse(resInvalid.isValid)
        assertTrue(resInvalid.errors.size >= 2)
    }
}
