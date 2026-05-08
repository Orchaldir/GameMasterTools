package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.STEM
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
        showBranching((stem.branching))
    }
}

// edit

fun HtmlBlockTag.editStem(
    stem: Stem,
    param: String,
) {
    val stemParam = combine(param, STEM)

    showDetails("Stem", true) {
        field("Segments", stem.segments)
        selectInt(
            "Segments",
            stem.segments,
            MIN_SEGMENTS,
            MAX_SEGMENTS,
            1,
            combine(stemParam, NUMBER),
        )
        editStemShape(stem.shape, stemParam)
        editStemSplitting(stem.splitting, stemParam)
        editStemThickness(stem.thickness, stemParam)
        editBranching(stem.branching, stemParam)
    }
}


// parse

fun parseStem(
    parameters: Parameters,
    param: String,
): Stem {
    val stemParam = combine(param, STEM)

    return Stem(
        parseInt(parameters, combine(stemParam, NUMBER), MIN_SEGMENTS),
        parseStemShape(parameters, stemParam),
        parseStemThickness(parameters, stemParam),
        parseStemSplitting(parameters, stemParam),
        parseBranching(parameters, stemParam)
    )
}
