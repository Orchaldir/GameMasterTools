package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.FixedWeight
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import at.orchaldir.gm.utils.math.unit.WeightLookupType
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
        is FixedWeight -> +lookup.weight.toString()
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

                showVolumePerMaterial(call, state, vpm)

                fieldWeight("Weight", vpm.getWeight(state))
            }
            is FixedWeight -> fieldWeight("Weight", lookup.weight)
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
        }
    }
}

// parse

fun parseWeightLookup(
    parameters: Parameters,
    param: String = WEIGHT,
) = when (parse(parameters, combine(param, TYPE), WeightLookupType.Calculated)) {
    WeightLookupType.Calculated -> CalculatedWeight
    WeightLookupType.Fixed -> FixedWeight(
        parseWeight(parameters, param, SiPrefix.Base, MIN_EQUIPMENT_WEIGHT),
    )
}
