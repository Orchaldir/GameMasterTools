package at.orchaldir.gm.app.html.ecology

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.Ecology
import at.orchaldir.gm.core.model.ecology.HasEcology
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEcologiesWithPlant(
    call: ApplicationCall,
    state: State,
    plant: Plant,
) = showEcologiesWithElement(
    call,
    state,
    { it.contains(plant.id) },
)

fun HtmlBlockTag.showEcologiesWithElement(
    call: ApplicationCall,
    state: State,
    containsElement: (Ecology) -> Boolean,
) {
    h2 { +"Ecology" }

    showEcologiesWithElement(call, state, state.getRegionStorage(), containsElement)
}

private fun <ID : Id<ID>, ELEMENT> HtmlBlockTag.showEcologiesWithElement(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    containsElement: (Ecology) -> Boolean,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasEcology {
    val ecologies = storage.getAll()
        .filter { containsElement(it.ecology()) }

    fieldElements(call, state, ecologies)
}