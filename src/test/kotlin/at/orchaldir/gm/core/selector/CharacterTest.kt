package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Friend
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
    DAUGHTER_ID, gender = Gender.Female, origin = Born(MOTHER_ID, FATHER_ID),
    relationships = mapOf(SON_ID to setOf(Friend))
)
private val MOTHER = Character(MOTHER_ID, gender = Gender.Female)
private val FATHER = Character(FATHER_ID, gender = Gender.Male)
private val SON = Character(SON_ID, gender = Gender.Male, origin = Born(MOTHER_ID, FATHER_ID))
private val FAMILY_STATE = State(
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

class CharacterTest {

    @Test
    fun `Get others`() {
        assertEquals(setOf(FATHER, MOTHER, SON), FAMILY_STATE.getOthers(DAUGHTER_ID).toSet())
        assertEquals(setOf(FATHER, DAUGHTER, SON), FAMILY_STATE.getOthers(MOTHER_ID).toSet())
        assertEquals(setOf(DAUGHTER, MOTHER, SON), FAMILY_STATE.getOthers(FATHER_ID).toSet())
        assertEquals(setOf(FATHER, DAUGHTER, MOTHER), FAMILY_STATE.getOthers(SON_ID).toSet())
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
            assertEquals(setOf(SON, FATHER), FAMILY_STATE.getPossibleFathers(DAUGHTER_ID).toSet())
            assertEquals(setOf(FATHER), FAMILY_STATE.getPossibleFathers(SON_ID).toSet())
            assertEquals(setOf(SON), FAMILY_STATE.getPossibleFathers(FATHER_ID).toSet())
            assertEquals(setOf(FATHER, SON), FAMILY_STATE.getPossibleFathers(MOTHER_ID).toSet())
        }

        @Test
        fun `Get possible mothers`() {
            assertEquals(setOf(MOTHER), FAMILY_STATE.getPossibleMothers(DAUGHTER_ID).toSet())
            assertEquals(setOf(DAUGHTER, MOTHER), FAMILY_STATE.getPossibleMothers(SON_ID).toSet())
            assertEquals(setOf(DAUGHTER, MOTHER), FAMILY_STATE.getPossibleMothers(FATHER_ID).toSet())
            assertEquals(setOf(DAUGHTER), FAMILY_STATE.getPossibleMothers(MOTHER_ID).toSet())
        }
    }

    @Test
    fun `Get others without relationships`() {
        assertEquals(setOf(FATHER, MOTHER), FAMILY_STATE.getOthersWithoutRelationship(DAUGHTER).toSet())
        assertEquals(setOf(FATHER, DAUGHTER, SON), FAMILY_STATE.getOthersWithoutRelationship(MOTHER).toSet())
        assertEquals(setOf(DAUGHTER, MOTHER, SON), FAMILY_STATE.getOthersWithoutRelationship(FATHER).toSet())
        assertEquals(setOf(FATHER, DAUGHTER, MOTHER), FAMILY_STATE.getOthersWithoutRelationship(SON).toSet())
    }
}