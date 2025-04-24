package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.utils.Id

fun State.canDeleteSpell(spell: SpellId) = getSpellsBasedOn(spell).isEmpty()
        && countDomains(spell) == 0

fun countEachLanguage(spells: Collection<Spell>) = spells
    .filter { it.language != null }
    .groupingBy { it.language!! }
    .eachCount()

fun countSpellOrigin(spells: Collection<Spell>) = spells
    .groupingBy { it.origin.getType() }
    .eachCount()

fun State.countSpells(language: LanguageId) = getSpellStorage()
    .getAll()
    .count { it.language == language }

fun State.getSpells(language: LanguageId) = getSpellStorage()
    .getAll()
    .filter { it.language == language }

fun State.getExistingSpell(date: Date?) = getExistingElements(getSpellStorage().getAll(), date)

fun <ID : Id<ID>> State.getSpellsCreatedBy(id: ID) = getSpellStorage()
    .getAll()
    .filter { it.creator().isId(id) }

fun State.getSpellsBasedOn(id: SpellId) = getSpellStorage()
    .getAll()
    .filter { it.origin.wasBasedOn(id) }
