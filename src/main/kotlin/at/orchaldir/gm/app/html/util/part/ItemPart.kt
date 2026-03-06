package at.orchaldir.gm.app.html.util.part

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.FABRIC
import at.orchaldir.gm.app.FILL
import at.orchaldir.gm.app.LEATHER
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.item.equipment.style.selectMaterial
import at.orchaldir.gm.app.html.util.color.*
import at.orchaldir.gm.app.html.util.parsePercentageDistribution
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Alloy
import at.orchaldir.gm.core.model.economy.material.LeatherGrade
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FabricType
import at.orchaldir.gm.core.model.util.part.FabricWeight
import at.orchaldir.gm.core.model.util.part.FillItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.MadeFromFabric
import at.orchaldir.gm.core.model.util.part.MadeFromLeather
import at.orchaldir.gm.core.model.util.part.UndefinedItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showItemPart(
    call: ApplicationCall,
    state: State,
    part: ItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        field("Type", part.getType())

        when (part) {
            is ColorItemPart -> {
                fieldLink("Material", call, state, part.material)
                fieldOptionalColor(part.color)
            }
            is ColorSchemeItemPart -> {
                fieldLink("Material", call, state, part.material)
                fieldColorLookup("Color", part.lookup)
            }
            is FillItemPart -> {
                fieldLink("Material", call, state, part.material)
                showOptionalFill(part.fill)
            }
            is FillLookupItemPart -> {
                fieldLink("Material", call, state, part.material)
                showFillLookup(part.fill)
            }
            is MadeFromFabric -> {
                fieldLink("Material", call, state, part.material)
                field("Fabric Weight", part.weight)
                field("Fabric Type", part.type)
                showFillLookup(part.fill)
            }
            is MadeFromLeather -> {
                fieldLink("Material", call, state, part.material)
                field("Leather Grade", part.grade)
                fieldColorLookup("Color", part.color)
            }

            UndefinedItemPart -> doNothing()
        }
    }
}

fun HtmlBlockTag.showColorItemPart(
    call: ApplicationCall,
    state: State,
    part: ColorItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        fieldOptionalColor(part.color)
    }
}

fun HtmlBlockTag.showColorSchemeItemPart(
    call: ApplicationCall,
    state: State,
    part: ColorSchemeItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        fieldColorLookup("Color", part.lookup)
    }
}

fun HtmlBlockTag.showFillItemPart(
    call: ApplicationCall,
    state: State,
    part: FillItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        showOptionalFill(part.fill)
    }
}

fun HtmlBlockTag.showFillLookupItemPart(
    call: ApplicationCall,
    state: State,
    part: FillLookupItemPart,
    label: String? = null,
) {
    showDetails(label, true) {
        fieldLink("Material", call, state, part.material)
        showFillLookup(part.fill)
    }
}

// edit

fun HtmlBlockTag.showItemPart(
    state: State,
    part: ItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ItemPartType.entries,
            part.getType(),
        )

        when (part) {
            is ColorItemPart -> {
                selectMaterial(state, param, part.material)
                selectOptionalColor(part.color, combine(param, COLOR))
            }
            is ColorSchemeItemPart -> {
                selectMaterial(state, param, part.material)
                editColorLookup(
                    state,
                    "Color Lookup",
                    part.lookup,
                    combine(param, COLOR),
                    Color.entries,
                )
            }
            is FillItemPart -> {
                selectMaterial(state, param, part.material)
                selectOptionalFill(part.fill, combine(param, FILL))
            }
            is FillLookupItemPart -> {
                selectMaterial(state, param, part.material)
                selectFillLookup(state, part.fill, combine(param, FILL))
            }
            is MadeFromFabric -> {
                selectMaterial(state, param, part.material)
                selectValue(
                    "Fabric Weight",
                    combine(param, FABRIC, WEIGHT),
                    FabricWeight.entries,
                    part.weight,
                )
                selectValue(
                    "Fabric Type",
                    combine(param, FABRIC, TYPE),
                    FabricType.entries,
                    part.type,
                )
                selectFillLookup(state, part.fill, combine(param, FILL))
            }
            is MadeFromLeather -> {
                selectMaterial(state, param, part.material)
                selectValue(
                    "Leather Grade",
                    combine(param, LEATHER, TYPE),
                    LeatherGrade.entries,
                    part.grade,
                )
                editColorLookup(
                    state,
                    "Color Lookup",
                    part.color,
                    combine(param, COLOR),
                    Color.entries,
                )
            }
            UndefinedItemPart -> doNothing()
        }
    }
}

private fun DETAILS.selectMaterial(
    state: State,
    param: String,
    materialId: MaterialId,
) {
    selectMaterial(state, materialId, combine(param, MATERIAL))
}

fun HtmlBlockTag.editColorItemPart(
    state: State,
    part: ColorItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectOptionalColor(part.color, combine(param, COLOR))
    }
}

fun HtmlBlockTag.editColorSchemeItemPart(
    state: State,
    part: ColorSchemeItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        editColorLookup(state, "Color Lookup", part.lookup, combine(param, COLOR), Color.entries)
    }
}

fun HtmlBlockTag.editFillItemPart(
    state: State,
    part: FillItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectOptionalFill(part.fill, combine(param, FILL))
    }
}

fun HtmlBlockTag.editFillLookupItemPart(
    state: State,
    part: FillLookupItemPart,
    param: String,
    label: String? = null,
) {
    showDetails(label, true) {
        selectMaterial(state, part.material, combine(param, MATERIAL))
        selectFillLookup(state, part.fill, combine(param, FILL))
    }
}

// parse

fun parseItemPart(
    state: State,
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), ItemPartType.Undefined)) {
    ItemPartType.Color -> parseColorItemPart(parameters, param)
    ItemPartType.ColorScheme -> parseColorSchemeItemPart(parameters, param)
    ItemPartType.Fill -> parseFillItemPart(parameters, param)
    ItemPartType.FillLookup -> parseFillLookupItemPart(parameters, param)
    ItemPartType.Fabric -> MadeFromFabric(
        parseMaterialId(parameters, combine(param, MATERIAL)),
        parse(parameters, combine(param, FABRIC, WEIGHT), FabricWeight.Medium),
        parse(parameters, combine(param, FABRIC, TYPE), FabricType.Woven),
        parseFillLookup(parameters, combine(param, FILL)),
    )
    ItemPartType.Leather -> MadeFromLeather(
        parseMaterialId(parameters, combine(param, MATERIAL)),
        parse(parameters, combine(param, LEATHER, TYPE), LeatherGrade.Undefined),
        parseColorLookup(parameters, combine(param, COLOR)),
    )
    ItemPartType.Undefined -> UndefinedItemPart
}

fun parseColorItemPart(parameters: Parameters, param: String) = ColorItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parse<Color>(parameters, combine(param, COLOR)),
)

fun parseColorSchemeItemPart(parameters: Parameters, param: String) = ColorSchemeItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseColorLookup(parameters, combine(param, COLOR)),
)

fun parseFillItemPart(parameters: Parameters, param: String) = FillItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseOptionalFill(parameters, combine(param, FILL)),
)

fun parseFillLookupItemPart(parameters: Parameters, param: String) = FillLookupItemPart(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseFillLookup(parameters, combine(param, FILL)),
)