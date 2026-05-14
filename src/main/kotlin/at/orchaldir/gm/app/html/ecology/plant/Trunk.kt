package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.APPEARANCE
import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.TRUNK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.parseDistance
import at.orchaldir.gm.app.html.util.math.parseDistribution
import at.orchaldir.gm.app.html.util.math.selectDistanceDistribution
import at.orchaldir.gm.app.html.util.math.showDistribution
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_TRUNK_HEIGHT
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.util.render.Color
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTrunk(
    trunk: Trunk,
) {
    showDetails("Trunk", true) {
        fieldColor(trunk.bark, "Bark")
        showDistribution("Height", trunk.height)
        showStem(trunk.stem)
    }
}

// edit

fun HtmlBlockTag.editTrunk(
    trunk: Trunk,
    param: String,
) {
    val trunkParam = combine(param, TRUNK)

    showDetails("Trunk", true) {
        selectColor(
            trunk.bark,
            combine(trunkParam, COLOR),
            "Bark",
        )
        selectDistanceDistribution(
            "Height",
            combine(trunkParam, HEIGHT),
            trunk.height,
            MIN_TRUNK_HEIGHT,
            MAX_TRUNK_HEIGHT,
        )
        editStem(trunk.stem, trunkParam)
    }
}


// parse

fun parseTrunk(
    parameters: Parameters,
    param: String = APPEARANCE,
): Trunk {
    val trunkParam = combine(param, TRUNK)

    return Trunk(
        parseDistribution(
            parameters,
            combine(trunkParam, HEIGHT),
        ) { _, p, prefix ->
            parseDistance(parameters, p, prefix, MIN_TRUNK_HEIGHT)
        },
        parseStem(parameters, trunkParam),
        parse(parameters, trunkParam, Color.SaddleBrown),
    )
}
