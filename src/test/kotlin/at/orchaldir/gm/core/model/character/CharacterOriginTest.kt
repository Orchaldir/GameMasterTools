package at.orchaldir.gm.core.model.character

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private val MOTHER = CharacterId(0)
private val FATHER = CharacterId(1)
private val OTHER = CharacterId(2)
private val BORN = Born(MOTHER, FATHER)


class CharacterOriginTest {

    @Test
    fun `The father is a parent`() {
        assertTrue(BORN.isParent(FATHER))
    }

    @Test
    fun `The mother is a parent`() {
        assertTrue(BORN.isParent(MOTHER))
    }

    @Test
    fun `Others are no parent`() {
        assertFalse(BORN.isParent(OTHER))
    }

}