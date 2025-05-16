package at.orchaldir.gm.core.selector.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.countCharactersKilledInCatastrophe
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDeleteCatastrophe(catastrophe: CatastropheId) = countCharactersKilledInCatastrophe(catastrophe) == 0

fun State.getExistingCatastrophes(date: Date?) = getExistingElements(getCatastropheStorage().getAll(), date)