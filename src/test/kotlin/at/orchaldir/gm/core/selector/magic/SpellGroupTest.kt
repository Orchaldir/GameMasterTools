package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.DOMAIN_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.MAGIC_TRADITION_ID_0
import at.orchaldir.gm.SPELL_GROUP_ID_0
import at.orchaldir.gm.SPELL_ID_0
import at.orchaldir.gm.SPELL_ID_1
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.AbstractContent
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.magic.MagicTradition
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

class SpellGroupTest {

    @Nested
    inner class CanDeleteTest {
        private val group = SpellGroup(SPELL_GROUP_ID_0)
        private val state = State(
            listOf(
                Storage(group),
            )
        )

        @Test
        fun `Cannot delete a spell group used by a tradition`() {
            val tradition = MagicTradition(MAGIC_TRADITION_ID_0, groups = setOf(SPELL_GROUP_ID_0))
            val newState = state.updateStorage(Storage(tradition))

            failCanDelete(newState, MAGIC_TRADITION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(SPELL_GROUP_ID_0).addId(blockingId),
                state.canDeleteSpellGroup(SPELL_GROUP_ID_0)
            )
        }
    }

}