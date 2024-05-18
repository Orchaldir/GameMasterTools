package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LanguageTest {
    @Test
    fun `Can delete an existing language`() {
        val state = CREATE_LANGUAGE.invoke(State(), CreateLanguage).first
        val action = DeleteLanguage(LanguageId(0))

        assertTrue(DELETE_LANGUAGE.invoke(state, action).first.languages.elements.isEmpty())
    }

    @Test
    fun `Cannot delete unknown id`() {
        val action = DeleteLanguage(LanguageId(0))

        assertFailsWith<IllegalArgumentException> { DELETE_LANGUAGE.invoke(State(), action) }
    }

    @Test
    fun `Cannot update unknown id`() {
        val action = UpdateLanguage(Language(LanguageId(0)))

        assertFailsWith<IllegalArgumentException> { UPDATE_LANGUAGE.invoke(State(), action) }
    }

}