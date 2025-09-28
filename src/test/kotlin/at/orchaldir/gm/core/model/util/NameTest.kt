package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.parseNames
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NameListTest {

    private val A = Name.init("A")
    private val B = Name.init("B")
    private val result = listOf(A, B)

    @Test
    fun `Trim the names`() {
        Assertions.assertEquals(result, parseNames("  A  \n  B  "))
    }

    @Test
    fun `Filter empty names`() {
        Assertions.assertEquals(result, parseNames("A,  ,B"))
    }

    @Test
    fun `Split at comma`() {
        Assertions.assertEquals(result, parseNames("A , B"))
    }

    @Test
    fun `Split at dot`() {
        Assertions.assertEquals(result, parseNames(" A.B "))
    }

    @Test
    fun `Split at semicolon`() {
        Assertions.assertEquals(result, parseNames("  A;B  "))
    }

    @Test
    fun `Capitalize the first letter`() {
        Assertions.assertEquals(listOf(Name.init("Name")), parseNames(" name"))
    }
}