package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.editNormalEye
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.item.equipment.EYE_PATCH_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEyePatchStyle(
    call: ApplicationCall,
    state: State,
    style: EyePatchStyle,
) {
    showDetails("Style") {
        field("Type", style.getType())

        when (style) {
            is SimpleEyePatch -> {
                field("Shape", style.shape)
                showItemPart(call, state, style.main)
            }

            is OrnamentAsEyePatch -> showOrnament(call, state, style.ornament)
            is EyePatchWithEye -> {
                field("Shape", style.shape)
                showItemPart(call, state, style.main)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editEyePatchStyle(
    state: State,
    style: EyePatchStyle,
) {
    showDetails("Style", true) {
        selectValue("Type", STYLE, EyePatchStyleType.entries, style.getType())

        when (style) {
            is SimpleEyePatch -> {
                selectValue("Shape", SHAPE, VALID_LENSES, style.shape)
                editItemPart(state, style.main, MAIN, allowedTypes = EYE_PATCH_MATERIALS)
            }

            is OrnamentAsEyePatch -> editOrnament(state, style.ornament)
            is EyePatchWithEye -> {
                editNormalEye(EyeOptions(), style.eye)
                selectValue("Shape", SHAPE, VALID_LENSES, style.shape)
                editItemPart(state, style.main, MAIN, allowedTypes = EYE_PATCH_MATERIALS)
            }
        }
    }
}

// parse

fun parseEyePatchStyle(parameters: Parameters) =
    when (parse(parameters, STYLE, EyePatchStyleType.Simple)) {
        EyePatchStyleType.Simple -> SimpleEyePatch(
            parse(parameters, SHAPE, LensShape.Circle),
            parseItemPart(parameters, MAIN, EYE_PATCH_MATERIALS),
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
            parseItemPart(parameters, MAIN, EYE_PATCH_MATERIALS),
        )
    }
