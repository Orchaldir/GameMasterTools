package at.orchaldir.gm.app.html.model.economy.material

import at.orchaldir.gm.app.ADD
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectOptionalElement
import at.orchaldir.gm.app.html.showMap
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.material.MaterialId
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
    val newMaterials = state.getMaterialStorage()
        .getAll()
        .filter { !materialCost.contains(it.id) }
    selectOptionalElement(state, "Add Material", combine(ADD, MATERIAL), newMaterials, null)
    showMap("Material Cost", materialCost.map) { material, cost ->
        link(call, state, material)
        +": "
        selectInt(cost, 0, Int.MAX_VALUE, 1, combine(MATERIAL, material.value))
    }
}

fun parseMaterialCost(parameters: Parameters): MaterialCost {
    val materialCost = parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(MATERIAL) }
        .associate { e ->
            val parts = e.key.split("-")
            val id = parts[1].toInt()
            Pair(MaterialId(id), e.value.first().toInt())
        }
        .toMutableMap()
    parseOptionalMaterialId(parameters, combine(ADD, MATERIAL))?.let { materialCost.put(it, 1) }

    return MaterialCost.init(
        materialCost
    )
}