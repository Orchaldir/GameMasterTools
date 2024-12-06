package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val CULTURE0 = CultureId(0)
private val LANGUAGE0 = LanguageId(0)
private val LANGUAGES = mapOf(LANGUAGE0 to ComprehensionLevel.Native)
private val PERSONALITY0 = PersonalityTraitId(0)
private val RACE0 = RaceId(0)
private val RACE1 = RaceId(1)
private val OWNER = History<Owner>(OwnedByCharacter(CHARACTER_ID_0))
private val PREVIOUS_OWNER = History(UndefinedOwner, listOf(HistoryEntry(OwnedByCharacter(CHARACTER_ID_0), Day(0))))

class CharacterTest {

    @Nested
    inner class CreateTest {

        @Test
        fun `Create another character`() {
            val character0 = Character(CHARACTER_ID_0)
            val character1 = Character(CHARACTER_ID_1)
            val state = State(Storage(listOf(character0)))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(2, characters.getSize())
            assertEquals(character0, characters.getOrThrow(CHARACTER_ID_0))
            assertEquals(character1, characters.getOrThrow(CHARACTER_ID_1))
        }

        @Test
        fun `Default birthday is today`() {
            val today = Day(42)
            val state = State(time = Time(currentDate = today))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(today, characters.getOrThrow(CHARACTER_ID_0).birthDate)
        }
    }

