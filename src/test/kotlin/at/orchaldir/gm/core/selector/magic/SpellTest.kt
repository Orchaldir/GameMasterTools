package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.AbstractContent
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpellTest {

    @Nested
    inner class CanDeleteTest {
        private val spell = Spell(SPELL_ID_0)
        private val state = State(
            listOf(
                Storage(spell),
            )
        )

        @Test
        fun `Cannot delete a spell modified by another spell`() {
            val origin = ModifiedElement(SPELL_ID_0)
            val spell1 = Spell(SPELL_ID_1, origin = origin)
            val newState = state.updateStorage(Storage(listOf(spell, spell1)))

            failCanDelete(newState, SPELL_ID_1)
        }

        @Test
        fun `Cannot delete a spell translated by another spell`() {
            val origin = TranslatedElement(SPELL_ID_0)
            val spell1 = Spell(SPELL_ID_1, origin = origin)
            val newState = state.updateStorage(Storage(listOf(spell, spell1)))

            failCanDelete(newState, SPELL_ID_1)
        }

        @Test
        fun `Cannot delete a spell used by a domain`() {
            val domain = Domain(DOMAIN_ID_0, spells = SomeOf(SPELL_ID_0))
            val newState = state.updateStorage(domain)

            failCanDelete(newState, DOMAIN_ID_0)
        }

        @Test
        fun `Cannot delete a spell used by a job`() {
            val job = Job(JOB_ID_0, spells = SomeOf(SPELL_ID_0))
            val newState = state.updateStorage(job)

            failCanDelete(newState, JOB_ID_0)
        }

        @Test
        fun `Cannot delete a spell used by a spell group`() {
            val group = SpellGroup(SPELL_GROUP_ID_0, spells = setOf(SPELL_ID_0))
            val newState = state.updateStorage(group)

            failCanDelete(newState, SPELL_GROUP_ID_0)
        }

        @Test
        fun `Cannot delete a spell contained in a text`() {
            val content = AbstractContent(spells = setOf(SPELL_ID_0))
            val group = Text(TEXT_ID_0, content = AbstractText(content))
            val newState = state.updateStorage(group)

            failCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(SPELL_ID_0).addId(blockingId),
                state.canDeleteSpell(SPELL_ID_0)
            )
        }
    }

}