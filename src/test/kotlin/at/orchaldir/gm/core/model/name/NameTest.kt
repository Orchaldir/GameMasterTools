package at.orchaldir.gm.core.model.name

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class NameTest {

    @Test
    fun `Test a valid name`() {
        test("Test")
    }

    @Test
    fun `Trim a name`() {
        test(" Test  ")
    }

    @Test
    fun `Capitalize name`() {
        test("test")
    }

    private fun test(name: String) {
        assertEquals("Test", Name.init(name).text)
    }

}