package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.character.editNormalEye
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showEyePatch(
    call: ApplicationCall,
    state: State,
    eyePatch: EyePatch,
) {
    showStyle(call, state, eyePatch.style)
}

private fun BODY.showStyle(
    call: ApplicationCall,
    state: State,
    style: EyePatchStyle,
) {
    field("Style", style.getType())

    when (style) {
        is SimpleEyePatch -> {
            field("Shape", style.shape)
            showLook(call, state, style.color, style.material)
        }

        is OrnamentAsEyePatch -> showOrnament(call, state, style.ornament)
        is EyePatchWithEye -> {
            field("Shape", style.shape)
            showLook(call, state, style.color, style.material)
        }
    }
}

// edit

fun FORM.editEyePatch(
    state: State,
    eyePatch: EyePatch,
) {
    editStyle(state, eyePatch.style)
}

private fun FORM.editStyle(
    state: State,
    style: EyePatchStyle,
) {
    selectValue("Style", STYLE, EyePatchStyleType.entries, style.getType(), true)

    when (style) {
        is SimpleEyePatch -> {
            selectValue("Shape", SHAPE, VALID_LENSES, style.shape, true)
            editLook(state, style.color, style.material, APPEARANCE)
        }

        is OrnamentAsEyePatch -> editOrnament(state, style.ornament)
        is EyePatchWithEye -> {
            editNormalEye(EyeOptions(), style.eye)
            selectValue("Shape", SHAPE, VALID_LENSES, style.shape, true)
            editLook(state, style.color, style.material, APPEARANCE)
        }
    }
}


// parse

fun parseEyePatch(parameters: Parameters) = EyePatch(
    parseStyle(parameters),
)

private fun parseStyle(parameters: Parameters) =
    when (parse(parameters, STYLE, EyePatchStyleType.Simple)) {
        EyePatchStyleType.Simple -> SimpleEyePatch(
            parse(parameters, SHAPE, LensShape.Circle),
            parse(parameters, combine(APPEARANCE, COLOR), Color.Black),
            parseMaterialId(parameters, combine(APPEARANCE, MATERIAL)),
        )

        EyePatchStyleType.Ornament -> OrnamentAsEyePatch(
            parseOrnament(parameters),
        )

        EyePatchStyleType.Eye -> EyePatchWithEye(
            NormalEye(
                parse(parameters, combine(EYE, SHAPE), EyeShape.Circle),
                parse(parameters, combine(PUPIL, SHAPE), PupilShape.Circle),
                parse(parameters, combine(PUPIL, COLOR), Color.Green),
                parse(parameters, combine(PUPIL, SCLERA), Color.White),
            ),
            parse(parameters, SHAPE, LensShape.Circle),
            parse(parameters, combine(APPEARANCE, COLOR), Color.Black),
            parseMaterialId(parameters, combine(APPEARANCE, MATERIAL)),
        )
    }

