package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith

fun State.canDeleteSpellGroup(group: SpellGroupId) = DeleteResult(group)
    .addElements(getMagicTraditions(group))

fun State.countSpellGroups(spell: SpellId) = getSpellGroupStorage()
    .getAll()
    .count { it.spells.contains(spell) }

fun State.getSpellGroups(id: SpellId) = getSpellGroupStorage()
    .getAll()
    .filter { it.spells.contains(id) }
