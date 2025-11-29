package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId

fun State.canDeleteCharacterTemplate(template: CharacterTemplateId) = DeleteResult(template)
    .addElements(getCharactersUsing(template))

// get characters

fun State.getCharacterTemplates(culture: CultureId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.culture == culture }

fun State.getCharacterTemplates(equipment: EquipmentId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.equipped.contains(equipment) }

fun State.getCharacterTemplates(language: LanguageId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.languages.containsKey(language) }

fun State.getCharacterTemplates(race: RaceId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.race == race }

fun State.getCharacterTemplates(statistic: StatisticId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.statblock.statistics.containsKey(statistic) }

fun State.getCharacterTemplates(uniform: UniformId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.equipped.contains(uniform) }
