package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialCost
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showMaterialCost(
    call: ApplicationCall,
    state: State,
    materialCost: MaterialCost,
) {
    showMap("Material Cost", materialCost.map) { material, cost ->
        link(call, state, material)
        +": $cost"
    }
}


fun FORM.selectMaterialCost(
    state: State,
    cost: MaterialCost,
) {

}

fun parseMaterialCost(parameters: Parameters) = MaterialCost()