package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CultureTest {
    private val nameList = NameList(NAME_LIST_ID0)
    private val STATE = State(
        listOf(
            Storage(Calendar(CALENDAR_ID_0)),
            Storage(Culture(CULTURE_ID_0)),
            Storage(Language(LANGUAGE_ID_0)),
        )
    )
    private val STATE_WITH_NAMES = STATE.updateStorage(nameList)

    @Nested
    inner class UpdateTest {

        private val givenNames = NonGenderedGivenNames(NAME_LIST_ID0)
        private val familyConvention = FamilyConvention(givenNames, NAME_LIST_ID0)
        private val genonymConvention = GenonymConvention(givenNames)
        private val patronymConvention = PatronymConvention(givenNames)
        private val matronymConvention = MatronymConvention(givenNames)

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Culture(CULTURE_ID_0))

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update culture with unknown calendar`() {
            val action = UpdateAction(Culture(CULTURE_ID_0, calendar = CALENDAR_ID_1))

            assertIllegalArgument("Requires unknown Calendar 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown fashion`() {
            val action = UpdateAction(Culture(CULTURE_ID_0, fashion = GenderMap(FASHION_ID_0)))

            assertIllegalArgument("Requires unknown Fashion 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown holiday`() {
            val action = UpdateAction(Culture(CULTURE_ID_0, holidays = setOf(HOLIDAY_ID_0)))

            assertIllegalArgument("Requires unknown Holiday 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown language`() {
            val action = UpdateAction(Culture(CULTURE_ID_0, languages = SomeOf(LANGUAGE_ID_0)))

            assertIllegalArgument("Requires unknown Language 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown name list`() {
            val action = UpdateAction(Culture(CULTURE_ID_0, namingConvention = MononymConvention(NAME_LIST_ID0)))

            assertIllegalArgument("Requires unknown Name List 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Can update culture with known name list`() {
            val culture = Culture(CULTURE_ID_0, namingConvention = MononymConvention(NAME_LIST_ID0))
            val action = UpdateAction(culture)

            assertEquals(Storage(listOf(culture)), REDUCER.invoke(STATE_WITH_NAMES, action).first.getCultureStorage())
        }

        @Nested
        inner class ChangingToNoNameConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToNo(familyConvention, Mononym(NAME0))
            }

            @Test
            fun `From family to no convention`() {
                changeToNo(familyConvention, FamilyName(NAME0, null, NAME1))
            }

            @Test
            fun `From genonym to no convention`() {
                changeToNo(genonymConvention, Genonym(NAME0))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToNo(patronymConvention, Genonym(NAME0))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToNo(matronymConvention, Genonym(NAME0))
            }

            private fun changeToNo(old: NamingConvention, name: CharacterName) =
                changeConvention(old, NoNamingConvention, name, Mononym(NAME0))
        }

        @Nested
        inner class ChangingToMononymNameConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToMononym(familyConvention, Mononym(NAME0))
            }

            @Test
            fun `From family to no convention`() {
                changeToMononym(familyConvention, FamilyName(NAME0, null, NAME1))
            }

            @Test
            fun `From genonym to no convention`() {
                changeToMononym(genonymConvention, Genonym(NAME0))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToMononym(patronymConvention, Genonym(NAME0))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToMononym(matronymConvention, Genonym(NAME0))
            }

            private fun changeToMononym(old: NamingConvention, name: CharacterName) =
                changeConvention(old, MononymConvention(givenNames), name, Mononym(NAME0))
        }

        @Nested
        inner class ChangingToFamilyConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToFamily(familyConvention, Mononym(NAME0))
            }

            @Test
            fun `Keep family name`() {
                val familyName = FamilyName(NAME0, null, NAME1)
                val convention = familyConvention

                changeConvention(convention, convention, familyName, familyName)
            }

            @Test
            fun `From genonym to no convention`() {
                changeToFamily(genonymConvention, Genonym(NAME0))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToFamily(patronymConvention, Genonym(NAME0))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToFamily(matronymConvention, Genonym(NAME0))
            }

            private fun changeToFamily(old: NamingConvention, oldName: CharacterName) =
                changeConvention(old, familyConvention, oldName, Mononym(NAME0))
        }


        @Nested
        inner class ChangingToPatronymConventionTest {

            @Test
            fun `Change mononym to genonym`() {
                changeToPatronym(familyConvention, Mononym(NAME0))
            }

            @Test
            fun `Change family name to genonym`() {
                changeToPatronym(familyConvention, FamilyName(NAME0, null, NAME1))
            }

            @Test
            fun `Keep for genonym`() {
                changeToPatronym(genonymConvention, Genonym(NAME0))
            }

            @Test
            fun `Keep for patronym`() {
                changeToPatronym(patronymConvention, Genonym(NAME0))
            }

            @Test
            fun `Keep for matronym`() {
                changeToPatronym(matronymConvention, Genonym(NAME0))
            }

            private fun changeToPatronym(old: NamingConvention, oldName: CharacterName) =
                changeConvention(old, patronymConvention, oldName, Genonym(NAME0))
        }

        private fun changeConvention(
            old: NamingConvention, new: NamingConvention, oldName: CharacterName, newName: CharacterName,
        ) {
            val action = UpdateAction(Culture(CULTURE_ID_0, namingConvention = new))
            val character0 = Character(CHARACTER_ID_0, oldName, culture = CULTURE_ID_0)
            val character1 = Character(CHARACTER_ID_1, Mononym(NAME2), culture = CULTURE_ID_1)
            val result = Character(CHARACTER_ID_0, newName, culture = CULTURE_ID_0)
            val state = State(
                listOf(
                    Storage(Calendar(CALENDAR_ID_0)),
                    Storage(listOf(character0, character1)),
                    Storage(Culture(CULTURE_ID_0, namingConvention = old)),
                    Storage(nameList)
                )
            )

            val newState = REDUCER.invoke(state, action)

            val storage = newState.first.getCharacterStorage()
            assertEquals(result, storage.getOrThrow(CHARACTER_ID_0))
            assertEquals(character1, storage.getOrThrow(CHARACTER_ID_1))
        }
    }

}