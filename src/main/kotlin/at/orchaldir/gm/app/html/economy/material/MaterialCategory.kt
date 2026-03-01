package at.orchaldir.gm.app.html.economy.material

import at.orchaldir.gm.app.CATEGORY
import at.orchaldir.gm.app.CULTURE
import at.orchaldir.gm.app.INCOME
import at.orchaldir.gm.app.LEATHER
import at.orchaldir.gm.app.LIST
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.PRICE
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STANDARD
import at.orchaldir.gm.app.THICKNESS
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parsePrice
import at.orchaldir.gm.app.html.economy.parseStandardOfLivingId
import at.orchaldir.gm.app.html.util.editPercentageDistribution
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.parsePercentageDistribution
import at.orchaldir.gm.app.html.util.showPercentageDistribution
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.model.economy.material.Alloy
import at.orchaldir.gm.core.model.economy.material.Fiber
import at.orchaldir.gm.core.model.economy.material.Hide
import at.orchaldir.gm.core.model.economy.material.Leather
import at.orchaldir.gm.core.model.economy.material.LeatherGrade
import at.orchaldir.gm.core.model.economy.material.LeatherThickness
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialCategory
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
import at.orchaldir.gm.core.model.economy.material.Metal
import at.orchaldir.gm.core.model.economy.material.Mineral
import at.orchaldir.gm.core.model.economy.material.Paper
import at.orchaldir.gm.core.model.economy.material.Rock
import at.orchaldir.gm.core.model.economy.material.RockType
import at.orchaldir.gm.core.model.economy.material.UndefinedMaterialCategory
import at.orchaldir.gm.core.model.economy.material.Wood
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayMaterialCategory(category: MaterialCategory, ) = when (category) {
    is UndefinedMaterialCategory -> doNothing()
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
            is Alloy -> showPercentageDistribution(call, state, "Components", category.components)
            is Fiber -> field("Weight", category.weight)
            is Hide -> field("Thickness", category.thickness)
            is Leather -> {
                optionalFieldLink("Hide", call, state, category.hide)
                field("Grade", category.grade)
                field("Thickness", category.thickness)
            }
            Metal -> doNothing()
            Mineral -> doNothing()
            Paper -> doNothing()
            is Rock -> {
                field("Type", category.type)
                fieldIds( call, state, "Components", category.components)
            }
            Wood -> doNothing()
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
    showDetails("Category", true) {
        selectValue(
            "Type",
            combine(CATEGORY, TYPE),
            MaterialCategoryType.entries,
            category.getType(),
        )
        when (category) {
            is Alloy -> editPercentageDistribution(
                call,
                state,
                "Components",
                combine(CATEGORY, MATERIAL),
                state.sortMaterials(), // get metals
                category.components,
            )
            is Fiber -> selectValue(
                "Weight",
                combine(CATEGORY, SIZE),
                Size.entries,
                category.weight,
            )
            is Hide -> selectLeatherThickness(category.thickness)
            is Leather -> {
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
            Metal -> doNothing()
            Mineral -> doNothing()
            Paper -> doNothing()
            is Rock -> {
                selectValue(
                    "Rock Type",
                    combine(CATEGORY, TYPE, TYPE),
                    RockType.entries,
                    category.type,
                )
                selectElements(
                    state,
                    "Hide",
                    combine(CATEGORY, MATERIAL, LIST),
                    state.sortMaterials(), // get minerals
                    category.components,
                )
            }
            Wood -> doNothing()
            UndefinedMaterialCategory -> doNothing()
        }
    }
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
        parsePercentageDistribution(
            state.getMaterialStorage(),
            parameters,
            combine(CATEGORY, MATERIAL),
        )
    )
    MaterialCategoryType.Fiber -> Fiber(
        parse(parameters, combine(CATEGORY, SIZE), Size.Medium),
    )
    MaterialCategoryType.Hide -> Hide(
        parseThickness(parameters),
    )
    MaterialCategoryType.Leather -> Leather(
        parseOptionalMaterialId(parameters, combine(CATEGORY, MATERIAL)),
        parse(
            parameters,
            combine(CATEGORY, LEATHER, TYPE),
            LeatherGrade.Undefined,
        ),
        parseThickness(parameters),
    )
    MaterialCategoryType.Metal -> Metal
    MaterialCategoryType.Mineral -> Mineral
    MaterialCategoryType.Paper -> Paper
    MaterialCategoryType.Rock -> Rock(
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
    MaterialCategoryType.Wood -> Wood
    MaterialCategoryType.Undefined -> UndefinedMaterialCategory
}

private fun parseThickness(parameters: Parameters): LeatherThickness =
    parse(parameters, combine(CATEGORY, THICKNESS), LeatherThickness.Medium)
