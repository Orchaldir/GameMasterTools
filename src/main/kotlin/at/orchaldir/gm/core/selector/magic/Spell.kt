package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.economy.getJobsContaining
import at.orchaldir.gm.core.selector.item.getTextsContaining
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteSpell(spell: SpellId) = DeleteResult(spell)
    .addElements(getDomainsAssociatedWith(spell))
    .addElements(getJobsContaining(spell))
    .addElements(getSpellsBasedOn(spell))
    .addElements(getSpellGroups(spell))
    .addElements(getTextsContaining(spell))

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

fun State.getExistingSpells(date: Date?) = getExistingElements(getSpellStorage().getAll(), date)

fun State.getSpellsBasedOn(id: SpellId) = getSpellStorage()
    .getAll()
    .filter { it.origin.isChildOf(id.value) }
