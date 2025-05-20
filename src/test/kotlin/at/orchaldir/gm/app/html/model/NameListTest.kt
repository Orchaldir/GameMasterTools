package at.orchaldir.gm.app.html.model


import at.orchaldir.gm.app.html.util.name.parseNames
import at.orchaldir.gm.core.model.util.name.Name
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NameListTest {

    private val A = Name.init("A")
    private val B = Name.init("B")
    private val result = listOf(A, B)

    @Test
    fun `Trim the names`() {
        assertEquals(result, parseNames("  A  \n  B  "))
    }

    @Test
    fun `Filter empty names`() {
        assertEquals(result, parseNames("A,  ,B"))
    }

    @Test
    fun `Split at comma`() {
        assertEquals(result, parseNames("A , B"))
    }

    @Test
    fun `Split at dot`() {
        assertEquals(result, parseNames(" A.B "))
    }

    @Test
    fun `Split at semicolon`() {
        assertEquals(result, parseNames("  A;B  "))
    }

    @Test
    fun `Capitalize the first letter`() {
        assertEquals(listOf(Name.init("Name")), parseNames(" name"))
    }
}