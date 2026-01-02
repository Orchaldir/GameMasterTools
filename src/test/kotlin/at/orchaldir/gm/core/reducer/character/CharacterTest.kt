package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.CreateAction
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.Gender.Genderless
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.util.testAllowedVitalStatusTypes
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharacterTest {

    val character0 = Character(CHARACTER_ID_0)

    @Nested
    inner class UpdateTest {

        val STATE = State(
            listOf(
                Storage(CALENDAR0),
                Storage(character0),
                Storage(Business(BUSINESS_ID_0)),
                Storage(Culture(CULTURE_ID_0)),
                Storage(Language(LANGUAGE_ID_0)),
                Storage(Job(JOB_ID_0)),
                Storage(CharacterTrait(CHARACTER_TRAIT_ID_0)),
                Storage(listOf(Race(RACE_ID_0), Race(RACE_ID_1)))
            )
        )

        @Test
        fun `Using an unknown template`() {
            val statblock = UseStatblockOfTemplate(UNKNOWN_CHARACTER_TEMPLATE_ID)
            val character = Character(CHARACTER_ID_0, statblock = statblock)
            val action = UpdateAction(character)

            assertIllegalArgument("Requires unknown Character Template 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Test allowed vital status types`() {
            val age = AgeViaBirthdate(DAY0)

            testAllowedVitalStatusTypes(
                STATE,
                mapOf(
                    VitalStatusType.Abandoned to false,
                    VitalStatusType.Alive to true,
                    VitalStatusType.Closed to false,
                    VitalStatusType.Dead to true,
                    VitalStatusType.Destroyed to false,
                    VitalStatusType.Vanished to true,
                ),
            ) { status ->
                Character(CHARACTER_ID_0, age = age, status = status)
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
                    val action = UpdateAction(character)

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
                    val action = UpdateAction(character)

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
                        character0,
                        Character(CHARACTER_ID_1, gender = Gender.Male),
                        Character(CHARACTER_ID_2, gender = Gender.Female)
                    )
                )
            )

            @Test
            fun `Cannot be born in the future`() {
                val age = AgeViaBirthdate(Day(1))
                val action = UpdateAction(Character(CHARACTER_ID_0, age = age))

                assertIllegalArgument("Date (Birthday) is in the future!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `Check origin with Unknown mother`() {
                val action =
                    UpdateAction(
                        Character(
                            CHARACTER_ID_0,
                            origin = BornElement(UNKNOWN_CHARACTER_ID, null)
                        )
                    )

                assertIllegalArgument("Requires unknown parent Character 99!") { REDUCER.invoke(state, action) }
            }

        }

        @Test
        fun `Cannot believe in an unknown god`() {
            val action =
                UpdateAction(Character(CHARACTER_ID_0, beliefStatus = History(WorshipOfGod(UNKNOWN_GOD_ID))))

            assertIllegalArgument("The belief's God 99 doesn't exist!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot be the secret identity of an unknown character`() {
            val action = UpdateAction(Character(CHARACTER_ID_0, authenticity = SecretIdentity(UNKNOWN_CHARACTER_ID)))

            assertIllegalArgument("Cannot be the secret identity of unknown Character 99!") {
                REDUCER.invoke(
                    STATE,
                    action
                )
            }
        }

        @Test
        fun `Using an unknown uniform`() {
            val equipped = UseUniform(UNKNOWN_UNIFORM_ID)
            val template = Character(CHARACTER_ID_0, equipped = equipped)
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Uniform 99!") { REDUCER.invoke(STATE, action) }
        }

        @Nested
        inner class HousingStatusTest {

            @Test
            fun `Cannot use unknown building as home`() {
                val action =
                    UpdateAction(Character(CHARACTER_ID_0, housingStatus = History(InHome(BUILDING_ID_0))))

                assertIllegalArgument("Requires unknown home!") { REDUCER.invoke(STATE, action) }
            }
        }

        @Test
        fun `Cannot update unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)
            val action = UpdateAction(character0)

            assertIllegalArgument("Requires unknown Character 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown culture`() {
            val state = STATE.removeStorage(CULTURE_ID_0)
            val action = UpdateAction(Character(CHARACTER_ID_0, culture = CULTURE_ID_0))

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown title`() {
            val action = UpdateAction(Character(CHARACTER_ID_0, title = TITLE_ID_0))

            assertIllegalArgument("Requires unknown Title 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot use unknown race`() {
            val state = STATE.removeStorage(RACE_ID_0)
            val action = UpdateAction(Character(CHARACTER_ID_0, race = RACE_ID_0))

            assertIllegalArgument("Requires unknown Race 0!") { REDUCER.invoke(state, action) }
        }
    }

}