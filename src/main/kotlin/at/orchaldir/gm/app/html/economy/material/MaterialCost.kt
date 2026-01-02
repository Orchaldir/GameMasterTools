package at.orchaldir.gm.app.html.economy.material

import at.orchaldir.gm.app.ADD
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.displayPrice
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.app.html.util.math.selectWeight
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialCost
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

fun HtmlBlockTag.showMaterialCost(
    call: ApplicationCall,
    state: State,
    materialCost: MaterialCost,
) {
    val currency = state.getDefaultCurrency()

    table {
        tr {
            th { +"Material" }
            th { +"Weight" }
            th { +"Price per Kilogram" }
            th { +"Price" }
        }
        materialCost.map.forEach { (id, weight) ->
            val material = state.getMaterialStorage().getOrThrow(id)
            val price = Price.fromWeight(weight, material.pricePerKilogram)

            tr {
                tdLink(call, state, material)
                tdString(weight.toString())
                td {
                    displayPrice(call, currency, material.pricePerKilogram)
                }
                td {
                    displayPrice(call, currency, price)
                }
            }
        }
    }

    fieldWeight("Total Weight", materialCost.calculateWeight())
    fieldPrice(call, state, "Total Price", materialCost.calculatePrice(state))
}


fun HtmlBlockTag.selectMaterialCost(
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
        selectWeight(
            combine(MATERIAL, material.value),
            cost,
            1L,
            Long.MAX_VALUE,
            SiPrefix.Kilo,
        )
    }
}

fun parseMaterialCost(parameters: Parameters): MaterialCost {
    val materialCost = parameters.entries()
        .asSequence()
        .filter { e -> e.key.startsWith(MATERIAL) }
        .associate { e ->
            val parts = e.key.split("-")
            val id = parts[1].toInt()
            Pair(MaterialId(id), Weight.from(SiPrefix.Kilo, e.value.first()))
        }
        .toMutableMap()
    parseOptionalMaterialId(parameters, combine(ADD, MATERIAL))?.let { materialCost.put(it, Weight.fromKilograms(1)) }

    return MaterialCost(materialCost)
}