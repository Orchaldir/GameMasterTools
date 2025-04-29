package at.orchaldir.gm.core.model.name

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class NameTest {

    @Test
    fun `Test a valid name`() {
        assertEquals("Test", Name.init("Test").text)
    }

    @Test
    fun `Trim a name`() {
        assertEquals("Test", Name.init(" Test  ").text)
    }

    @Test
    fun `Capitalize name`() {
        assertEquals("Test", Name.init("test").text)
    }

}