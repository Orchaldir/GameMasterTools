package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTreeSilhouette(
    silhouette: TreeSilhouette,
) {
    showDetails("Tree Silhouette", true) {
        field("Type", silhouette.getType())

        when (silhouette) {
            NoTreeSilhouette -> doNothing()
            is SimpleTreeSilhouette -> {
                field("Shape", silhouette.shape)
                fieldColor(silhouette.color)
                fieldFactor("Base", silhouette.base)
                fieldFactor("Width", silhouette.width)
                field("Show Stems", silhouette.showStems)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editTreeSilhouette(
    silhouette: TreeSilhouette,
    param: String,
) {
    val silhouetteParam = combine(param, SILHOUETTE)

    showDetails("Tree Silhouette", true) {
        selectValue(
            "Type",
            silhouetteParam,
            TreeSilhouetteType.entries,
            silhouette.getType(),
        )

        when (silhouette) {
            NoTreeSilhouette -> doNothing()
            is SimpleTreeSilhouette -> {
                selectValue(
                    "Shape",
                    combine(silhouetteParam, SHAPE),
                    TreeSilhouetteShape.entries,
                    silhouette.shape,
                )
                selectColor(silhouette.color, combine(silhouetteParam, COLOR))
                selectFactor(
                    "Base",
                    combine(silhouetteParam, BASE),
                    silhouette.base,
                    MIN_BRANCHING_BASE,
                    MAX_BRANCHING_BASE,
                )
                selectFactor(
                    "Width",
                    combine(silhouetteParam, WIDTH),
                    silhouette.width,
                    MIN_SILHOUETTE_WIDTH,
                    MAX_SILHOUETTE_WIDTH,
                )
                selectBool(
                    "Show Stems",
                    silhouette.showStems,
                    combine(silhouetteParam, STEM),
                )
            }
        }
    }
}

// parse

fun parseTreeSilhouette(
    parameters: Parameters,
    param: String,
): TreeSilhouette {
    val silhouetteParam = combine(param, SILHOUETTE)

    return when (parse(parameters, silhouetteParam, TreeSilhouetteType.None)) {
        TreeSilhouetteType.None -> NoTreeSilhouette

        TreeSilhouetteType.Simple -> SimpleTreeSilhouette(
            parse(parameters, combine(silhouetteParam, SHAPE), TreeSilhouetteShape.Conical),
            parse(parameters, combine(silhouetteParam, COLOR), Color.Green),
            parseFactor(parameters, combine(silhouetteParam, BASE), DEFAULT_BRANCHING_BASE),
            parseFactor(parameters, combine(silhouetteParam, WIDTH), DEFAULT_SILHOUETTE_WIDTH),
            parseBool(parameters, combine(silhouetteParam, STEM)),
        )
    }
}
