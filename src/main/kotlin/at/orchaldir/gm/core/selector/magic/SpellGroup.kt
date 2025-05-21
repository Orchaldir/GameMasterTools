package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.SpellId

fun State.canDeleteSpellGroup(group: SpellGroupId) = countMagicTraditions(group) == 0

fun State.countSpellGroups(spell: SpellId) = getSpellGroupStorage()
    .getAll()
    .count { it.spells.contains(spell) }

fun State.getSpellGroups(id: SpellId) = getSpellGroupStorage()
    .getAll()
    .filter { it.spells.contains(id) }
