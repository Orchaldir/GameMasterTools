package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.ecology.plant.appearance.MAX_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.MIN_SEGMENTS
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStem(
    stem: Stem,
) {
    showDetails("Stem", true) {
        field("Segments", stem.segments)
        showStemShape(stem.shape)
        showStemSplitting(stem.splitting)
        showStemThickness(stem.thickness)
    }
}

// edit

fun HtmlBlockTag.editStem(
    stem: Stem,
    param: String,
) {
    showDetails("Stem", true) {
        field("Segments", stem.segments)
        selectInt(
            "Segments",
            stem.segments,
            MIN_SEGMENTS,
            MAX_SEGMENTS,
            1,
            combine(param, NUMBER),
        )
        editStemShape(stem.shape, param)
        editStemSplitting(stem.splitting, param)
        editStemThickness(stem.thickness, param)
    }
}


// parse

fun parseStem(
    parameters: Parameters,
    param: String,
) = Stem(
    parseInt(parameters, combine(param, NUMBER), MIN_SEGMENTS),
    parseStemShape(parameters, param),
    parseStemThickness(parameters, param),
    parseStemSplitting(parameters, param),
)
