package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DistanceTest {

    @Test
    fun `Millimeters to string`() {
        assertEquals("123 μm", fromMicrometers(123).toString())
        assertEquals("1.50 mm", fromMillimeters(1.5f).toString())
        assertEquals("12.00 mm", fromMillimeters(12).toString())
        assertEquals("123.00 mm", fromMillimeters(123).toString())
        assertEquals("1.23 m", fromMillimeters(1234).toString())
        assertEquals("12.35 m", fromMillimeters(12345).toString())
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

            @Test
            fun `Integer meters to micrometers`() {
                assertEquals(2000000, fromMeters(2).toMicrometers())
            }

            @Test
            fun `Float meters to micrometers`() {
                assertEquals(1500000, fromMeters(1.5f).toMicrometers())
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

            @Test
            fun `Integer millimeters to micrometers`() {
                assertEquals(1234000, fromMillimeters(1234).toMicrometers())
            }

            @Test
            fun `Float millimeters to micrometers`() {
                assertEquals(1234500, fromMillimeters(1234.5f).toMicrometers())
            }
        }


        @Nested
        inner class FromMicrometersTest {

            @Test
            fun `To meters`() {
                assertEquals(0.001234f, fromMicrometers(1234).toMeters())
            }

            @Test
            fun `To millimeters`() {
                assertEquals(1.234f, fromMicrometers(1234).toMillimeters())
            }

            @Test
            fun `Integer millimeters to micrometers`() {
                assertEquals(1234, fromMicrometers(1234).toMicrometers())
            }
        }
    }
}