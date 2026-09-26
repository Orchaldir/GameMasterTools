package at.orchaldir.gm.app.html.economy.material

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.fieldPrice
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.money.selectPrice
import at.orchaldir.gm.app.html.economy.properties.editMaterialProperties
import at.orchaldir.gm.app.html.economy.properties.parseMaterialProperties
import at.orchaldir.gm.app.html.economy.properties.showMaterialProperties
import at.orchaldir.gm.app.html.util.showGenericUsage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.*
import at.orchaldir.gm.core.selector.ecology.plant.getPlantsMadeOf
import at.orchaldir.gm.core.selector.economy.getFirstMaterial
import at.orchaldir.gm.core.selector.economy.getMaterialsMadeOf
import at.orchaldir.gm.core.selector.economy.money.getCurrencyUnits
import at.orchaldir.gm.core.selector.item.equipment.getEquipment
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMadeOf
import at.orchaldir.gm.core.selector.item.getTextsMadeOf
import at.orchaldir.gm.core.selector.race.getRaceAppearancesMadeOf
import at.orchaldir.gm.core.selector.util.getColorSchemeGroups
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.core.selector.world.getMoonsContaining
import at.orchaldir.gm.core.selector.world.getRegionsContaining
import at.orchaldir.gm.core.selector.world.getStreetTemplatesMadeOf
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
    showMaterialProperties(call, state, material.properties)
    fieldPrice(call, state, "Price Per Kilogram", material.pricePerKilogram)

    showUsage(call, state, material)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    material: Material,
) = showGenericUsage(
    call,
    state,
    listOf(
        state.getCurrencyUnits(material.id),
        state.getEquipmentMadeOf(material.id),
        state.getMaterialsMadeOf(material.id),
        state.getMoonsContaining(material.id),
        state.getPlantsMadeOf(material.id),
        state.getRegionsContaining(material.id),
        state.getRaceAppearancesMadeOf(material.id),
        state.getStreetTemplatesMadeOf(material.id),
        state.getTextsMadeOf(material.id),
    ),
)

// edit

fun HtmlBlockTag.editMaterial(
    call: ApplicationCall,
    state: State,
    material: Material,
) {
    selectName(material.name)
    editMaterialProperties(call, state, material.properties)
    selectPrice(
        state,
        "Price Per Kilogram",
        material.pricePerKilogram,
        PRICE,
        MIN_MATERIAL_PRICE,
        MAX_MATERIAL_PRICE,
    )
}

fun HtmlBlockTag.selectMaterial(
    state: State,
    current: MaterialId,
    param: String = MATERIAL,
    label: String = "Material",
) = selectMaterial(
    state,
    state.sortMaterials(),
    current,
    param,
    label,
)

fun HtmlBlockTag.selectMaterial(
    state: State,
    materials: Collection<Material>,
    current: MaterialId,
    param: String = MATERIAL,
    label: String = "Material",
) {
    selectElement(state, label, param, materials, current)
}

// parse

fun parseMaterialId(value: String) = MaterialId(value.toInt())
fun parseMaterialId(parameters: Parameters, param: String) = MaterialId(parseInt(parameters, param))
fun parseOptionalMaterialId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { MaterialId(it) }

fun parseMaterialId(
    state: State,
    parameters: Parameters,
    param: String,
    category: MaterialCategoryType,
) = parseOptionalMaterialId(parameters, param)
    ?: state.getFirstMaterial(category).id

fun parseMaterialId(
    state: State,
    parameters: Parameters,
    param: String,
    categories: Set<MaterialCategoryType>,
) = parseOptionalMaterialId(parameters, param)
    ?: state.getFirstMaterial(categories).id

fun parseMaterial(
    state: State,
    parameters: Parameters,
    id: MaterialId,
) = Material(
    id,
    parseName(parameters),
    parseMaterialProperties(state, parameters),
    parsePrice(state, parameters, PRICE),
)
