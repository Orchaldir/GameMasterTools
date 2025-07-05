package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.EYE
import at.orchaldir.gm.app.FRONT
import at.orchaldir.gm.app.NOSE
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHelmetFront(
    call: ApplicationCall,
    state: State,
    front: HelmetFront,
) {
    showDetails("Front") {
        field("Type", front.getType())

        when (front) {
            NoHelmetFront -> doNothing()
            is NoseProtection -> {
                field("Shape", front.shape)
                showColorSchemeItemPart(call, state, front.part)
            }

            is EyeProtection -> {
                field("Shape", front.shape)
                field("Eye Holes", front.hole)
                optionalField("Nose", front.nose)
                showColorSchemeItemPart(call, state, front.part)
            }

            is FaceProtection -> {
                field("Shape", front.shape)
                field("Eye Holes", front.eyeHole)
                showColorSchemeItemPart(call, state, front.part)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editHelmetFront(
    state: State,
    front: HelmetFront,
) {
    showDetails("Front", true) {
        selectValue("Type", FRONT, HelmetFrontType.entries, front.getType())

        when (front) {
            NoHelmetFront -> doNothing()
            is NoseProtection -> {
                selectValue(
                    "Shape",
                    combine(FRONT, NOSE),
                    NoseProtectionShape.entries,
                    front.shape,
                )
                editColorSchemeItemPart(state, front.part, FRONT)
            }

            is EyeProtection -> {
                selectValue(
                    "Shape",
                    combine(FRONT, SHAPE),
                    EyeProtectionShape.entries,
                    front.shape,
                )
                selectEyeHoles(front.hole, FRONT)
                selectOptionalValue(
                    "Nose",
                    combine(FRONT, NOSE),
                    front.nose,
                    NoseProtectionShape.entries,
                )
                editColorSchemeItemPart(state, front.part, FRONT)
            }

            is FaceProtection -> {
                selectValue(
                    "Shape",
                    combine(FRONT, SHAPE),
                    FaceProtectionShape.entries,
                    front.shape,
                )
                selectEyeHoles(front.eyeHole, FRONT)
                editColorSchemeItemPart(state, front.part, FRONT)
            }
        }
    }
}

fun HtmlBlockTag.selectEyeHoles(
    shape: EyeHoleShape,
    param: String,
) {
    selectValue(
        "Eye Holes",
        combine(param, EYE),
        EyeHoleShape.entries,
        shape,
    )
}


// parse

fun parseHelmetFront(
    parameters: Parameters,
) = when (parse(parameters, FRONT, HelmetFrontType.None)) {
    HelmetFrontType.None -> NoHelmetFront
    HelmetFrontType.Nose -> NoseProtection(
        parse(parameters, combine(FRONT, NOSE), NoseProtectionShape.Rectangle),
        parseColorSchemeItemPart(parameters, FRONT),
    )

    HelmetFrontType.Eye -> EyeProtection(
        parse(parameters, combine(FRONT, SHAPE), EyeProtectionShape.Glasses),
        parseEyeHoles(parameters, FRONT),
        parse<NoseProtectionShape>(parameters, combine(FRONT, NOSE)),
        parseColorSchemeItemPart(parameters, FRONT),
    )

    HelmetFrontType.Face -> FaceProtection(
        parse(parameters, combine(FRONT, SHAPE), FaceProtectionShape.Oval),
        parseEyeHoles(parameters, FRONT),
        parseColorSchemeItemPart(parameters, FRONT),
    )
}

fun parseEyeHoles(parameters: Parameters, param: String) =
    parse(parameters, combine(param, EYE), EyeHoleShape.Slit)
