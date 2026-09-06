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

fun HtmlBlockTag.showWeightLookupForType(
    lookup: WeightLookup,
) {
    when (lookup) {
        is UserDefinedWeight -> fieldWeight("Weight", lookup.weight)
        UndefinedWeight -> doNothing()
        else -> error("WeightLookup of type ${lookup.getType()} is not supported!")
    }
}

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

fun HtmlBlockTag.selectWeightLookupForType(
    lookup: WeightLookup,
    minWeight: Long,
    maxWeight: Long,
    param: String = WEIGHT,
) = selectWeightLookup(
    lookup,
    minWeight,
    maxWeight,
    param,
    ALLOWED_WEIGHT_LOOKUP_TYPES_FOR_TYPES,
)

fun HtmlBlockTag.selectWeightLookup(
    lookup: WeightLookup,
    minWeight: Long,
    maxWeight: Long,
    param: String = WEIGHT,
    allowedTypes: Collection<WeightLookupType> = WeightLookupType.entries,
) {
    showDetails("Weight", true) {
        selectValue("Type", combine(param, TYPE), allowedTypes, lookup.getType())

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

fun parseWeightLookupForType(
    parameters: Parameters,
    minWeight: Long,
    param: String = WEIGHT,
) = parseWeightLookup(
    parameters,
    minWeight,
    param,
    ALLOWED_WEIGHT_LOOKUP_TYPES_FOR_TYPES,
)

fun parseWeightLookup(
    parameters: Parameters,
    minWeight: Long,
    param: String = WEIGHT,
    allowedTypes: Collection<WeightLookupType> = WeightLookupType.entries,
) = when (parse(parameters, combine(param, TYPE), allowedTypes)) {
    WeightLookupType.Calculated -> CalculatedWeight
    WeightLookupType.UserDefined -> UserDefinedWeight(
        parseWeight(parameters, param, SiPrefix.Base, minWeight),
    )

    WeightLookupType.Type -> WeightBasedOnType
    WeightLookupType.Undefined -> UndefinedWeight
}