    @Nested
    inner class DeleteTest {

        private val action = DeleteCharacter(CHARACTER_ID_0)

        @Test
        fun `Can delete an existing character`() {
            val state = State(Storage(listOf(Character(CHARACTER_ID_0))))

            assertEquals(0, REDUCER.invoke(state, action).first.getCharacterStorage().getSize())
        }

        @Test
        fun `Cannot delete an inventor`() {
            val origin = InventedLanguage(CreatedByCharacter(CHARACTER_ID_0), DAY0)
            val state = State(
                listOf(
                    Storage(listOf(Character(CHARACTER_ID_0))),
                    Storage(listOf(Language(LANGUAGE0, origin = origin)))
                )
            )

            assertIllegalArgument("Cannot delete character 0, because of invented languages!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a builder`() {
            val state = createState(Building(BUILDING_ID_0, builder = CreatedByCharacter(CHARACTER_ID_0)))

            assertIllegalArgument("Cannot delete character 0, because of built buildings!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a town founder`() {
            val state = createState(Town(TOWN_ID_0, founder = CreatedByCharacter(CHARACTER_ID_0)))

            assertIllegalArgument("Cannot delete character 0, because of founded towns!") {
                REDUCER.invoke(state, action)
            }
        }

        @Nested
        inner class BuildingOwnerTest {

            @Test
            fun `Cannot delete a building owner`() {
                val state = createState(Building(BUILDING_ID_0, ownership = OWNER))

                assertIllegalArgument("Cannot delete character 0, because he owns buildings!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous building owner`() {
                val state = createState(Building(BUILDING_ID_0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete character 0, because he previously owned buildings!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        @Nested
        inner class BusinessOwnerTest {

            @Test
            fun `Cannot delete a business owner`() {
                val state = createState(Business(BUSINESS_ID_0, ownership = OWNER))

                assertIllegalArgument("Cannot delete character 0, because he owns businesses!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `Cannot delete a previous business owner`() {
                val state = createState(Business(BUSINESS_ID_0, ownership = PREVIOUS_OWNER))

                assertIllegalArgument("Cannot delete character 0, because he previously owned businesses!") {
                    REDUCER.invoke(state, action)
                }
            }
        }

        private fun <ID : Id<ID>, ELEMENT : Element<ID>> createState(element: ELEMENT) = State(
            listOf(
                Storage(listOf(Character(CHARACTER_ID_0))),
                Storage(listOf(element))
            )
        )

        @Nested
        inner class DeleteFamilyMemberTest {

            private val state = State(
                Storage(
                    listOf(
                        Character(CHARACTER_ID_0, origin = Born(CHARACTER_ID_1, CHARACTER_ID_2)),
                        Character(CHARACTER_ID_1),
                        Character(CHARACTER_ID_2)
                    )
                ),
            )

            @Test
            fun `Cannot delete a character with parents`() {
                assertIllegalArgument("Cannot delete character 0, because he has parents!") {
                    REDUCER.invoke(state, DeleteCharacter(CHARACTER_ID_0))
                }
            }

            @Test
            fun `Cannot delete a father`() {
                assertIllegalArgument("Cannot delete character 2, because he has children!") {
                    REDUCER.invoke(state, DeleteCharacter(CHARACTER_ID_2))
                }
            }

            @Test
            fun `Cannot delete a mother`() {
                assertIllegalArgument("Cannot delete character 1, because he has children!") {
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
                Storage(Culture(CULTURE0)),
                Storage(Language(LANGUAGE0)),
                Storage(Job(JOB_ID_0)),
                Storage(PersonalityTrait(PERSONALITY0)),
                Storage(listOf(Race(RACE0), Race(RACE1)))
            )
        )

        @Test
        fun `Do not overwrite languages`() {
            val state = STATE.updateStorage(Storage(Character(CHARACTER_ID_0, languages = LANGUAGES)))
            val action =
                UpdateCharacter(
                    Character(
                        CHARACTER_ID_0,
                        Mononym("Test"),
                        RACE1,
                        Gender.Male,
                        personality = setOf(PERSONALITY0)
                    )
                )

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                Character(
                    CHARACTER_ID_0,
                    Mononym("Test"),
                    RACE1,
                    Gender.Male,
                    personality = setOf(PERSONALITY0),
                    languages = LANGUAGES,
                ),
                result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
            )
        }

        @Nested
        inner class BornTest {
            private val UNKNOWN = CharacterId(3)
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
            fun `Valid parents`() {
                val character = Character(CHARACTER_ID_0, origin = Born(CHARACTER_ID_2, CHARACTER_ID_1))
                val action = UpdateCharacter(character)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    character,
                    result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
                )
            }

            @Test
            fun `Cannot be born in the future`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, birthDate = Day(1)))

                assertIllegalArgument("Character is born in the future!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown mother`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, origin = Born(UNKNOWN, CHARACTER_ID_1)))

                assertIllegalArgument("Cannot use an unknown mother 3!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Mother is not female`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, origin = Born(CHARACTER_ID_1, CHARACTER_ID_1)))

                assertIllegalArgument("Mother 1 is not female!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown father`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, origin = Born(CHARACTER_ID_2, UNKNOWN)))

                assertIllegalArgument("Cannot use an unknown father 3!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Father is not male`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, origin = Born(CHARACTER_ID_2, CHARACTER_ID_2)))

                assertIllegalArgument("Father 2 is not male!") { REDUCER.invoke(state, action) }
            }

        }

        @Nested
        inner class CauseOfDeathTest {

            private val state = STATE.updateStorage(
                listOf(
                    Storage(
                        listOf(
                            Character(CHARACTER_ID_0),
                            Character(CHARACTER_ID_1),
                        )
                    ),
                    Storage(listOf(Culture(CULTURE0))),
                    Storage(listOf(Race(RACE0))),
                )
            ).copy(time = Time(currentDate = Day(10)))

            @Test
            fun `Died from accident`() {
                testDie(Day(5), Accident)
            }

            @Test
            fun `Cannot die from accident in the future`() {
                testFailToDie(Day(11), Accident)
            }

            @Test
            fun `Cannot die from accident before its origin`() {
                testFailToDie(Day(-1), Accident)
            }

            @Test
            fun `Died from murder`() {
                testDie(Day(5), Murder(CHARACTER_ID_1))
            }

            @Test
            fun `Cannot die from murder in the future`() {
                testFailToDie(Day(11), Murder(CHARACTER_ID_1))
            }

            @Test
            fun `Cannot die from murder before its origin`() {
                testFailToDie(Day(-1), Murder(CHARACTER_ID_1))
            }

            @Test
            fun `Killer doesn't exist`() {
                testFailToDie(Day(5), Murder(CHARACTER_ID_2))
            }

            @Test
            fun `Died from old age`() {
                testDie(Day(5), OldAge)
            }

            @Test
            fun `Cannot die from old age in the future`() {
                testFailToDie(Day(11), OldAge)
            }

            @Test
            fun `Cannot die from old age before its origin`() {
                testFailToDie(Day(-1), OldAge)
            }

            private fun testDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
                val character = Character(CHARACTER_ID_0, vitalStatus = Dead(deathDate, causeOfDeath))
                val action = UpdateCharacter(character)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    character,
                    result.getCharacterStorage().getOrThrow(CHARACTER_ID_0)
                )
            }

            private fun testFailToDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, vitalStatus = Dead(deathDate, causeOfDeath)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }

        @Nested
        inner class HousingStatusTest {

            @Test
            fun `Cannot use unknown building as home`() {
                val action = UpdateCharacter(Character(CHARACTER_ID_0, housingStatus = History(InHouse(BUILDING_ID_0))))

                assertIllegalArgument("The home doesn't exist!") { REDUCER.invoke(STATE, action) }
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
            val state = STATE.removeStorage(CULTURE0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, culture = CULTURE0))

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown personality trait`() {
            val state = STATE.removeStorage(PERSONALITY0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, personality = setOf(PERSONALITY0)))

            assertIllegalArgument("Requires unknown Personality Trait 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = STATE.removeStorage(RACE0)
            val action = UpdateCharacter(Character(CHARACTER_ID_0, race = RACE0))

            assertIllegalArgument("Requires unknown Race 0!") { REDUCER.invoke(state, action) }
        }
    }

}