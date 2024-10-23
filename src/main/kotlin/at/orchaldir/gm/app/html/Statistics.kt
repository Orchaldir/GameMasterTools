package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.selector.world.countArchitecturalStyles
import at.orchaldir.gm.core.selector.world.countTowns
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// count

fun HtmlBlockTag.showArchitecturalStyleCount(
    call: ApplicationCall,
    state: State,
    buildings: List<Building>,
) = showCount(call, state, "Architectural Styles", countArchitecturalStyles(buildings))

fun HtmlBlockTag.showTownCount(
    call: ApplicationCall,
    state: State,
    buildings: List<Building>,
) = showCount(call, state, "Towns", countTowns(buildings))

fun <ID : Id<ID>> HtmlBlockTag.showCount(
    call: ApplicationCall,
    state: State,
    label: String,
    map: Map<ID, Int>,
) {
    showMap(label, map) { id, count ->
        link(call, state, id)
        +": $count"
    }
}