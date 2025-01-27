package at.orchaldir.gm.core.selector.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId

fun State.canDeleteSpell(spell: SpellId) = true

fun countEachLanguage(texts: Collection<Spell>) = texts
    .filter { it.language != null }
    .groupingBy { it.language!! }
    .eachCount()

fun State.countSpells(language: LanguageId) = getSpellStorage()
    .getAll()
    .count { it.language == language }

fun State.getSpells(language: LanguageId) = getSpellStorage()
    .getAll()
    .filter { it.language == language }
