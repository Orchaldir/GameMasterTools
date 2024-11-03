package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.exists
import at.orchaldir.gm.utils.doNothing
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showOwnership(
    call: ApplicationCall,
    state: State,
    ownership: Ownership,
) {
    field("Owner") {
        showOwner(call, state, ownership.owner)
    }
    showList("Previous Owners", ownership.previousOwners) { previous ->
        +"Until "
        showDate(call, state, previous.until)
        +": "
        showOwner(call, state, previous.owner)
    }
}

fun HtmlBlockTag.showOwner(
    call: ApplicationCall,
    state: State,
    owner: Owner,
) {
    when (owner) {
        NoOwner -> +"None"
        is OwnedByCharacter -> link(call, state, owner.character)
        is OwnedByTown -> link(call, state, owner.town)
        UnknownOwner -> +"Unknown"
    }
}

fun FORM.selectOwnership(
    state: State,
    ownership: Ownership,
    startDate: Date,
) {
    val previousOwnersParam = combine(OWNER, HISTORY)
    selectInt("Previous Owners", ownership.previousOwners.size, 0, 100, 1, previousOwnersParam, true)
    var minDate = startDate.next()

    showListWithIndex(ownership.previousOwners) { index, previous ->
        val previousParam = combine(previousOwnersParam, index)
        selectOwner(state, previousParam, previous.owner, minDate)
        selectDate(state, "Until", previous.until, combine(previousParam, DATE), minDate)

        minDate = previous.until.next()
    }

    selectOwner(state, OWNER, ownership.owner, minDate)
}

fun HtmlBlockTag.selectOwner(
    state: State,
    param: String,
    owner: Owner,
    start: Date,
) {
    selectValue("Owner Type", param, OwnerType.entries, true) { type ->
        label = type.toString()
        value = type.toString()
        selected = owner.getType() == type
    }
    when (owner) {
        is OwnedByCharacter -> selectValue(
            "Owner",
            combine(param, CHARACTER),
            state.getCharacterStorage().getAll(),
            false
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = owner.character == character.id
            disabled = !state.isAlive(character, start)
        }

        is OwnedByTown -> selectValue(
            "Owner",
            combine(param, TOWN),
            state.getTownStorage().getAll(),
            false
        ) { town ->
            label = town.name
            value = town.id.value.toString()
            selected = owner.town == town.id
            disabled = !state.exists(town, start)
        }

        else -> doNothing()
    }
}
