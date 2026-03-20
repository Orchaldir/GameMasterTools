package at.orchaldir.gm.app.html.economy.material

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.race.appearance.editHairColorOptions
import at.orchaldir.gm.app.html.race.appearance.parseHairColorOptions
import at.orchaldir.gm.app.html.race.appearance.showHairColorOptions
import at.orchaldir.gm.app.html.util.editPercentageDistribution
import at.orchaldir.gm.app.html.util.parsePercentageDistribution
import at.orchaldir.gm.app.html.util.showPercentageDistribution
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.*
import at.orchaldir.gm.core.model.race.appearance.ALLOWED_FUR_COLOR_TYPES
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayMaterialCategory(
    call: ApplicationCall,
    state: State,
    category: MaterialCategory,
) = when (category) {
    is UndefinedMaterialCategory -> doNothing()
    is Alloy -> {
        +"Alloy of "
        showInlineIds(call, state, category.components.map.keys)
    }

    is Rock -> +"${category.type} ${category.type}"
    else -> +category.getType().name
}

fun HtmlBlockTag.showMaterialCategory(
    call: ApplicationCall,
    state: State,
    category: MaterialCategory,
) {
    showDetails("Category", true) {
        field("Type", category.getType())

        when (category) {
            is Alloy -> {
                fieldColor(category.color)
                showPercentageDistribution(call, state, "Components", category.components)
            }

            is Fiber -> {
                fieldColor(category.color)
                field("Weight", category.weight)
            }

            is Fur -> {
                showHairColorOptions(category.colors, "Fur Color")
                field("Thickness", category.thickness)
            }

            is Glass -> {
                fieldColor(category.color)
                field("Transparency", category.transparency)
            }

            is Hide -> {
                fieldColor(category.color)
                field("Thickness", category.thickness)
            }

            is Leather -> {
                fieldColor(category.color)
                optionalFieldLink("Hide", call, state, category.hide)
                field("Grade", category.grade)
                field("Thickness", category.thickness)
            }

            is Metal -> fieldColor(category.color)
            is Mineral -> {
                showColorRarityMap("Colors", category.colors)
                field("Transparency", category.transparency)
            }

            is Paper -> fieldColor(category.color)
            is Rock -> {
                showColorRarityMap("Colors", category.colors)
                field("Type", category.type)
                fieldIds(call, state, "Components", category.components)
            }

            is Wood -> fieldColor(category.color)
            UndefinedMaterialCategory -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.editMaterialCategory(
    call: ApplicationCall,
    state: State,
    category: MaterialCategory,
) {
    val materialsForAlloy = state.sortMaterials(CATEGORIES_FOR_ALLOY)
    val materialsForRock = state.sortMaterials(CATEGORIES_FOR_ROCK)

    showDetails("Category", true) {
        selectValue(
            "Type",
            combine(CATEGORY, TYPE),
            MaterialCategoryType.entries,
            category.getType(),
        ) {
            when (it) {
                MaterialCategoryType.Alloy -> materialsForAlloy.size < 2
                MaterialCategoryType.Rock -> materialsForRock.size < 2
                else -> false
            }
        }

        when (category) {
            is Alloy -> {
                selectMaterialColor(category.color)
                editPercentageDistribution(
                    call,
                    state,
                    "Components",
                    combine(CATEGORY, MATERIAL),
                    materialsForAlloy,
                    category.components,
                )
            }

            is Fiber -> {
                selectMaterialColor(category.color)
                selectValue(
                    "Weight",
                    combine(CATEGORY, SIZE),
                    Size.entries,
                    category.weight,
                )
            }

            is Fur -> {
                editHairColorOptions(
                    category.colors,
                    combine(CATEGORY, FUR),
                    "Fur Colors",
                    ALLOWED_FUR_COLOR_TYPES,
                )
                selectLeatherThickness(category.thickness)
            }

            is Glass -> {
                selectMaterialColor(category.color)
                selectTransparency(category.transparency)
            }

            is Hide -> {
                selectMaterialColor(category.color)
                selectLeatherThickness(category.thickness)
            }

            is Leather -> {
                selectMaterialColor(category.color)
                selectOptionalElement(
                    state,
                    "Hide",
                    combine(CATEGORY, MATERIAL),
                    state.sortMaterials(), // get hides
                    category.hide,
                )
                selectValue(
                    "Grade",
                    combine(CATEGORY, LEATHER, TYPE),
                    LeatherGrade.entries,
                    category.grade,
                )
                selectLeatherThickness(category.thickness)
            }

            is Metal -> selectMaterialColor(category.color)
            is Mineral -> {
                selectMaterialColors(category.colors)
                selectTransparency(category.transparency)
            }

            is Paper -> selectMaterialColor(category.color)
            is Rock -> {
                selectMaterialColors(category.colors)
                selectValue(
                    "Rock Type",
                    combine(CATEGORY, TYPE, TYPE),
                    RockType.entries,
                    category.type,
                )
                selectElements(
                    state,
                    "Components",
                    combine(CATEGORY, MATERIAL, LIST),
                    materialsForRock,
                    category.components,
                )
            }

            is Wood -> selectMaterialColor(category.color)
            UndefinedMaterialCategory -> doNothing()
        }
    }
}

private fun DETAILS.selectTransparency(transparency: Transparency) {
    selectValue(
        "Transparency",
        combine(CATEGORY, OPACITY),
        Transparency.entries,
        transparency,
    )
}

private fun DETAILS.selectMaterialColors(colors: OneOf<Color>) {
    selectColorRarityMap(
        "Colors",
        combine(CATEGORY, COLOR, MAP),
        colors,
    )
}

private fun DETAILS.selectMaterialColor(color: Color) {
    selectColor(
        color,
        combine(CATEGORY, COLOR),
    )
}

private fun DETAILS.selectLeatherThickness(thickness: LeatherThickness) {
    selectValue(
        "Thickness",
        combine(CATEGORY, THICKNESS),
        LeatherThickness.entries,
        thickness,
    )
}

// parse

fun parseMaterialCategory(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, combine(CATEGORY, TYPE), MaterialCategoryType.Undefined)) {
    MaterialCategoryType.Alloy -> Alloy(
        parseMaterialColor(parameters, Color.Gray),
        parsePercentageDistribution(
            state.getMaterialStorage(),
            parameters,
            combine(CATEGORY, MATERIAL),
        )
    )

    MaterialCategoryType.Fiber -> Fiber(
        parseMaterialColor(parameters, Color.White),
        parse(parameters, combine(CATEGORY, SIZE), Size.Medium),
    )

    MaterialCategoryType.Fur -> Fur(
        parseHairColorOptions(parameters, combine(CATEGORY, FUR)),
        parseThickness(parameters),
    )

    MaterialCategoryType.Glass -> Glass(
        parseMaterialColor(parameters, Color.SkyBlue),
        selectTransparency(parameters, Transparency.Transparent),
    )

    MaterialCategoryType.Hide -> Hide(
        parseMaterialColor(parameters, Color.SaddleBrown),
        parseThickness(parameters),
    )

    MaterialCategoryType.Leather -> Leather(
        parseMaterialColor(parameters, Color.SaddleBrown),
        parseOptionalMaterialId(parameters, combine(CATEGORY, MATERIAL)),
        parse(
            parameters,
            combine(CATEGORY, LEATHER, TYPE),
            LeatherGrade.Undefined,
        ),
        parseThickness(parameters),
    )

    MaterialCategoryType.Metal -> Metal(
        parseMaterialColor(parameters, Color.SkyBlue),
    )

    MaterialCategoryType.Mineral -> Mineral(
        parseMaterialColors(parameters, Color.Gray),
        selectTransparency(parameters, Transparency.Opaque),
    )

    MaterialCategoryType.Paper -> Paper(
        parseMaterialColor(parameters, Color.SkyBlue),
    )

    MaterialCategoryType.Rock -> Rock(
        parseMaterialColors(parameters, Color.Gray),
        parseElements(
            parameters,
            combine(CATEGORY, MATERIAL, LIST),
            ::parseMaterialId,
        ),
        parse(
            parameters,
            combine(CATEGORY, TYPE, TYPE),
            RockType.Undefined,
        ),
    )

    MaterialCategoryType.Wood -> Wood(
        parseMaterialColor(parameters, Color.Teal),
    )

    MaterialCategoryType.Undefined -> UndefinedMaterialCategory
}

private fun selectTransparency(
    parameters: Parameters,
    default: Transparency,
) = parse(parameters, combine(CATEGORY, OPACITY), default)

private fun parseMaterialColors(parameters: Parameters, default: Color): OneOf<Color> =
    parseColorOneOf(parameters, combine(CATEGORY, COLOR, MAP), setOf(default))

private fun parseMaterialColor(parameters: Parameters, default: Color) =
    parse(parameters, combine(CATEGORY, COLOR), default)

private fun parseThickness(parameters: Parameters): LeatherThickness =
    parse(parameters, combine(CATEGORY, THICKNESS), LeatherThickness.Medium)
