package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.appearance.editNormalEye
import at.orchaldir.gm.app.html.item.equipment.style.editLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.editOrnament
import at.orchaldir.gm.app.html.item.equipment.style.parseLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseOrnament
import at.orchaldir.gm.app.html.item.equipment.style.showLineStyle
import at.orchaldir.gm.app.html.item.equipment.style.showOrnament
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.NormalEye
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.race.appearance.EyeOptions
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEyePatch(
    call: ApplicationCall,
    state: State,
    eyePatch: EyePatch,
) {
    showStyle(call, state, eyePatch.style)
    showFixation(call, state, eyePatch.fixation)
}

private fun HtmlBlockTag.showStyle(
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

private fun HtmlBlockTag.showFixation(
    call: ApplicationCall,
    state: State,
    fixation: EyePatchFixation,
) {
    showDetails("Fixation") {
        field("Type", fixation.getType())

        when (fixation) {
            NoFixation -> doNothing()
            is OneBand -> showLineStyle(call, state, fixation.band, "Band")
            is DiagonalBand -> showLineStyle(call, state, fixation.band, "Band")
            is TwoBands -> showLineStyle(call, state, fixation.band, "Band")
        }
    }
}

// edit

fun HtmlBlockTag.editEyePatch(
    state: State,
    eyePatch: EyePatch,
) {
    editStyle(state, eyePatch.style)
    editFixation(state, eyePatch.fixation)
}

private fun HtmlBlockTag.editStyle(
    state: State,
    style: EyePatchStyle,
) {
    showDetails("Style", true) {
        selectValue("Type", STYLE, EyePatchStyleType.entries, style.getType())

        when (style) {
            is SimpleEyePatch -> {
                selectValue("Shape", SHAPE, VALID_LENSES, style.shape)
                editItemPart(state, style.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
            }

            is OrnamentAsEyePatch -> editOrnament(state, style.ornament)
            is EyePatchWithEye -> {
                editNormalEye(EyeOptions(), style.eye)
                selectValue("Shape", SHAPE, VALID_LENSES, style.shape)
                editItemPart(state, style.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
            }
        }
    }
}

private fun HtmlBlockTag.editFixation(
    state: State,
    fixation: EyePatchFixation,
) {
    showDetails("Fixation", true) {
        selectValue("Type", FIXATION, EyePatchFixationType.entries, fixation.getType())

        when (fixation) {
            NoFixation -> doNothing()
            is OneBand -> selectBand(state, fixation.band)
            is DiagonalBand -> selectBand(state, fixation.band)
            is TwoBands -> selectBand(state, fixation.band)
        }
    }
}

private fun DETAILS.selectBand(
    state: State,
    band: LineStyle,
) {
    editLineStyle(state, band, "Band", FIXATION, WITHOUT_ORNAMENT_LINE)
}

// parse

fun parseEyePatch(parameters: Parameters) = EyePatch(
    parseStyle(parameters),
    parseFixation(parameters),
)

private fun parseStyle(parameters: Parameters) =
    when (parse(parameters, STYLE, EyePatchStyleType.Simple)) {
        EyePatchStyleType.Simple -> SimpleEyePatch(
            parse(parameters, SHAPE, LensShape.Circle),
            parseItemPart(parameters, MAIN),
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
            parseItemPart(parameters, MAIN),
        )
    }

private fun parseFixation(parameters: Parameters) = when (parse(parameters, FIXATION, EyePatchFixationType.None)) {
    EyePatchFixationType.None -> NoFixation
    EyePatchFixationType.OneBand -> OneBand(
        parseLineStyle(parameters, FIXATION),
    )

    EyePatchFixationType.DiagonalBand -> DiagonalBand(
        parseLineStyle(parameters, FIXATION),
    )

    EyePatchFixationType.TwoBands -> TwoBands(
        parseLineStyle(parameters, FIXATION),
    )
}

