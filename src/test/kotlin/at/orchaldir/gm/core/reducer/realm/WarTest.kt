package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathInWar
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.RealmReference
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Realm(REALM_ID_0)),
            Storage(War(WAR_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteWar(WAR_ID_0)

        @Test
        fun `Can delete an existing war`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getWarStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteWar(UNKNOWN_WAR_ID)

            assertIllegalArgument("Requires unknown War 99!") { REDUCER.invoke(STATE, action) }
        }

        // see VitalStatusTest for other elements
        @Test
        fun `Cannot delete a war that killed a character`() {
            val dead = Dead(DAY0, DeathInWar(WAR_ID_0))
            val character = Character(CHARACTER_ID_0, vitalStatus = dead)
            val newState = STATE.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete War 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a war with a battle`() {
            val battle = Battle(BATTLE_ID_0, war = WAR_ID_0)
            val newState = STATE.updateStorage(Storage(battle))

            assertIllegalArgument("Cannot delete War 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateWar(War(UNKNOWN_WAR_ID))

            assertIllegalArgument("Requires unknown War 99!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class ParticipantsTest {
            val sides = listOf(WarSide(Color.Red))

            @Test
            fun `Realm must exist`() {
                val participant = WarParticipant(RealmReference(UNKNOWN_REALM_ID))
                val war = War(WAR_ID_0, sides = sides, participants = listOf(participant))
                val action = UpdateWar(war)

                assertIllegalArgument("Cannot use an unknown Realm 99 as Participant!") {
                    REDUCER.invoke(STATE, action)
                }
            }

            @Test
            fun `Cannot have the same participant twice`() {
                val participant = WarParticipant(RealmReference(REALM_ID_0))
                val war = War(WAR_ID_0, sides = sides, participants = listOf(participant, participant))
                val action = UpdateWar(war)

                assertIllegalArgument("Cannot have Participant Realm 0 multiple times!") {
                    REDUCER.invoke(STATE, action)
                }
            }

            @Test
            fun `The participants side must exist`() {
                val participant = WarParticipant(RealmReference(REALM_ID_0), History(1))
                val war = War(WAR_ID_0, sides = sides, participants = listOf(participant))
                val action = UpdateWar(war)

                assertIllegalArgument("Cannot have Participant Realm 0 multiple times!") {
                    REDUCER.invoke(STATE, action)
                }
            }
        }

        @Test
        fun `Catastrophe that interrupted the war must exist`() {
            val status = FinishedWar(InterruptedByCatastrophe(UNKNOWN_CATASTROPHE_ID), DAY0)
            val war = War(WAR_ID_0, status = status)
            val action = UpdateWar(war)

            assertIllegalArgument("Requires unknown Catastrophe 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Treaty must exist`() {
            val war = War(WAR_ID_0, status = FinishedWar(Peace(UNKNOWN_TREATY_ID), DAY0))
            val action = UpdateWar(war)

            assertIllegalArgument("Requires unknown Treaty 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update a war`() {
            val war = War(WAR_ID_0, NAME)
            val action = UpdateWar(war)

            assertEquals(war, REDUCER.invoke(STATE, action).first.getWarStorage().get(WAR_ID_0))
        }
    }

}