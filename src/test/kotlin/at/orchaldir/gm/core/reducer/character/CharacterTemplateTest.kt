package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.UseUniform
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel.Native
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.UseRaceRarityMap
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statblock.UniqueStatblock
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CharacterTemplateTest {

    private val STATE = State(
        listOf(
            Storage(Culture(CULTURE_ID_0)),
            Storage(God(GOD_ID_0)),
            Storage(Language(LANGUAGE_ID_0)),
            Storage(Race(RACE_ID_0)),
            Storage(Statistic(STATISTIC_ID_0)),
            Storage(Uniform(UNIFORM_ID_0)),
            Storage(CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(CharacterTemplate(UNKNOWN_CHARACTER_TEMPLATE_ID, race = RACE_LOOKUP_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Using an unknown race`() {
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = UNKNOWN_RACE_LOOKUP)
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Race 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown race with rarity`() {
            val map = OneOf(UNKNOWN_RACE_ID)
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = UseRaceRarityMap(map))
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Race 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown culture`() {
            val template =
                CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0, culture = UNKNOWN_CULTURE_ID)
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Culture 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown statistic`() {
            val statblock = UniqueStatblock(StatblockUpdate(mapOf(UNKNOWN_STATISTIC_ID to 4)))
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0, statblock = statblock)
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Statistic 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown language`() {
            val template = CharacterTemplate(
                CHARACTER_TEMPLATE_ID_0,
                race = RACE_LOOKUP_0,
                languages = mapOf(UNKNOWN_LANGUAGE_ID to Native)
            )
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Language 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown god`() {
            val template =
                CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0, belief = WorshipOfGod(UNKNOWN_GOD_ID))
            val action = UpdateAction(template)

            assertIllegalArgument("The belief's God 99 doesn't exist!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Using an unknown uniform`() {
            val equipped = UseUniform(UNKNOWN_UNIFORM_ID)
            val template = CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0, equipped = equipped)
            val action = UpdateAction(template)

            assertIllegalArgument("Requires unknown Uniform 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update with all values set`() {
            val template = CharacterTemplate(
                CHARACTER_TEMPLATE_ID_0,
                Name.init("Test"),
                RACE_LOOKUP_0,
                Gender.Male,
                CULTURE_ID_0,
                mapOf(LANGUAGE_ID_0 to Native),
                WorshipOfGod(GOD_ID_0),
                UniqueStatblock(StatblockUpdate()),
                UseUniform(UNIFORM_ID_0),
            )
            val action = UpdateAction(template)

            assertEquals(
                template,
                REDUCER.invoke(STATE, action).first.getCharacterTemplateStorage().get(CHARACTER_TEMPLATE_ID_0)
            )
        }
    }

}