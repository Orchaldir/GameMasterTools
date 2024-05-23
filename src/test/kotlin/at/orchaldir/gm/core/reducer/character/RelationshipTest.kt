package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.AddRelationship
import at.orchaldir.gm.core.action.RemoveRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Enemy
import at.orchaldir.gm.core.model.character.InterpersonalRelationship.Friend
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)

class RelationshipTest {

    @Nested
    inner class AddRelationshipTest {

        private val action = AddRelationship(ID0, ID1, Friend)

        @Test
        fun `Add a relationship`() {
            val state = State(
                characters = Storage(listOf(Character(ID0), Character(ID1))),
            )

            val result = ADD_RELATIONSHIP.invoke(state, action).first

            assertEquals(mapOf(ID1 to setOf(Friend)), result.characters.getOrThrow(ID0).relationships)
            assertEquals(mapOf(ID0 to setOf(Friend)), result.characters.getOrThrow(ID1).relationships)
        }

        @Test
        fun `Cannot add relationship to unknown character`() {
            val state = State(characters = Storage(listOf(Character(ID1))))

            assertFailsWith<IllegalArgumentException> { ADD_RELATIONSHIP.invoke(state, action) }
        }

        @Test
        fun `Cannot add relationship to unknown other`() {
            val state = State(characters = Storage(listOf(Character(ID0))))

            assertFailsWith<IllegalArgumentException> { ADD_RELATIONSHIP.invoke(state, action) }
        }

        @Test
        fun `Cannot add relationship to the same character`() {
            val state = State(characters = Storage(listOf(Character(ID0))))

            assertFailsWith<IllegalArgumentException> {
                ADD_RELATIONSHIP.invoke(
                    state,
                    AddRelationship(ID0, ID0, Friend)
                )
            }
        }
    }

    @Nested
    inner class RemoveRelationshipTest {

        @Test
        fun `Remove a relationship`() {
            val relationships = setOf(Friend, Enemy)
            val state = State(
                characters = Storage(
                    listOf(
                        Character(ID0, relationships = mapOf(ID1 to relationships)),
                        Character(ID1, relationships = mapOf(ID0 to relationships)),
                    )
                )
            )
            val action = RemoveRelationships(ID0, mapOf(ID1 to setOf(Friend)))

            val result = REMOVE_RELATIONSHIPS.invoke(state, action).first

            assertEquals(mapOf(ID1 to setOf(Enemy)), result.characters.getOrThrow(ID0).relationships)
            assertEquals(mapOf(ID0 to setOf(Enemy)), result.characters.getOrThrow(ID1).relationships)
        }
    }

}