package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.LIST
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.*
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRaceLookup(
    call: ApplicationCall,
    state: State,
    lookup: RaceLookup,
) {
    when (lookup) {
        is UseRace -> link(call, state, lookup.race)
        is UseRaceRarityMap -> showInlineIds(call, state, lookup.map.getValidValues())
    }
}

fun HtmlBlockTag.showRaceLookupDetails(
    call: ApplicationCall,
    state: State,
    lookup: RaceLookup,
) {
    showDetails("Race Lookup", true) {
        field("Type", lookup.getType())

        when (lookup) {
            is UseRace -> fieldLink(call, state, lookup.race)
            is UseRaceRarityMap -> showRarityMap("Races", lookup.map, true) {
                link(call, state, it)
            }
        }
    }
}

// edit


fun HtmlBlockTag.editRaceLookup(
    state: State,
    lookup: RaceLookup,
    param: String = RACE,
) {
    showDetails("Race Lookup", true) {
        selectValue(
            "Type",
            param,
            RaceLookupType.entries,
            lookup.getType(),
        )

        when (lookup) {
            is UseRace -> selectElement(
                state,
                combine(param, REFERENCE),
                state.sortRaces(),
                lookup.race,
            )
            is UseRaceRarityMap -> selectRarityMap(
                "Races",
                combine(param, REFERENCE, LIST),
                state.getRaceStorage(),
                lookup.map,
            )
        }
    }
}


// parse

fun parseRaceLookup(
    parameters: Parameters,
    state: State,
    param: String = RACE,
) = when (parse(parameters, param, RaceLookupType.Race)) {
    RaceLookupType.Race -> UseRace(
        parseRaceId(parameters, combine(param, REFERENCE)),
    )
    RaceLookupType.Rarity -> UseRaceRarityMap(
        parseOneOf(
            parameters,
            combine(param, REFERENCE, LIST),
            ::parseRaceId,
            listOf(state.getRaceStorage().getAll().first().id),
        )
    )
}
