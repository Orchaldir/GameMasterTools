package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.showMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.material.MaterialCost
import at.orchaldir.gm.core.model.material.MaterialId
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
    call: ApplicationCall,
    state: State,
    materialCost: MaterialCost,
) {
    showMap("Material Cost", materialCost.map) { material, cost ->
        link(call, state, material)
        +": "
        selectInt(cost, 0, Int.MAX_VALUE, 1, combine(MATERIAL, material.value))
    }
}

fun parseMaterialCost(parameters: Parameters) = MaterialCost.init(
    parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(MATERIAL) }
        .associate { e ->
            val parts = e.key.split("-")
            val id = parts[1].toInt()
            Pair(MaterialId(id), e.value.first().toInt())
        }
)