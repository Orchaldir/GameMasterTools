package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DistanceTest {

    @Test
    fun `Millimeters to string`() {
        assertEquals("0.001 m", fromMillimeters(1).toString())
        assertEquals("0.012 m", fromMillimeters(12).toString())
        assertEquals("0.123 m", fromMillimeters(123).toString())
        assertEquals("1.234 m", fromMillimeters(1234).toString())
        assertEquals("12.345 m", fromMillimeters(12345).toString())
    }

    @Nested
    inner class ConversionTest {
        @Nested
        inner class FromMeterTest {

            @Test
            fun `Integer meters to meter`() {
                assertEquals(2.0f, fromMeters(2).toMeters())
            }

            @Test
            fun `Float meters to meter`() {
                assertEquals(1.5f, fromMeters(1.5f).toMeters())
            }

            @Test
            fun `Integer meters to millimeters`() {
                assertEquals(2000.0f, fromMeters(2).toMillimeters())
            }

            @Test
            fun `Float meters to millimeters`() {
                assertEquals(1500.0f, fromMeters(1.5f).toMillimeters())
            }
        }

        @Nested
        inner class FromMillimetersTest {

            @Test
            fun `Integer millimeters to meters`() {
                assertEquals(1.234f, fromMillimeters(1234).toMeters())
            }

            @Test
            fun `Float millimeters to meters`() {
                assertEquals(1.2345f, fromMillimeters(1234.5f).toMeters())
            }

            @Test
            fun `Integer millimeters to millimeters`() {
                assertEquals(1234.0f, fromMillimeters(1234).toMillimeters())
            }

            @Test
            fun `Float millimeters to millimeters`() {
                assertEquals(1234.5f, fromMillimeters(1234.5f).toMillimeters())
            }
        }
    }
}