package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterInstance
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdPair
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.CharacterTemplateReference
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterInstanceTest {

    private val oldInstance = CharacterInstance(
        CHARACTER_INSTANCE_ID_0,
        race = RACE_ID_0,
        gender = Gender.Male,
    )
    private val STATE = State(
        listOf(
            Storage(Culture(CULTURE_ID_0)),
            Storage(God(GOD_ID_0)),
            Storage(Language(LANGUAGE_ID_0)),
            Storage(Race(RACE_ID_0)),
            Storage(Statistic(STATISTIC_ID_0)),
            Storage(Uniform(UNIFORM_ID_0)),
            Storage(oldInstance),
            Storage(CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_LOOKUP_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Based on an unknown character`() {
            val instance = oldInstance.copy(basedOn = CharacterReference(UNKNOWN_CHARACTER_ID))

            fail(instance, "Requires unknown Base (Character 99)!")
        }

        @Test
        fun `Based on an unknown character template`() {
            val reference = CharacterTemplateReference(UNKNOWN_CHARACTER_TEMPLATE_ID)
            val instance = oldInstance.copy(basedOn = reference)

            fail(instance, "Requires unknown Base (Character Template 99)!")
        }

        @Test
        fun `Using an unknown race`() {
            fail(oldInstance.copy(race = UNKNOWN_RACE_ID), "Requires unknown Race 99!")
        }

        @Test
        fun `Using an gender not supported by the race`() {
            fail(oldInstance.copy(gender = Gender.Genderless), "Gender Genderless not allowed by Race 0!")
        }

        @Test
        fun `Using an unknown statistic`() {
            val statblock = Statblock(UNKNOWN_STATISTIC_ID, 4)
            val instance = oldInstance.copy(statblock = statblock)


            fail(instance, "Requires unknown Statistic 99!")
        }

        @Test
        fun `Using an unknown equipment`() {
            val equipped = EquipmentMap<EquipmentIdPair>(Pair(UNKNOWN_EQUIPMENT_ID, null), BodySlot.Bottom)
            val instance = oldInstance.copy(equipped = equipped)

            fail(instance, "Requires unknown Equipment 99!")
        }

        private fun fail(instance: CharacterInstance, message: String) {
            val action = UpdateAction(instance)

            assertIllegalArgument(message) { REDUCER.invoke(STATE, action) }
        }
    }

}