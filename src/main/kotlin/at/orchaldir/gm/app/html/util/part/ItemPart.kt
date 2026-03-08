package at.orchaldir.gm.app.html.util.part

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.economy.material.selectMaterial
import at.orchaldir.gm.app.html.util.color.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.*
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.ColorLookup
import at.orchaldir.gm.core.selector.util.sortMaterials
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

private const val TEXT = "Made Out Of"

// show

fun HtmlBlockTag.showItemPart(
    call: ApplicationCall,
    state: State,
    part: ItemPart,
    label: String = TEXT,
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

            is MadeFromCord -> {
                fieldLink("Material", call, state, part.material)
                fieldColorLookup("Color", part.color)
            }

            is MadeFromFabric -> {
                fieldLink("Material", call, state, part.material)
                field("Fabric Weight", part.weight)
                field("Fabric Type", part.type)
                showFillLookup(part.fill)
            }

            is MadeFromGem -> fieldLink("Material", call, state, part.material)
            is MadeFromGlass -> {
                fieldLink("Material", call, state, part.material)
                fieldColorLookup("Color", part.color)
                fieldFactor("Opacity", part.opacity)
            }

            is MadeFromLeather -> {
                fieldLink("Material", call, state, part.material)
                field("Leather Grade", part.grade)
                fieldColorLookup("Color", part.color)
            }

            is MadeFromMetal -> fieldLink("Material", call, state, part.material)
            is MadeFromPaper -> {
                fieldLink("Material", call, state, part.material)
                fieldColorLookup("Color", part.color)
            }

            is MadeFromWood -> {
                fieldLink("Material", call, state, part.material)
                showFillLookup(part.fill)
            }
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

fun HtmlBlockTag.editItemPart(
    state: State,
    part: ItemPart,
    param: String,
    label: String = TEXT,
    allowedType: ItemPartType,
) = editItemPart(
    state,
    part,
    param,
    label,
    setOf(allowedType),
)

fun HtmlBlockTag.editItemPart(
    state: State,
    part: ItemPart,
    param: String,
    label: String = TEXT,
    allowedTypes: Collection<ItemPartType> = ItemPartType.entries,
) {
    val fibers = state.sortMaterials(MaterialCategoryType.Fiber)
    val gems = state.sortMaterials(CATEGORIES_FOR_GEM)
    val glasses = state.sortMaterials(MaterialCategoryType.Glass)
    val leathers = state.sortMaterials(MaterialCategoryType.Leather)
    val metals = state.sortMaterials(ALLOYS_OR_METALS)
    val papers = state.sortMaterials(MaterialCategoryType.Paper)
    val woods = state.sortMaterials(MaterialCategoryType.Wood)

    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            allowedTypes,
            part.getType(),
        ) {
            when (it) {
                ItemPartType.Cord -> fibers.isEmpty() && leathers.isEmpty()
                ItemPartType.Fabric -> fibers.isEmpty()
                ItemPartType.Gem -> gems.isEmpty()
                ItemPartType.Glass -> glasses.isEmpty()
                ItemPartType.Leather -> leathers.isEmpty()
                ItemPartType.Metal -> metals.isEmpty()
                ItemPartType.Paper -> papers.isEmpty()
                ItemPartType.Wood -> woods.isEmpty()
                else -> false
            }
        }

        when (part) {
            is ColorItemPart -> {
                selectMaterial(state, part.material, param)
                selectOptionalColor(part.color, combine(param, COLOR))
            }

            is ColorSchemeItemPart -> selectColorSchemeItemParts(
                state,
                part,
                param,
                state.getMaterialStorage().getAll()
            )

            is FillItemPart -> {
                selectMaterial(state, part.material, param)
                selectOptionalFill(part.fill, combine(param, FILL))
            }

            is FillLookupItemPart -> {
                selectMaterial(state, part.material, param)
                selectFillLookup(state, part.fill, combine(param, FILL))
            }

            is MadeFromCord -> {
                selectMaterial(state, param, part.material, fibers + leathers)
                selectColor(state, param, part.color)
            }

            is MadeFromFabric -> {
                selectMaterial(state, param, part.material, fibers)
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

            is MadeFromGem -> selectMaterial(state, param, part.material, gems)
            is MadeFromGlass -> {
                selectMaterial(state, param, part.material, glasses)
                selectColor(state, param, part.color)
                selectFactor("Opacity", combine(param, OPACITY), part.opacity)
            }

            is MadeFromLeather -> {
                selectMaterial(state, param, part.material, leathers)
                selectValue(
                    "Leather Grade",
                    combine(param, LEATHER, TYPE),
                    LeatherGrade.entries,
                    part.grade,
                )
                selectColor(state, param, part.color)
            }

            is MadeFromMetal -> selectMaterial(state, param, part.material, metals)
            is MadeFromPaper -> {
                selectMaterial(state, param, part.material, papers)
                selectColor(state, param, part.color)
            }

            is MadeFromWood -> {
                selectMaterial(state, param, part.material, woods)
                selectFillLookup(state, part.fill, combine(param, FILL))
            }
        }
    }
}

