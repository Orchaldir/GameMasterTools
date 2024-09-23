package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.language.ComprehensionLevel
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ID1 = CharacterId(1)
private val ID2 = CharacterId(2)
private val BUILDING0 = BuildingId(0)
private val CULTURE0 = CultureId(0)
private val LANGUAGE0 = LanguageId(0)
private val LANGUAGES = mapOf(LANGUAGE0 to ComprehensionLevel.Native)
private val PERSONALITY0 = PersonalityTraitId(0)
private val RACE0 = RaceId(0)
private val RACE1 = RaceId(1)

class CharacterTest {

    @Nested
    inner class CreateTest {

        @Test
        fun `Create another character`() {
            val character0 = Character(ID0)
            val character1 = Character(ID1)
            val state = State(Storage(listOf(character0)))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(2, characters.getSize())
            assertEquals(character0, characters.getOrThrow(ID0))
            assertEquals(character1, characters.getOrThrow(ID1))
        }

        @Test
        fun `Default birthday is today`() {
            val today = Day(42)
            val state = State(time = Time(currentDate = today))

            val characters = REDUCER.invoke(state, CreateCharacter).first.getCharacterStorage()

            assertEquals(today, characters.getOrThrow(ID0).birthDate)
        }
    }

    @Nested
    inner class DeleteTest {

        private val action = DeleteCharacter(ID0)

        @Test
        fun `Can delete an existing character`() {
            val state = State(Storage(listOf(Character(ID0))))

            assertEquals(0, REDUCER.invoke(state, action).first.getCharacterStorage().getSize())
        }

        @Test
        fun `Cannot delete an inventor`() {
            val origin = InventedLanguage(ID0)
            val state = State(
                listOf(
                    Storage(listOf(Character(ID0))),
                    Storage(listOf(Language(LANGUAGE0, origin = origin)))
                )
            )

            assertIllegalArgument("Cannot delete character 0, because he is an language inventor!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a building owner`() {
            val building = Building(BUILDING0, ownership = Ownership(OwnedByCharacter(ID0)))
            val state = State(
                listOf(
                    Storage(listOf(Character(ID0))),
                    Storage(listOf(building))
                )
            )

            assertIllegalArgument("Cannot delete character 0, because he owns buildings!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Cannot delete a previous building owner`() {
            val building = Building(
                BUILDING0,
                ownership = Ownership(previousOwners = listOf(PreviousOwner(OwnedByCharacter(ID0), Day(0))))
            )
            val state = State(
                listOf(
                    Storage(listOf(Character(ID0))),
                    Storage(listOf(building))
                )
            )

            assertIllegalArgument("Cannot delete character 0, because he previously owned buildings!") {
                REDUCER.invoke(state, action)
            }
        }

        @Nested
        inner class DeleteFamilyMemberTest {

            private val state = State(
                Storage(
                    listOf(
                        Character(ID0, origin = Born(ID1, ID2)),
                        Character(ID1),
                        Character(ID2)
                    )
                ),
            )

            @Test
            fun `Cannot delete a character with parents`() {
                assertIllegalArgument("Cannot delete character 0, because he has parents!") {
                    REDUCER.invoke(state, DeleteCharacter(ID0))
                }
            }

            @Test
            fun `Cannot delete a father`() {
                assertIllegalArgument("Cannot delete character 2, because he has children!") {
                    REDUCER.invoke(state, DeleteCharacter(ID2))
                }
            }

            @Test
            fun `Cannot delete a mother`() {
                assertIllegalArgument("Cannot delete character 1, because he has children!") {
                    REDUCER.invoke(state, DeleteCharacter(ID1))
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

        @Test
        fun `Do not overwrite languages`() {
            val state = State(
                listOf(
                    Storage(listOf(Character(ID0, languages = LANGUAGES))),
                    Storage(listOf(Culture(CULTURE0))),
                    Storage(listOf(Language(LANGUAGE0))),
                    Storage(listOf(PersonalityTrait(PERSONALITY0))),
                    Storage(listOf(Race(RACE0), Race(RACE1)))
                )
            )
            val action =
                UpdateCharacter(Character(ID0, Mononym("Test"), RACE1, Gender.Male, personality = setOf(PERSONALITY0)))

            val result = REDUCER.invoke(state, action).first

            assertEquals(
                Character(
                    ID0,
                    Mononym("Test"),
                    RACE1,
                    Gender.Male,
                    personality = setOf(PERSONALITY0),
                    languages = LANGUAGES,
                ),
                result.getCharacterStorage().getOrThrow(ID0)
            )
        }

        @Nested
        inner class BornTest {
            private val UNKNOWN = CharacterId(3)

            private val state = State(
                listOf(
                    Storage(
                        listOf(
                            Character(ID0),
                            Character(ID1, gender = Gender.Male),
                            Character(ID2, gender = Gender.Female)
                        )
                    ),
                    Storage(listOf(Culture(CULTURE0))),
                    Storage(listOf(Race(RACE0)))
                )
            )

            @Test
            fun `Valid parents`() {
                val character = Character(ID0, origin = Born(ID2, ID1))
                val action = UpdateCharacter(character)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    character,
                    result.getCharacterStorage().getOrThrow(ID0)
                )
            }

            @Test
            fun `Cannot be born in the future`() {
                val action = UpdateCharacter(Character(ID0, birthDate = Day(1)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown mother`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(UNKNOWN, ID1)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Mother is not female`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID1, ID1)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Unknown father`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID2, UNKNOWN)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Father is not male`() {
                val action = UpdateCharacter(Character(ID0, origin = Born(ID2, ID2)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }

        }

        @Nested
        inner class CauseOfDeathTest {

            private val state = State(
                listOf(
                    Storage(
                        listOf(
                            Character(ID0),
                            Character(ID1),
                        )
                    ),
                    Storage(listOf(Culture(CULTURE0))),
                    Storage(listOf(Race(RACE0))),
                ),
                time = Time(currentDate = Day(10)),
            )

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
                testDie(Day(5), Murder(ID1))
            }

            @Test
            fun `Cannot die from murder in the future`() {
                testFailToDie(Day(11), Murder(ID1))
            }

            @Test
            fun `Cannot die from murder before its origin`() {
                testFailToDie(Day(-1), Murder(ID1))
            }

            @Test
            fun `Killer doesn't exist`() {
                testFailToDie(Day(5), Murder(ID2))
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
                val character = Character(ID0, vitalStatus = Dead(deathDate, causeOfDeath))
                val action = UpdateCharacter(character)

                val result = REDUCER.invoke(state, action).first

                assertEquals(
                    character,
                    result.getCharacterStorage().getOrThrow(ID0)
                )
            }

            private fun testFailToDie(deathDate: Day, causeOfDeath: CauseOfDeath) {
                val action = UpdateCharacter(Character(ID0, vitalStatus = Dead(deathDate, causeOfDeath)))

                assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
            }
        }

        @Test
        fun `Cannot update unknown character`() {
            val state = State(Storage(listOf(Race(RACE0))))
            val action = UpdateCharacter(Character(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown culture`() {
            val state = State(listOf(Storage(listOf(Character(ID0))), Storage(listOf(Race(RACE0)))))
            val action = UpdateCharacter(Character(ID0, culture = CULTURE0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown personality trait`() {
            val state = State(listOf(Storage(listOf(Character(ID0))), Storage(listOf(Race(RACE0)))))
            val action = UpdateCharacter(Character(ID0, personality = setOf(PERSONALITY0)))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = State(Storage(listOf(Character(ID0))))
            val action = UpdateCharacter(Character(ID0, race = RACE0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

}