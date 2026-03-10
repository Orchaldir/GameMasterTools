package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.EYE
import at.orchaldir.gm.app.FRONT
import at.orchaldir.gm.app.NOSE
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.HELMET_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.SOLID_MATERIALS
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
                showItemPart(call, state, front.main)
            }

            is EyeProtection -> {
                field("Shape", front.shape)
                field("Eye Holes", front.hole)
                optionalField("Nose", front.nose)
                showItemPart(call, state, front.main)
            }

            is FaceProtection -> {
                field("Shape", front.shape)
                field("Eye Holes", front.eyeHole)
                showItemPart(call, state, front.main)
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
                editItemPart(state, front.main, FRONT, allowedTypes = HELMET_MATERIALS)
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
                editItemPart(state, front.main, FRONT, allowedTypes = HELMET_MATERIALS)
            }

            is FaceProtection -> {
                selectValue(
                    "Shape",
                    combine(FRONT, SHAPE),
                    FaceProtectionShape.entries,
                    front.shape,
                )
                selectEyeHoles(front.eyeHole, FRONT)
                editItemPart(state, front.main, FRONT, allowedTypes = HELMET_MATERIALS)
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
    state: State,
    parameters: Parameters,
) = when (parse(parameters, FRONT, HelmetFrontType.None)) {
    HelmetFrontType.None -> NoHelmetFront
    HelmetFrontType.Nose -> NoseProtection(
        parse(parameters, combine(FRONT, NOSE), NoseProtectionShape.Rectangle),
        parseItemPart(state, parameters, FRONT, HELMET_MATERIALS),
    )

    HelmetFrontType.Eye -> EyeProtection(
        parse(parameters, combine(FRONT, SHAPE), EyeProtectionShape.Glasses),
        parseEyeHoles(parameters, FRONT),
        parse<NoseProtectionShape>(parameters, combine(FRONT, NOSE)),
        parseItemPart(state, parameters, FRONT, HELMET_MATERIALS),
    )

    HelmetFrontType.Face -> FaceProtection(
        parse(parameters, combine(FRONT, SHAPE), FaceProtectionShape.Oval),
        parseEyeHoles(parameters, FRONT),
        parseItemPart(state, parameters, FRONT, HELMET_MATERIALS),
    )
}

fun parseEyeHoles(parameters: Parameters, param: String) =
    parse(parameters, combine(param, EYE), EyeHoleShape.Slit)
