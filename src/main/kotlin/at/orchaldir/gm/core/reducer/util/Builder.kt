package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.BuildByBusiness
import at.orchaldir.gm.core.model.world.building.BuildByCharacter
import at.orchaldir.gm.core.model.world.building.Builder
import at.orchaldir.gm.core.model.world.building.UndefinedBuilder
import at.orchaldir.gm.core.selector.economy.isOpen
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.utils.doNothing

fun checkBuilder(
    state: State,
    builder: Builder,
    date: Date,
) {
    when (builder) {
        is BuildByBusiness -> {
            state.getBusinessStorage()
                .require(builder.business) { "Cannot use an unknown business ${builder.business.value} as builder!" }
            require(state.isOpen(builder.business, date)) {
                "Builder (business ${builder.business.value}) is not open!"
            }
        }

        is BuildByCharacter -> {
            state.getCharacterStorage()
                .require(builder.character) { "Cannot use an unknown character ${builder.character.value} as builder!" }
            require(state.isAlive(builder.character, date)) {
                "Builder (character ${builder.character.value}) is not alive!"
            }
        }

        UndefinedBuilder -> doNothing()
    }
}
