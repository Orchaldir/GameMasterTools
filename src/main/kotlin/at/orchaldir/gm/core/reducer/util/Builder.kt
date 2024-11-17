package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.selector.economy.isInOperation
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.utils.doNothing

fun checkBuilder(
    state: State,
    builder: Creator,
    date: Date,
) {
    when (builder) {
        is CreatedByBusiness -> {
            state.getBusinessStorage()
                .require(builder.business) { "Cannot use an unknown business ${builder.business.value} as builder!" }
            require(state.isInOperation(builder.business, date)) {
                "Builder (business ${builder.business.value}) is not open!"
            }
        }

        is CreatedByCharacter -> {
            state.getCharacterStorage()
                .require(builder.character) { "Cannot use an unknown character ${builder.character.value} as builder!" }
            require(state.isAlive(builder.character, date)) {
                "Builder (character ${builder.character.value}) is not alive!"
            }
        }

        UndefinedCreator -> doNothing()
    }
}