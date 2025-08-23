package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.Gender.Genderless
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.organization.Organization
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyParticipant
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTest {

    private val LANGUAGES = mapOf(LANGUAGE_ID_0 to ComprehensionLevel.Native)
    private val state = State(
        listOf(
            Storage(listOf(Character(CHARACTER_ID_0))),
            Storage(listOf(Language(LANGUAGE_ID_0)))
        )
    )

    @Nested
    inner class CreateTest {

        @Test
        fun `Create another character`() {
            val character0 = Character(CHARACTER_ID_0)
            val character1 = Character(CHARACTER_ID_1, birthDate = Day(0))
            val state = State(Storage(listOf(character0)))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(2, characters.getSize())
            assertEquals(character0, characters.getOrThrow(CHARACTER_ID_0))
            assertEquals(character1, characters.getOrThrow(CHARACTER_ID_1))
        }

        @Test
        fun `Default birthday is today`() {
            val today = Day(42)
            val state = State(data = Data(time = Time(currentDate = today)))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(today, characters.getOrThrow(CHARACTER_ID_0).birthDate)
        }
    }

    @Nested
    inner class DeleteTest {

        private val action = DeleteCharacter(CHARACTER_ID_0)
        private val createdByCharacter = CharacterReference(CHARACTER_ID_0)

        @Test
        fun `Can delete an existing character`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getCharacterStorage().getSize())
        }

        // see CreatorTest for other elements
        @Test
        fun `Cannot delete a character that created another element`() {
            val building = Building(BUILDING_ID_0, builder = createdByCharacter)
            val newState = state.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        // see OwnershipTest for other elements
        @Test
        fun `Cannot delete a character that owns another element`() {
            val ownership = History<Reference>(CharacterReference(CHARACTER_ID_0))
            val building = Building(BUILDING_ID_0, ownership = ownership)
            val newState = state.updateStorage(Storage(building))

            assertIllegalArgument("Cannot delete Character 0, because of owned elements (Building)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a member of an organization`() {
            val organization = Organization(ORGANIZATION_ID_0, members = mapOf(CHARACTER_ID_0 to History(0)))
            val newState = state.updateStorage(Storage(organization))

            assertIllegalArgument("Cannot delete Character 0, because he is a member of an organization!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a character that signed a treaty`() {
            val participant = TreatyParticipant(REALM_ID_0, CHARACTER_ID_0)
            val treaty = Treaty(TREATY_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(Storage(treaty))

            assertIllegalArgument("Cannot delete Character 0, because of created elements (Treaty)!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a character that led a battle`() {
            val participant = BattleParticipant(REALM_ID_0, CHARACTER_ID_0)
            val treaty = Battle(BATTLE_ID_0, participants = listOf(participant))
            val newState = state.updateStorage(Storage(treaty))

            assertIllegalArgument("Cannot delete Character 0, because of a battle!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Nested
        inner class DeleteFamilyMemberTest {

            private val state = State(
                Storage(
                    listOf(
                        Character(CHARACTER_ID_0, origin = BornElement(CHARACTER_ID_1, CHARACTER_ID_2)),
                        Character(CHARACTER_ID_1),
                        Character(CHARACTER_ID_2)
                    )
                ),
            )

            @Test
            fun `Cannot delete a character with parents`() {
                assertIllegalArgument("Cannot delete Character 0, because he has parents!") {
                    REDUCER.invoke(state, DeleteCharacter(CHARACTER_ID_0))
                }
            }

            @Test
            fun `Cannot delete a father`() {
                assertIllegalArgument("Cannot delete Character 2, because he has children!") {
                    REDUCER.invoke(state, DeleteCharacter(CHARACTER_ID_2))
                }
            }

            @Test
            fun `Cannot delete a mother`() {
                assertIllegalArgument("Cannot delete Character 1, because he has children!") {
                    REDUCER.invoke(state, DeleteCharacter(CHARACTER_ID_1))
                }
            }
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Character 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        val STATE = State(
            listOf(
                Storage(CALENDAR0),
                Storage(Character(CHARACTER_ID_0)),
                Storage(Business(BUSINESS_ID_0)),
                Storage(Culture(CULTURE_ID_0)),
                Storage(Language(LANGUAGE_ID_0)),
                Storage(Job(JOB_ID_0)),
                Storage(PersonalityTrait(PERSONALITY_ID_0)),
                Storage(listOf(Race(RACE_ID_0), Race(RACE_ID_1)))
            )
        )

        @Test
        fun `Do not overwrite languages`() {
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, languages = LANGUAGES)))
            val action =
                UpdateCharacter(
                    Character(
                        CHARACTER_ID_0,
                        Mononym(NAME0),
                        RACE_ID_1,
                        Gender.Male,
                        personality = setOf(PERSONALITY_ID_0)
                    )
                )

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                Character(
                    CHARACTER_ID_0,
                    Mononym(NAME0),
                    RACE_ID_1,
                    Gender.Male,
                    personality = setOf(PERSONALITY_ID_0),
                    languages = LANGUAGES,
                ),
                result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
            )
        }

        @Nested
        inner class VitalStatusTest {

            @Test
            fun `A character can be alive`() {
                testValidStatus(Alive)
            }

            @Test
            fun `A character can die`() {
                testValidStatus(Dead(DAY2))
            }

            @Test
            fun `A character cannot be abandoned`() {
                testInvalidStatus(Abandoned(DAY2))
            }

            @Test
            fun `A character cannot be destroyed`() {
                testInvalidStatus(Destroyed(DAY2))
            }

            private fun testValidStatus(status: VitalStatus) {
                val character = Character(CHARACTER_ID_0, birthDate = DAY0, vitalStatus = status)
                val action = UpdateCharacter(character)

                REDUCER.invoke(STATE, action);
            }

            private fun testInvalidStatus(status: VitalStatus) {
                val character = Character(CHARACTER_ID_0, birthDate = DAY0, vitalStatus = status)
                val action = UpdateCharacter(character)

                assertIllegalArgument("Invalid vital status ${status.getType()}!") { REDUCER.invoke(STATE, action) }
            }

        }

        @Nested
        inner class SexualOrientationTest {
            @Test
            fun `All sexual orientations are valid for males`() {
                assertValidSexualOrientations(Gender.Male, SexualOrientation.entries)
            }

            @Test
            fun `All sexual orientations are valid for females`() {
                assertValidSexualOrientations(Gender.Female, SexualOrientation.entries)
            }

            @Test
            fun `Some sexual orientations are valid for genderless`() {
                assertValidSexualOrientations(Genderless, SEXUAL_ORIENTATION_FOR_GENDERLESS)
            }

            @Test
            fun `Some sexual orientations are invalid for genderless`() {
                val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, gender = Genderless)))
                val invalidList = SexualOrientation.entries - SEXUAL_ORIENTATION_FOR_GENDERLESS

                invalidList.forEach { sexuality ->
                    val character = Character(CHARACTER_ID_0, gender = Genderless, sexuality = sexuality)
                    val action = UpdateCharacter(character)

                    assertIllegalArgument("Sexual orientation $sexuality is invalid for gender Genderless!") {
                        REDUCER.invoke(state, action)
                    }
                }
            }

            private fun assertValidSexualOrientations(
                gender: Gender,
                validList: Collection<SexualOrientation>,
            ) {
                val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, gender = gender)))

                validList.forEach { sexuality ->
                    val character = Character(CHARACTER_ID_0, gender = gender, sexuality = sexuality)
                    val action = UpdateCharacter(character)

                    val result = REDUCER.invoke(state, action).first

                    assertEquals(character, result.getCharacterStorage().getOrThrow(CHARACTER_ID_0))
                }
            }
        }

        // See more in OriginTest
        @Nested
        inner class BornTest {
            private val state = STATE.updateStorage(
                Storage(
                    listOf(
                        Character(CHARACTER_ID_0),
                        Character(CHARACTER_ID_1, gender = Gender.Male),
                        Character(CHARACTER_ID_2, gender = Gender.Female)
                    )
                )
            )

            @Test
            fun `Cannot be born in the future`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, birthDate = Day(1)))

                assertIllegalArgument("Date (Birthday) is in the future!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Check origin with Unknown mother`() {
                val action =
                    UpdateCharacter(
                        Character(
                            CHARACTER_ID_0,
                            origin = BornElement(UNKNOWN_CHARACTER_ID, null)
                        )
                    )

                assertIllegalArgument("Requires unknown parent Character 99!") { REDUCER.invoke(state, action) }
            }

        }


        @Nested
        inner class BeliefStatusTest {

            @Test
            fun `Cannot believe in an unknown god`() {
                val action =
                    UpdateCharacter(Character(CHARACTER_ID_0, beliefStatus = History(WorshipOfGod(UNKNOWN_GOD_ID))))

                assertIllegalArgument("The belief's god 99 doesn't exist!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Nested
        inner class HousingStatusTest {

            @Test
            fun `Cannot use unknown building as home`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, housingStatus = History(InHouse(BUILDING_ID_0))))

                assertIllegalArgument("Requires unknown home!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Cannot update unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0))

            assertIllegalArgument("Requires unknown Character 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown culture`() {
            val state = STATE.removeStorage(CULTURE_ID_0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, culture = CULTURE_ID_0))

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown title`() {
            val action = UpdateCharacter(Character(CHARACTER_ID_0, title = TITLE_ID_0))

            assertIllegalArgument("Requires unknown Title 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown personality trait`() {
            val state = STATE.removeStorage(PERSONALITY_ID_0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, personality = setOf(PERSONALITY_ID_0)))

            assertIllegalArgument("Requires unknown Personality Trait 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = STATE.removeStorage(RACE_ID_0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, race = RACE_ID_0))

            assertIllegalArgument("Requires unknown Race 0!") { REDUCER.invoke(state, action) }
        }
    }

}