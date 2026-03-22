package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.CHARACTER_TEMPLATE_ID_0
import at.orchaldir.gm.ENCOUNTER_ID_0
import at.orchaldir.gm.ENCOUNTER_ID_1
import at.orchaldir.gm.RACE_ID_0
import at.orchaldir.gm.STATISTIC_ID_0
import at.orchaldir.gm.STATISTIC_ID_1
import at.orchaldir.gm.UNKNOWN_CHARACTER_TEMPLATE_ID
import at.orchaldir.gm.UNKNOWN_ENCOUNTER_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.race.UseRace
import at.orchaldir.gm.core.model.rpg.encounter.CharacterTemplateEncounter
import at.orchaldir.gm.core.model.rpg.encounter.Encounter
import at.orchaldir.gm.core.model.rpg.encounter.EncounterLookup
import at.orchaldir.gm.core.model.rpg.statistic.Attribute
import at.orchaldir.gm.core.model.rpg.statistic.BasedOnStatistic
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EncounterTest {

    private val stat = State(
        listOf(
            Storage(CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = UseRace(RACE_ID_0))),
            Storage(listOf(Encounter(ENCOUNTER_ID_0), Encounter(ENCOUNTER_ID_1))),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot be based on a unknown character template`() {
            val entry = CharacterTemplateEncounter(UNKNOWN_CHARACTER_TEMPLATE_ID)
            val encounter = Encounter(ENCOUNTER_ID_0, entry = entry)
            val action = UpdateAction(encounter)

            assertIllegalArgument("Requires unknown Character Template 99!") { REDUCER.invoke(stat, action) }
        }

        @Test
        fun `Cannot be based on a unknown encounter`() {
            val entry = EncounterLookup(UNKNOWN_ENCOUNTER_ID)
            val encounter = Encounter(ENCOUNTER_ID_0, entry = entry)
            val action = UpdateAction(encounter)

            assertIllegalArgument("Requires unknown Encounter 99!") { REDUCER.invoke(stat, action) }
        }

        @Test
        fun `Cannot be based on itself`() {
            val entry = EncounterLookup(ENCOUNTER_ID_0)
            val encounter = Encounter(ENCOUNTER_ID_0, entry = entry)
            val action = UpdateAction(encounter)

            assertIllegalArgument("Cannot be based on itself!") { REDUCER.invoke(stat, action) }
        }

        @Test
        fun `Update with all values set`() {
            val entry = CharacterTemplateEncounter(CHARACTER_TEMPLATE_ID_0)
            val encounter = Encounter(ENCOUNTER_ID_0, entry = entry)
            val action = UpdateAction(encounter)

            REDUCER.invoke(stat, action)
        }
    }

}