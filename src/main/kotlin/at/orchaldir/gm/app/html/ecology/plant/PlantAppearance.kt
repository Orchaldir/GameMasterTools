package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.APPEARANCE
import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.TREE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseOptionalMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearanceType
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.UndefinedPlantAppearance
import at.orchaldir.gm.core.model.economy.material.MaterialCategoryType
import at.orchaldir.gm.core.selector.util.sortMaterials
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.displayPlantAppearance(appearance: PlantAppearance) {
    when (appearance) {
        is Tree -> +"Tree"
        UndefinedPlantAppearance -> doNothing()
    }
}

fun HtmlBlockTag.showPlantAppearance(
    call: ApplicationCall,
    state: State,
    appearance: PlantAppearance,
) {
    showDetails("Appearance", true) {
        field("Type", appearance.getType())

        when (appearance) {
            is Tree -> optionalFieldLink("Wood", call, state, appearance.wood)
            UndefinedPlantAppearance -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.editPlantAppearance(
    state: State,
    appearance: PlantAppearance,
    param: String = APPEARANCE,
) {
    val woods = state.sortMaterials(MaterialCategoryType.Wood)

    showDetails("Appearance", true) {
        selectValue(
            "Type",
            param,
            PlantAppearanceType.entries,
            appearance.getType(),
        ) {
            when (it) {
            PlantAppearanceType.Tree -> woods.isEmpty()
            PlantAppearanceType.Undefined -> false
        }

        }

        when (appearance) {
            is Tree -> selectOptionalElement(
                state,
                "Wood",
                combine(param, TREE, MATERIAL),
                woods,
                appearance.wood,
            )

            UndefinedPlantAppearance -> doNothing()
        }
    }
}


// parse

fun parsePlantAppearance(
    parameters: Parameters,
    state: State,
    param: String = APPEARANCE,
) = when (parse(parameters, param, PlantAppearanceType.Undefined)) {
    PlantAppearanceType.Tree -> Tree(
        parseOptionalMaterialId(parameters, combine(param, TREE, MATERIAL)),
    )

    PlantAppearanceType.Undefined -> UndefinedPlantAppearance
}
