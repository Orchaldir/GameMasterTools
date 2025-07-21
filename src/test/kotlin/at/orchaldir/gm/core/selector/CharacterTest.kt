package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Friend
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.selector.character.*
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

private val DAUGHTER = Character(
    DAUGHTER_ID, gender = Gender.Female, origin = BornElement(MOTHER_ID, FATHER_ID),
    relationships = mapOf(SON_ID to setOf(Friend))
)
private val MOTHER = Character(MOTHER_ID, gender = Gender.Female)
private val FATHER = Character(FATHER_ID, gender = Gender.Male)
private val SON = Character(SON_ID, gender = Gender.Male, origin = BornElement(MOTHER_ID, FATHER_ID))
private val FAMILY_STATE = State(
    listOf(
        Storage(
            listOf(
                DAUGHTER,
                MOTHER,
                FATHER,
                SON,
            )
        ),
        Storage(Race(RACE0)),
    )
)

class CharacterTest {

    @Test
    fun `Get others`() {
        assertOthers(DAUGHTER_ID, setOf(FATHER, MOTHER, SON))
        assertOthers(MOTHER_ID, setOf(FATHER, DAUGHTER, SON))
        assertOthers(FATHER_ID, setOf(DAUGHTER, MOTHER, SON))
        assertOthers(SON_ID, setOf(FATHER, DAUGHTER, MOTHER))
    }

    @Nested
    inner class RelativesTest {

        @Test
        fun `Get the parents of an unknown character`() {
            assertTrue(FAMILY_STATE.getParents(CharacterId(100)).isEmpty())
        }

        @Test
        fun `Get the parents`() {
            assertEquals(setOf(MOTHER, FATHER), FAMILY_STATE.getParents(DAUGHTER_ID).toSet())
            assertEquals(setOf(MOTHER, FATHER), FAMILY_STATE.getParents(SON_ID).toSet())
        }

        @Test
        fun `No parents`() {
            assertTrue(FAMILY_STATE.getParents(MOTHER_ID).isEmpty())
        }

        @Test
        fun `Get the children of the mother`() {
            assertEquals(setOf(DAUGHTER, SON), FAMILY_STATE.getChildren(MOTHER_ID).toSet())
        }

        @Test
        fun `Get the children of the father`() {
            assertEquals(setOf(DAUGHTER, SON), FAMILY_STATE.getChildren(FATHER_ID).toSet())
        }

        @Test
        fun `Get the siblings`() {
            assertEquals(setOf(SON), FAMILY_STATE.getSiblings(DAUGHTER_ID).toSet())
            assertEquals(setOf(DAUGHTER), FAMILY_STATE.getSiblings(SON_ID).toSet())
        }

        @Test
        fun `No siblings`() {
            assertTrue(FAMILY_STATE.getSiblings(FATHER_ID).isEmpty())
        }

        @Test
        fun `Get possible fathers`() {
            assertPossibleFathers(DAUGHTER_ID, setOf(SON, FATHER))
            assertPossibleFathers(SON_ID, setOf(FATHER))
            assertPossibleFathers(FATHER_ID, setOf(SON))
            assertPossibleFathers(MOTHER_ID, setOf(FATHER, SON))
        }

        @Test
        fun `Get possible mothers`() {
            assertPossibleMothers(DAUGHTER_ID, setOf(MOTHER))
            assertPossibleMothers(SON_ID, setOf(DAUGHTER, MOTHER))
            assertPossibleMothers(FATHER_ID, setOf(DAUGHTER, MOTHER))
            assertPossibleMothers(MOTHER_ID, setOf(DAUGHTER))
        }

        private fun assertPossibleFathers(id: CharacterId, others: Set<Character>) {
            assertEquals(others, FAMILY_STATE.getPossibleFathers(id).toSet())
        }

        private fun assertPossibleMothers(id: CharacterId, others: Set<Character>) {
            assertEquals(others, FAMILY_STATE.getPossibleMothers(id).toSet())
        }
    }

    @Test
    fun `Get others without relationships`() {
        assertOthersWithoutRelationship(DAUGHTER, setOf(FATHER, MOTHER))
        assertOthersWithoutRelationship(MOTHER, setOf(FATHER, DAUGHTER, SON))
        assertOthersWithoutRelationship(FATHER, setOf(DAUGHTER, MOTHER, SON))
        assertOthersWithoutRelationship(SON, setOf(FATHER, DAUGHTER, MOTHER))
    }

    private fun assertOthers(id: CharacterId, others: Set<Character>) {
        assertEquals(others, FAMILY_STATE.getCharacterStorage().getAllExcept(id).toSet())
    }

    private fun assertOthersWithoutRelationship(character: Character, others: Set<Character>) {
        assertEquals(others, FAMILY_STATE.getOthersWithoutRelationship(character).toSet())
    }
}