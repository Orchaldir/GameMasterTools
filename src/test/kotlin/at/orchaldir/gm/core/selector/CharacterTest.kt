package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val DAUGHTER_ID = CharacterId(0)
private val MOTHER_ID = CharacterId(1)
private val FATHER_ID = CharacterId(2)
private val SON_ID = CharacterId(3)
private val RACE0 = RaceId(0)

private val DAUGHTER = Character(DAUGHTER_ID, gender = Gender.Female, origin = Born(MOTHER_ID, FATHER_ID))
private val MOTHER = Character(MOTHER_ID, gender = Gender.Female)
private val FATHER = Character(FATHER_ID, gender = Gender.Male)
private val SON = Character(SON_ID, gender = Gender.Male, origin = Born(MOTHER_ID, FATHER_ID))

class CharacterTest {
    @Nested
    inner class RelativesTest {
        private val state = State(
            characters = Storage(
                listOf(
                    DAUGHTER,
                    MOTHER,
                    FATHER,
                    SON,
                )
            ),
            races = Storage(listOf(Race(RACE0)))
        )

        @Test
        fun `Get the parents of an unknown character`() {
            assertTrue(state.getParents(CharacterId(100)).isEmpty())
        }

        @Test
        fun `Get the parents`() {
            assertEquals(setOf(MOTHER, FATHER), state.getParents(DAUGHTER_ID).toSet())
            assertEquals(setOf(MOTHER, FATHER), state.getParents(SON_ID).toSet())
        }

        @Test
        fun `No parents`() {
            assertTrue(state.getParents(MOTHER_ID).isEmpty())
        }

        @Test
        fun `Get the children of the mother`() {
            assertEquals(setOf(DAUGHTER, SON), state.getChildren(MOTHER_ID).toSet())
        }

        @Test
        fun `Get the children of the father`() {
            assertEquals(setOf(DAUGHTER, SON), state.getChildren(FATHER_ID).toSet())
        }

        @Test
        fun `Get the siblings`() {
            assertEquals(setOf(SON), state.getSiblings(DAUGHTER_ID).toSet())
            assertEquals(setOf(DAUGHTER), state.getSiblings(SON_ID).toSet())
        }

        @Test
        fun `No siblings`() {
            assertTrue(state.getSiblings(FATHER_ID).isEmpty())
        }

        @Test
        fun `Get possible fathers`() {
            assertEquals(setOf(SON, FATHER), state.getPossibleFathers(DAUGHTER_ID).toSet())
            assertEquals(setOf(FATHER), state.getPossibleFathers(SON_ID).toSet())
            assertEquals(setOf(SON), state.getPossibleFathers(FATHER_ID).toSet())
            assertEquals(setOf(FATHER, SON), state.getPossibleFathers(MOTHER_ID).toSet())
        }

        @Test
        fun `Get possible mothers`() {
            assertEquals(setOf(MOTHER), state.getPossibleMothers(DAUGHTER_ID).toSet())
            assertEquals(setOf(DAUGHTER, MOTHER), state.getPossibleMothers(SON_ID).toSet())
            assertEquals(setOf(DAUGHTER, MOTHER), state.getPossibleMothers(FATHER_ID).toSet())
            assertEquals(setOf(DAUGHTER), state.getPossibleMothers(MOTHER_ID).toSet())
        }
    }
}