package at.orchaldir.gm.core.reducer.culture

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.name.NameList
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.reducer.*
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
        )
    )
    private val STATE_WITH_NAMES = STATE.updateStorage(Storage(nameList))

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing language`() {
            val action = DeleteCulture(CULTURE_ID_0)

            assertEquals(0, REDUCER.invoke(STATE, action).first.getCultureStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteCulture(CULTURE_ID_0)

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if used by a character`() {
            val action = DeleteCulture(CULTURE_ID_0)
            val state = STATE.updateStorage(Storage(listOf(Character(CharacterId(0), culture = CULTURE_ID_0))))

            assertIllegalArgument("Culture 0 is used by characters") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateCulture(Culture(CULTURE_ID_0))

            assertIllegalArgument("Requires unknown Culture 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot update culture with unknown calendar`() {
            val action = UpdateCulture(Culture(CULTURE_ID_0, calendar = CALENDAR_ID_1))

            assertIllegalArgument("Requires unknown Calendar 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown fashion`() {
            val action = UpdateCulture(Culture(CULTURE_ID_0, fashion = GenderMap(FASHION_ID_0)))

            assertIllegalArgument("Requires unknown Fashion 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update culture with unknown name list`() {
            val action = UpdateCulture(Culture(CULTURE_ID_0, namingConvention = MononymConvention(NAME_LIST_ID0)))

            assertIllegalArgument("Requires unknown Name List 0!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Can update culture with known name list`() {
            val culture = Culture(CULTURE_ID_0, namingConvention = MononymConvention(NAME_LIST_ID0))
            val action = UpdateCulture(culture)

            assertEquals(Storage(listOf(culture)), REDUCER.invoke(STATE_WITH_NAMES, action).first.getCultureStorage())
        }

        @Nested
        inner class ChangingToNoNameConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToNo(FamilyConvention(), Mononym("A"))
            }

            @Test
            fun `From family to no convention`() {
                changeToNo(FamilyConvention(), FamilyName("A", null, "B"))
            }

            @Test
            fun `From genonym to no convention`() {
                changeToNo(GenonymConvention(), Genonym("A"))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToNo(PatronymConvention(), Genonym("A"))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToNo(MatronymConvention(), Genonym("A"))
            }

            private fun changeToNo(old: NamingConvention, name: CharacterName) =
                changeConvention(old, NoNamingConvention, name, Mononym("A"))
        }

        @Nested
        inner class ChangingToMononymNameConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToMononym(FamilyConvention(), Mononym("A"))
            }

            @Test
            fun `From family to no convention`() {
                changeToMononym(FamilyConvention(), FamilyName("A", null, "B"))
            }

            @Test
            fun `From genonym to no convention`() {
                changeToMononym(GenonymConvention(), Genonym("A"))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToMononym(PatronymConvention(), Genonym("A"))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToMononym(MatronymConvention(), Genonym("A"))
            }

            private fun changeToMononym(old: NamingConvention, name: CharacterName) =
                changeConvention(old, MononymConvention(), name, Mononym("A"))
        }

        @Nested
        inner class ChangingToFamilyConventionTest {

            @Test
            fun `Keep Mononym`() {
                changeToFamily(FamilyConvention(), Mononym("A"))
            }

            @Test
            fun `Keep family name`() {
                val familyName = FamilyName("A", null, "B")
                val convention = FamilyConvention()

                changeConvention(convention, convention, familyName, familyName)
            }

            @Test
            fun `From genonym to no convention`() {
                changeToFamily(GenonymConvention(), Genonym("A"))
            }

            @Test
            fun `From patronym to no convention`() {
                changeToFamily(PatronymConvention(), Genonym("A"))
            }

            @Test
            fun `From matronym to no convention`() {
                changeToFamily(MatronymConvention(), Genonym("A"))
            }

            private fun changeToFamily(old: NamingConvention, oldName: CharacterName) =
                changeConvention(old, FamilyConvention(), oldName, Mononym("A"))
        }


        @Nested
        inner class ChangingToPatronymConventionTest {

            @Test
            fun `Change mononym to genonym`() {
                changeToPatronym(FamilyConvention(), Mononym("A"))
            }

            @Test
            fun `Change family name to genonym`() {
                changeToPatronym(FamilyConvention(), FamilyName("A", null, "B"))
            }

            @Test
            fun `Keep for genonym`() {
                changeToPatronym(GenonymConvention(), Genonym("A"))
            }

            @Test
            fun `Keep for patronym`() {
                changeToPatronym(PatronymConvention(), Genonym("A"))
            }

            @Test
            fun `Keep for matronym`() {
                changeToPatronym(MatronymConvention(), Genonym("A"))
            }

            private fun changeToPatronym(old: NamingConvention, oldName: CharacterName) =
                changeConvention(old, PatronymConvention(), oldName, Genonym("A"))
        }

        private fun changeConvention(
            old: NamingConvention, new: NamingConvention, oldName: CharacterName, newName: CharacterName,
        ) {
            val action = UpdateCulture(Culture(CULTURE_ID_0, namingConvention = new))
            val character0 = Character(CHARACTER_ID_0, oldName, culture = CULTURE_ID_0)
            val character1 = Character(CHARACTER_ID_1, Mononym("Z"), culture = CULTURE_ID_1)
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