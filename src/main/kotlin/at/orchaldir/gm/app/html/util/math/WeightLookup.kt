package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showWeightLookup(
    lookup: WeightLookup,
    calculate: () -> Weight,
) {
    when (lookup) {
        CalculatedWeight -> +calculate().toString()
        is UserDefinedWeight -> +lookup.weight.toString()
    }
}

fun HtmlBlockTag.showWeightLookupDetails(
    call: ApplicationCall,
    state: State,
    lookup: WeightLookup,
    vpm: VolumePerMaterial,
) {
    showDetails("Weight", true) {
        field("Type", lookup.getType())

        showVolumePerMaterial(call, state, vpm)

        when (lookup) {
            CalculatedWeight -> fieldWeight("Calculated Weight", vpm.getWeight(state))
            is UserDefinedWeight -> fieldWeight("User Defined Weight", lookup.weight)
        }
    }
}

fun HtmlBlockTag.showVolumePerMaterial(
    call: ApplicationCall,
    state: State,
    vpm: VolumePerMaterial,
) {
    table {
        tr {
            th { +"Material" }
            th { +"Volume" }
            th { +"Density" }
            th { +"Weight" }
        }
        vpm.getMap().forEach { (id, volume) ->
            val material = state.getMaterialStorage().getOrThrow(id)
            val weight = Weight.fromVolume(volume, material.density)

            tr {
                tdLink(call, state, material)
                tdString(volume.toString())
                tdString(material.density.toString())
                tdString(weight.toString())
            }
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
        }
    }
}

// parse

fun parseWeightLookup(
    parameters: Parameters,
    minWeight: Long,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), WeightLookupType.Calculated)) {
    WeightLookupType.Calculated -> CalculatedWeight
    WeightLookupType.UserDefined -> UserDefinedWeight(
        parseWeight(parameters, param, SiPrefix.Base, minWeight),
    )
}
