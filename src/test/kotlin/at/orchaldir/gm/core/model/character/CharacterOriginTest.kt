package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.util.origin.BornElement
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private const val MOTHER = 0
private const val FATHER = 1
private const val OTHER = 2
private val BORN = BornElement(MOTHER, FATHER)


class CharacterOriginTest {

    @Test
    fun `The father is a parent`() {
        assertTrue(BORN.isChildOf(FATHER))
    }

    @Test
    fun `The mother is a parent`() {
        assertTrue(BORN.isChildOf(MOTHER))
    }

    @Test
    fun `Others are no parent`() {
        assertFalse(BORN.isChildOf(OTHER))
    }

}