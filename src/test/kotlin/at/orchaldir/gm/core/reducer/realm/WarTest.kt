package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.util.*
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
            Storage(listOf(Realm(REALM_ID_0), Realm(REALM_ID_1))),
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
            fun `Reference has invalid type!`() {
                assertSides(listOf(WarParticipant(NoReference)), "Reference has invalid type None!")
            }

            @Test
            fun `Realm must exist`() {
                val participant = WarParticipant(RealmReference(UNKNOWN_REALM_ID))

                assertSides(listOf(participant), "Requires unknown Participant (Realm 99)!")
            }

            @Test
            fun `Cannot have the same participant twice`() {
                val participant = WarParticipant(RealmReference(REALM_ID_0))

                assertSides(listOf(participant, participant), "Cannot have Participant Realm 0 multiple times!")
            }

            @Test
            fun `The participant's side must exist`() {
                val participant = WarParticipant(RealmReference(REALM_ID_0), History(1))

                assertSides(listOf(participant), "The side '1' doesn't exist!")
            }

            @Test
            fun `The participant cannot change side before the war's start`() {
                val history = History(1, HistoryEntry(null, DAY0))
                val participant = WarParticipant(RealmReference(REALM_ID_0), history)
                val war = War(WAR_ID_0, startDate = DAY1, sides = sides, participants = listOf(participant))
                val action = UpdateWar(war)

                assertIllegalArgument("1.previous side's until is too early!") {
                    REDUCER.invoke(STATE, action)
                }
            }

            @Test
            fun `The participant cannot have the same side 2 times in a row`() {
                testSameSideTwice(0)
            }

            @Test
            fun `The participant cannot have no side 2 times in a row`() {
                testSameSideTwice(null)
            }

            private fun testSameSideTwice(side: Int?) {
                val history = History(side, HistoryEntry(side, DAY1))
                val participant = WarParticipant(RealmReference(REALM_ID_0), history)

                assertSides(listOf(participant), "Cannot have the same side 2 times in a row!")
            }

            fun assertSides(participants: List<WarParticipant>, text: String) {
                val war = War(WAR_ID_0, sides = sides, participants = participants)
                val action = UpdateWar(war)

                assertIllegalArgument(text) { REDUCER.invoke(STATE, action) }
            }
        }

        @Nested
        inner class SidesTest {
            @Test
            fun `A war with no sides is valid`() {
                val war = War(WAR_ID_0)
                val action = UpdateWar(war)

                REDUCER.invoke(STATE, action)
            }

            @Test
            fun `Cannot have the same color twice`() {
                assertSides(
                    listOf(WarSide(Color.Red), WarSide(Color.Red)),
                    "Multiple sides cannot have the same color!"
                )
            }

            @Test
            fun `Cannot have the same name twice`() {
                assertSides(
                    listOf(WarSide.init(Color.Red, "A"), WarSide.init(Color.Blue, "A")),
                    "Multiple sides cannot have the same name!",
                )
            }

            fun assertSides(sides: List<WarSide>, text: String) {
                val war = War(WAR_ID_0, sides = sides)
                val action = UpdateWar(war)

                assertIllegalArgument(text) { REDUCER.invoke(STATE, action) }
            }
        }

        @Nested
        inner class StatusTest {
            @Test
            fun `The catastrophe that interrupted the war must exist`() {
                assertResult(InterruptedByCatastrophe(UNKNOWN_CATASTROPHE_ID), "Requires unknown Catastrophe 99!")
            }

            @Test
            fun `The treaty must exist`() {
                assertResult(Peace(UNKNOWN_TREATY_ID), "Requires unknown Treaty 99!")
            }

            @Test
            fun `The victorious side must exist`() {
                assertResult(TotalVictory(1), "The result's side '1' doesn't exist!")
            }

            @Test
            fun `The surrendering side must exist`() {
                assertResult(Surrender(1), "The result's side '1' doesn't exist!")
            }

            fun assertResult(result: WarResult, text: String) {
                val war = War(WAR_ID_0, status = FinishedWar(result))
                val action = UpdateWar(war)

                assertIllegalArgument(text) { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Update a war`() {
            val war = War(
                WAR_ID_0,
                NAME,
                sides = listOf(WarSide.init(Color.Red, "A"), WarSide.init(Color.Blue, "B")),
                participants = listOf(
                    WarParticipant(RealmReference(REALM_ID_0)),
                    WarParticipant(RealmReference(REALM_ID_1))
                ),
                status = FinishedWar(TotalVictory(1)),
            )
            val action = UpdateWar(war)

            assertEquals(war, REDUCER.invoke(STATE, action).first.getWarStorage().get(WAR_ID_0))
        }
    }

}