package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Friend
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Lover
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)

class RelationshipTest {

    private val action = UpdateRelationships(ID0, mapOf(ID1 to setOf(Friend)))

    @Test
    fun `Set first relationships`() {
        val state = State(
            characters = Storage(listOf(Character(ID0), Character(ID1))),
        )

        val result = UPDATE_RELATIONSHIPS.invoke(state, action).first

        assertEquals(mapOf(ID1 to setOf(Friend)), result.characters.getOrThrow(ID0).relationships)
        assertEquals(mapOf(ID0 to setOf(Friend)), result.characters.getOrThrow(ID1).relationships)
    }

    @Test
    fun `Update relationships`() {
        val state = createStateWithExistingRelationships(setOf(Friend))

        val new = setOf(Friend, Lover)
        val action = UpdateRelationships(ID0, mapOf(ID1 to new))
        val result = UPDATE_RELATIONSHIPS.invoke(state, action).first

        assertEquals(mapOf(ID1 to new), result.characters.getOrThrow(ID0).relationships)
        assertEquals(mapOf(ID0 to new), result.characters.getOrThrow(ID1).relationships)
    }

    @Test
    fun `Remove relationships`() {
        val state = createStateWithExistingRelationships(setOf(Friend, Lover))

        val new = setOf(Friend)
        val action = UpdateRelationships(ID0, mapOf(ID1 to new))
        val result = UPDATE_RELATIONSHIPS.invoke(state, action).first

        assertEquals(mapOf(ID1 to new), result.characters.getOrThrow(ID0).relationships)
        assertEquals(mapOf(ID0 to new), result.characters.getOrThrow(ID1).relationships)
    }

    @Test
    fun `Cannot add relationship to unknown character`() {
        val state = State(characters = Storage(listOf(Character(ID1))))

        assertFailsWith<IllegalArgumentException> { UPDATE_RELATIONSHIPS.invoke(state, action) }
    }

    @Test
    fun `Cannot add relationship to unknown other`() {
        val state = State(characters = Storage(listOf(Character(ID0))))

        assertFailsWith<IllegalArgumentException> { UPDATE_RELATIONSHIPS.invoke(state, action) }
    }

    @Test
    fun `Cannot add relationship to the same character`() {
        val state = State(characters = Storage(listOf(Character(ID0))))

        assertFailsWith<IllegalArgumentException> {
            UPDATE_RELATIONSHIPS.invoke(
                state,
                UpdateRelationships(ID0, mapOf(ID0 to setOf(Friend)))
            )
        }
    }

    private fun createStateWithExistingRelationships(relationships: Set<InterpersonalRelationship>): State {
        val state = State(
            characters = Storage(
                listOf(
                    Character(ID0, relationships = mapOf(ID1 to relationships)),
                    Character(ID1, relationships = mapOf(ID0 to relationships)),
                )
            )
        )
        return state
    }

}