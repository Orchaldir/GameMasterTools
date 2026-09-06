package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show


fun HtmlBlockTag.showWeightLookupDetails(
    call: ApplicationCall,
    state: State,
    lookup: WeightLookup,
    vpm: VolumePerMaterial,
    getWeightFromType: () -> Weight,
) {
    showDetails("Weight", true) {
        field("Type", lookup.getType())

        showVolumePerMaterial(call, state, vpm)

        when (lookup) {
            CalculatedWeight -> fieldWeight("Calculated Weight", vpm.getWeight(state))
            is UserDefinedWeight -> fieldWeight("User Defined Weight", lookup.weight)
            WeightBasedOnType -> fieldWeight("Weight based on Type", getWeightFromType())
            UndefinedWeight -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.selectWeightLookup(
    state: State,
    lookup: WeightLookup,
    minWeight: Long,
    maxWeight: Long,
    param: String = WEIGHT,
) {
    showDetails("Weight", true) {
        selectValue("Type", combine(param, TYPE), WeightLookupType.entries, lookup.getType())

        when (lookup) {
            CalculatedWeight -> doNothing()
            is UserDefinedWeight -> selectWeight(
                "User Defined Weight",
                param,
                lookup.weight,
                minWeight,
                maxWeight,
                SiPrefix.Base,
            )

            WeightBasedOnType -> doNothing()
            UndefinedWeight -> doNothing()
        }
    }
}

// parse

fun parseWeightLookup(
    parameters: Parameters,
    minWeight: Long,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), WeightLookupType.UserDefined)) {
    WeightLookupType.Calculated -> CalculatedWeight
    WeightLookupType.UserDefined -> UserDefinedWeight(
        parseWeight(parameters, param, SiPrefix.Base, minWeight),
    )

    WeightLookupType.Type -> WeightBasedOnType
    WeightLookupType.Undefined -> UndefinedWeight
}
