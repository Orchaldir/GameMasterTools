package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.utils.Id

fun State.canDeleteSpell(spell: SpellId) = getSpellsBasedOn(spell).isEmpty()

fun countEachLanguage(texts: Collection<Spell>) = texts
    .filter { it.language != null }
    .groupingBy { it.language!! }
    .eachCount()

fun countSpellOrigin(texts: Collection<Spell>) = texts
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
    .filter { it.origin.wasCreatedBy(id) }

fun State.getSpellsBasedOn(id: SpellId) = getSpellStorage()
    .getAll()
    .filter { it.origin.wasBasedOn(id) }
