package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterInstanceId
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.containsId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId

fun State.canDeleteCharacterInstance(instance: CharacterInstanceId) = DeleteResult(instance)

fun State.getCharacterInstancesBasedOn(character: CharacterId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.basedOn.isId(character) }

fun State.getCharacterInstances(equipment: EquipmentId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.equipped.containsId(equipment) }

fun State.getCharacterInstances(race: RaceId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.race == race }

fun State.getCharacterInstancesWith(statistic: StatisticId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.statblock.statistics.containsKey(statistic) }


fun State.getCharacterInstances(template: CharacterTemplateId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.basedOn.isId(template) }