private fun DETAILS.selectColor(
    state: State,
    param: String,
    lookup: ColorLookup,
) = editColorLookup(
    state,
    "Color Lookup",
    lookup,
    combine(param, COLOR),
    Color.entries,
)

private fun HtmlBlockTag.selectMaterial(
    state: State,
    param: String,
    current: MaterialId,
    materials: Collection<Material>,
) = selectMaterial(state, materials, current, combine(param, MATERIAL))

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

fun HtmlBlockTag.selectColorSchemeItemParts(
    state: State,
    part: ColorSchemeItemPart,
    param: String,
    materials: Collection<Material>,
) {
    selectMaterial(state, param, part.material, materials)
    editColorLookup(
        state,
        "Color Lookup",
        part.lookup,
        combine(param, COLOR),
        Color.entries,
    )
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
    parameters: Parameters,
    param: String,
    allowedTypes: List<ItemPartType>,
) = parseItemPart(
    parameters,
    param,
    allowedTypes.first(),
)

fun parseItemPart(
    parameters: Parameters,
    param: String,
    default: ItemPartType = ItemPartType.Color,
) = when (parse(parameters, combine(param, TYPE), default)) {
    ItemPartType.Color -> parseColorItemPart(parameters, param)
    ItemPartType.ColorScheme -> parseColorSchemeItemPart(parameters, param)
    ItemPartType.Fill -> parseFillItemPart(parameters, param)
    ItemPartType.FillLookup -> parseFillLookupItemPart(parameters, param)
    ItemPartType.Cord -> parseMadeFromCord(parameters, param)
    ItemPartType.Fabric -> MadeFromFabric(
        parseMaterialId(parameters, combine(param, MATERIAL)),
        parse(parameters, combine(param, FABRIC, WEIGHT), FabricWeight.Medium),
        parse(parameters, combine(param, FABRIC, TYPE), FabricType.Woven),
        parseFillLookup(parameters, combine(param, FILL)),
    )

    ItemPartType.Gem -> parseMadeFromGem(parameters, param)
    ItemPartType.Glass -> parseGlass(parameters, param)
    ItemPartType.Leather -> parseLeather(parameters, param)
    ItemPartType.Metal -> parseMadeFromMetal(parameters, param)
    ItemPartType.Paper -> parsePaper(parameters, param)
    ItemPartType.Wood -> MadeFromWood(
        parseMaterialId(parameters, combine(param, MATERIAL)),
        parseFillLookup(parameters, combine(param, FILL)),
    )
}

fun parseMadeFromCord(
    parameters: Parameters,
    param: String,
) = MadeFromCord(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseColorLookup(parameters, combine(param, COLOR)),
)

fun parseMadeFromGem(
    parameters: Parameters,
    param: String,
) = MadeFromGem(
    parseMaterialId(parameters, combine(param, MATERIAL)),
)

fun parseGlass(
    parameters: Parameters,
    param: String,
) = MadeFromGlass(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseColorLookup(parameters, combine(param, COLOR)),
    parseFactor(parameters, combine(param, OPACITY)),
)

fun parseLeather(
    parameters: Parameters,
    param: String,
) = MadeFromLeather(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parse(parameters, combine(param, LEATHER, TYPE), LeatherGrade.Undefined),
    parseColorLookup(parameters, combine(param, COLOR)),
)

fun parseMadeFromMetal(
    parameters: Parameters,
    param: String,
) = MadeFromMetal(
    parseMaterialId(parameters, combine(param, MATERIAL)),
)

fun parsePaper(
    parameters: Parameters,
    param: String,
) = MadeFromPaper(
    parseMaterialId(parameters, combine(param, MATERIAL)),
    parseColorLookup(parameters, combine(param, COLOR)),
)

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