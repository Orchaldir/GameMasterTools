package at.orchaldir.gm.app.html.ecology

import at.orchaldir.gm.app.ECOLOGY
import at.orchaldir.gm.app.PLANT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.ecology.plant.parsePlantId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.*
import at.orchaldir.gm.core.selector.util.sortPlants
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEcology(
    call: ApplicationCall,
    state: State,
    ecology: Ecology,
) {
    showDetails("Ecology", true) {
        field("Type", ecology.getType())

        when (ecology) {
            is EcologyWithSets -> fieldIds(call, state, ecology.plants)
            is EcologyWithRarity -> showRarityMap("Plants", ecology.plants, true) { id ->
                link(call, state, id)
            }

            UndefinedEcology -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.editEcology(
    state: State,
    ecology: Ecology,
    param: String = ECOLOGY,
) {
    showDetails("Ecology", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            EcologyType.entries,
            ecology.getType(),
        )

        when (ecology) {
            is EcologyWithSets -> selectElements(
                state,
                "Plants",
                combine(param, PLANT),
                state.sortPlants(),
                ecology.plants,
            )

            is EcologyWithRarity -> selectRarityMap(
                "Plants",
                combine(param, PLANT),
                state.getPlantStorage(),
                ecology.plants,
            )

            UndefinedEcology -> doNothing()
        }
    }
}

// parse

fun parseEcology(
    parameters: Parameters,
    param: String = ECOLOGY,
) = when (parse(parameters, combine(param, TYPE), EcologyType.Undefined)) {
    EcologyType.Sets -> EcologyWithSets(
        parseElements(parameters, combine(param, PLANT), ::parsePlantId),
    )

    EcologyType.Rarity -> EcologyWithRarity(
        parseSomeOf(parameters, combine(param, PLANT), ::parsePlantId),
    )

    EcologyType.Undefined -> UndefinedEcology
}
