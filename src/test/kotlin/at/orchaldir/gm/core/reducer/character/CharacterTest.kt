package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.Gender.Genderless
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.ImmutableLifeStage
import at.orchaldir.gm.core.model.race.aging.LifeStageId
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statblock.UniqueStatblock
import at.orchaldir.gm.core.model.rpg.statblock.UseStatblockOfTemplate
import at.orchaldir.gm.core.model.rpg.trait.CharacterTrait
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

            assertInvalid(character, "Requires unknown Character Template 99!")
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
        inner class AgeTest {
            @Test
            fun `Cannot use age via default life stage with immutable life stage`() {
                val newState = STATE.updateStorage(Race(RACE_ID_0, lifeStages = ImmutableLifeStage()))
                val character = Character(CHARACTER_ID_0, age = AgeViaDefaultLifeStage, race = RACE_ID_0)

                assertInvalid(character, "Age via default life stage is not supported by Race 0!", newState)
            }

            @Test
            fun `Cannot use age via life stage with immutable life stage`() {
                val newState = STATE.updateStorage(Race(RACE_ID_0, lifeStages = ImmutableLifeStage()))
                val age = AgeViaLifeStage(LifeStageId(0))
                val character = Character(CHARACTER_ID_0, age = age, race = RACE_ID_0)

                assertInvalid(character, "Age via life stage is not supported by Race 0!", newState)
            }

            @Test
            fun `Cannot use an unknown life stage`() {
                val character = Character(CHARACTER_ID_0, age = AgeViaLifeStage(LifeStageId(-1)))

                assertInvalid(character, "Age via Life Stage -1 is not supported by Race 0!")
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
                val state = STATE.updateStorage(Character(CHARACTER_ID_0, gender = Genderless))
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
                val state = STATE.updateStorage(Character(CHARACTER_ID_0, gender = gender))

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

            @Test
            fun `Cannot be born in the future`() {
                val age = AgeViaBirthdate(Day(1))
                val character = Character(CHARACTER_ID_0, age = age)

                assertInvalid(character, "Date (Birthday) is in the future!")
            }

            @Test
            fun `Check origin with Unknown mother`() {
                val origin = BornElement(UNKNOWN_CHARACTER_ID, null)
                val character = Character(CHARACTER_ID_0, origin = origin)

                assertInvalid(character, "Requires unknown parent Character 99!")
            }

        }

        @Test
        fun `Cannot believe in an unknown god`() {
            val character = Character(CHARACTER_ID_0, beliefStatus = History(WorshipOfGod(UNKNOWN_GOD_ID)))

            assertInvalid(character, "The belief's God 99 doesn't exist!")
        }

        @Test
        fun `Cannot be the secret identity of an unknown character`() {
            val character = Character(CHARACTER_ID_0, authenticity = SecretIdentity(UNKNOWN_CHARACTER_ID))

            assertInvalid(character, "Cannot be the secret identity of unknown Character 99!")
        }

        @Test
        fun `Using an unknown uniform`() {
            val equipped = UseUniform(UNKNOWN_UNIFORM_ID)
            val character = Character(CHARACTER_ID_0, equipped = equipped)

            assertInvalid(character, "Requires unknown Uniform 99!")
        }

        @Nested
        inner class HousingStatusTest {

            @Test
            fun `Cannot use unknown building as home`() {
                val character =Character(CHARACTER_ID_0, housingStatus = History(InHome(BUILDING_ID_0)))

                assertInvalid(character, "Requires unknown home!")
            }
        }

        @Test
        fun `Cannot update unknown character`() {
            val state = STATE.removeStorage(CHARACTER_ID_0)

            assertInvalid(character0, "Requires unknown Character 0!", state)
        }

        @Test
        fun `Cannot use unknown culture`() {
            val character = Character(CHARACTER_ID_0, culture = UNKNOWN_CULTURE_ID)

            assertInvalid(character, "Requires unknown Culture 99!")
        }

        @Test
        fun `Cannot use unknown title`() {
            val character = Character(CHARACTER_ID_0, title = UNKNOWN_TITLE_ID)

            assertInvalid(character, "Requires unknown Title 99!")
        }

        @Test
        fun `Cannot use unknown race`() {
            val character = Character(CHARACTER_ID_0, race = UNKNOWN_RACE_ID)

            assertInvalid(character, "Requires unknown Race 99!")
        }

        @Test
        fun `Using an unknown statistic`() {
            val statblock = UniqueStatblock(StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4)))
            val character = Character(CHARACTER_ID_0, statblock = statblock)

            assertInvalid(character, "Requires unknown Statistic 99!")
        }

        private fun assertInvalid(character: Character, message: String, state: State = STATE) {
            val action = UpdateAction(character)

            assertIllegalArgument(message) { REDUCER.invoke(state, action) }
        }
    }

}