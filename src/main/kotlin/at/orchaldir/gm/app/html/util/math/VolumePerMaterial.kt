package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.math.unit.VolumePerMaterial
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.showVolumePerMaterial(
    call: ApplicationCall,
    state: State,
    vpm: VolumePerMaterial,
) {
    if (vpm.isEmpty()) {
        return
    }

    table {
        tr {
            th { +"Material" }
            th { +"Volume" }
            th { +"Density" }
            th { +"Weight" }
        }
        vpm.getMap().forEach { (id, volume) ->
            val material = state.getMaterialStorage().getOrThrow(id)
            val weight = Weight.fromVolume(volume, material.properties.density)

            tr {
                tdLink(call, state, material)
                tdString(volume.toString())
                tdString(material.properties.density.toString())
                tdString(weight.toString())
            }
        }
    }
}
