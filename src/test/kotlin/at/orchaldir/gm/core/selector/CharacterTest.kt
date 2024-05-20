package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val CHILD_ID0 = CharacterId(0)
private val MOTHER_ID = CharacterId(1)
private val FATHER_ID = CharacterId(2)
private val CHILD_ID1 = CharacterId(3)
private val RACE0 = RaceId(0)

private val CHILD0 = Character(CHILD_ID0, origin = Born(MOTHER_ID, FATHER_ID))
private val MOTHER = Character(MOTHER_ID, gender = Gender.Female)
private val FATHER = Character(FATHER_ID, gender = Gender.Male)
private val CHILD1 = Character(CHILD_ID1, origin = Born(MOTHER_ID, FATHER_ID))

class CharacterTest {
    @Nested
    inner class RelativesTest {
        private val state = State(
            characters = Storage(
                listOf(
                    CHILD0,
                    MOTHER,
                    FATHER,
                    CHILD1,
                )
            ),
            races = Storage(listOf(Race(RACE0)))
        )

        @Test
        fun `Get the parents`() {
            assertEquals(setOf(MOTHER, FATHER), state.getParents(CHILD_ID0).toSet())
            assertEquals(setOf(MOTHER, FATHER), state.getParents(CHILD_ID1).toSet())
        }

        @Test
        fun `No parents`() {
            assertTrue(state.getParents(MOTHER_ID).isEmpty())
        }

        @Test
        fun `Get the children of the mother`() {
            assertEquals(setOf(CHILD0, CHILD1), state.getChildren(MOTHER_ID).toSet())
        }

        @Test
        fun `Get the children of the father`() {
            assertEquals(setOf(CHILD0, CHILD1), state.getChildren(FATHER_ID).toSet())
        }

        @Test
        fun `Get the siblings`() {
            assertEquals(setOf(CHILD1), state.getSiblings(CHILD_ID0).toSet())
            assertEquals(setOf(CHILD0), state.getSiblings(CHILD_ID1).toSet())
        }

        @Test
        fun `No siblings`() {
            assertTrue(state.getSiblings(FATHER_ID).isEmpty())
        }
    }
}