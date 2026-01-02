package at.orchaldir.gm.app.html.economy.material

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.DENSITY
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.selectPrice
import at.orchaldir.gm.app.html.util.math.fieldWeight
import at.orchaldir.gm.app.html.util.math.parseWeight
import at.orchaldir.gm.app.html.util.math.selectWeight
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCategory
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.item.getTextsMadeOf
import at.orchaldir.gm.core.selector.race.getRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showMaterial(
    call: ApplicationCall,
    state: State,
    material: Material,
) {
    fieldName(material.name)
    field("Category", material.category)
    fieldColor(material.color)
    fieldWeight("Density", material.density)
    fieldPrice(call, state, "Price Per Kilogram", material.pricePerKilogram)

    showUsage(call, state, material)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    material: Material,
) {
    val currencyUnits = state.getCurrencyUnits(material.id)
    val equipmentList = state.getEquipmentMadeOf(material.id)
    val moons = state.getMoonsContaining(material.id)
    val regions = state.getRegionsContaining(material.id)
    val raceAppearances = state.getRaceAppearancesMadeOf(material.id)
    val streetTemplates = state.getStreetTemplatesMadeOf(material.id)
    val texts = state.getTextsMadeOf(material.id)

    if (currencyUnits.isEmpty() && equipmentList.isEmpty() && moons.isEmpty() && regions.isEmpty() && raceAppearances.isEmpty() && streetTemplates.isEmpty() && texts.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, currencyUnits)
    fieldElements(call, state, equipmentList)
    fieldElements(call, state, moons)
    fieldElements(call, state, regions)
    fieldElements(call, state, raceAppearances)
    fieldElements(call, state, streetTemplates)
    fieldList("Texts", texts) { text ->
        link(call, text, text.getNameWithDate(state))
    }
}

// edit

fun HtmlBlockTag.editMaterial(
    call: ApplicationCall,
    state: State,
    material: Material,
) {
    selectName(material.name)
    selectValue("Category", CATEGORY, MaterialCategory.entries, material.category)
    selectColor(material.color)
    selectWeight(
        "Density",
        DENSITY,
        material.density,
        1,
        25000,
        SiPrefix.Kilo,
    )
    selectPrice(
        state,
        "Price Per Kilogram",
        material.pricePerKilogram,
        PRICE,
        0,
        Int.MAX_VALUE,
    )
}

// parse

fun parseMaterialId(value: String) = MaterialId(value.toInt())
fun parseMaterialId(parameters: Parameters, param: String) = MaterialId(parseInt(parameters, param))
fun parseOptionalMaterialId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { MaterialId(it) }

fun parseMaterial(
    state: State,
    parameters: Parameters,
    id: MaterialId,
) = Material(
    id,
    parseName(parameters),
    parse(parameters, CATEGORY, MaterialCategory.Metal),
    parse(parameters, COLOR, Color.Pink),
    parseWeight(parameters, DENSITY, SiPrefix.Kilo),
    parsePrice(state, parameters, PRICE),
)
