package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.selector.economy.getBusinessesFoundedBy
import at.orchaldir.gm.core.selector.economy.isInOperation
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.getBuildingsBuildBy
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> checkCreator(
    state: State,
    creator: Creator,
    created: ID,
    date: Date,
    noun: String,
) {
    when (creator) {
        is CreatedByBusiness -> {
            require(creator.business != created) { "A business cannot create itself!" }
            state.getBusinessStorage()
                .require(creator.business) { "Cannot use an unknown business ${creator.business.value} as $noun!" }
            require(state.isInOperation(creator.business, date)) {
                "$noun (business ${creator.business.value}) is not open!"
            }
        }

        is CreatedByCharacter -> {
            state.getCharacterStorage()
                .require(creator.character) { "Cannot use an unknown character ${creator.character.value} as $noun!" }
            require(state.isAlive(creator.character, date)) {
                "$noun (character ${creator.character.value}) is not alive!"
            }
        }

        UndefinedCreator -> doNothing()
    }
}

fun <ID : Id<ID>> checkCreated(
    state: State,
    id: ID,
    noun: String,
) {
    val buildings = state.getBuildingsBuildBy(id)
    require(buildings.isEmpty()) { "Cannot delete $noun ${id.value()}, because of built buildings!" }
    val businesses = state.getBusinessesFoundedBy(id)
    require(businesses.isEmpty()) { "Cannot delete $noun ${id.value()}, because of founded businesses!" }
    val invented = state.getInventedLanguages(id)
    require(invented.isEmpty()) { "Cannot delete $noun ${id.value()}, because of invented languages!" }
}
