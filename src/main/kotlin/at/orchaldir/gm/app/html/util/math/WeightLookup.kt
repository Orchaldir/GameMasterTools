package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.selector.item.equipment.VOLUME_CONFIG
import at.orchaldir.gm.core.selector.item.equipment.calculateVolumePerMaterial
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.FixedWeight
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.UndefinedWeight
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import at.orchaldir.gm.utils.math.unit.WeightLookupType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWeightLookup(
    lookup: WeightLookup,
    showUndefined: Boolean = false,
    calculate: () -> Weight,
) {
    when (lookup) {
        CalculatedWeight -> +calculate().toString()
        is FixedWeight -> +lookup.weight.toString()
        UndefinedWeight -> if (showUndefined) {
            +"Undefined"
        }
    }
}

fun HtmlBlockTag.showWeightLookupDetails(
    call: ApplicationCall,
    state: State,
    lookup: WeightLookup,
    calculate: () -> VolumePerMaterial,
) {
    showDetails("Weight", true) {
        field("Type", lookup.getType())

        when (lookup) {
            CalculatedWeight -> {
                val vpm = calculate()

                fieldWeight("Weight", vpm.getWeight(state))
            }
            is FixedWeight -> fieldWeight("Weight", lookup.weight)
            UndefinedWeight -> doNothing()
        }
    }
}

// edit

fun HtmlBlockTag.selectWeightLookup(
    state: State,
    lookup: WeightLookup,
    param: String = WEIGHT,
) {
    showDetails("Weight", true) {
        selectValue("Type", combine(param, TYPE), WeightLookupType.entries, lookup.getType())

        when (lookup) {
            CalculatedWeight -> doNothing()
            is FixedWeight -> selectWeight(
                "Weight",
                param,
                lookup.weight,
                MIN_EQUIPMENT_WEIGHT,
                MAX_EQUIPMENT_WEIGHT,
                SiPrefix.Base,
            )
            UndefinedWeight -> doNothing()
        }
    }
}

// parse

fun parseWeightLookup(
    parameters: Parameters,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), WeightLookupType.Undefined)) {
    WeightLookupType.Calculated -> CalculatedWeight
    WeightLookupType.Fixed -> FixedWeight(
        parseWeight(parameters, param, SiPrefix.Base, MIN_EQUIPMENT_WEIGHT),
    )
    WeightLookupType.Undefined -> UndefinedWeight
}
