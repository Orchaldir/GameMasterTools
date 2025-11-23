package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLamellarLacing(
    call: ApplicationCall,
    state: State,
    lacing: LamellarLacing,
) {
    showDetails("Lacing") {
        field("Type", lacing.getType())

        when (lacing) {
            NoLacing -> doNothing()
            is DiagonalLacing -> {
                showColorSchemeItemPart(call, state, lacing.lacing, "Lacing")
                fieldFactor("Thickness", lacing.thickness)
            }

            is FourSidesLacing -> showColorSchemeItemPart(call, state, lacing.lacing, "Lacing")
            is LacingAndStripe -> {
                showColorSchemeItemPart(call, state, lacing.lacing, "Lacing")
                showColorSchemeItemPart(call, state, lacing.stripe, "Stripe")
                fieldFactor("Stripe Width", lacing.stripeWidth)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editLamellarLacing(state: State, param: String, lacing: LamellarLacing) {
    showDetails("Lacing", true) {
        selectValue("Type", combine(param, TYPE), LamellarLacingType.entries, lacing.getType())

        when (lacing) {
            NoLacing -> doNothing()
            is DiagonalLacing -> {
                editColorSchemeItemPart(state, lacing.lacing, combine(param, LACING), "Lacing")
                selectLacingThickness(lacing.thickness, param)
            }

            is FourSidesLacing -> {
                editColorSchemeItemPart(state, lacing.lacing, combine(param, LACING), "Lacing")
                selectLacingLength(lacing.lacingLength, param)
                selectLacingThickness(lacing.lacingThickness, param)
            }

            is LacingAndStripe -> {
                editColorSchemeItemPart(state, lacing.lacing, combine(param, LACING), "Lacing")
                selectLacingLength(lacing.lacingLength, param)
                selectLacingThickness(lacing.lacingThickness, param)
                editColorSchemeItemPart(state, lacing.stripe, combine(param, STRIPE), "Stripe")
                selectFactor(
                    "Stripe Width",
                    combine(param, STRIPE, WIDTH),
                    lacing.stripeWidth,
                    MIN_STRIPE_WIDTH,
                    MAX_STRIPE_WIDTH,
                )
            }
        }
    }
}

private fun DETAILS.selectLacingThickness(thickness: Factor, param: String) {
    selectFactor(
        "Lacing Thickness",
        combine(param, LACING, WIDTH),
        thickness,
        MIN_THICKNESS,
        MAX_THICKNESS,
    )
}

private fun DETAILS.selectLacingLength(length: Factor, param: String) {
    selectFactor(
        "Lacing Length",
        combine(param, LACING, LENGTH),
        length,
        MIN_LENGTH,
        MAX_LENGTH,
    )
}

// parse

fun parseLamellarLacing(parameters: Parameters, param: String): LamellarLacing {
    val type = parse(parameters, combine(param, TYPE), LamellarLacingType.FourSides)

    return when (type) {
        LamellarLacingType.None -> NoLacing
        LamellarLacingType.Diagonal -> DiagonalLacing(
            parseColorSchemeItemPart(parameters, combine(param, LACING)),
            parseLacingThickness(parameters, param),
        )

        LamellarLacingType.FourSides -> FourSidesLacing(
            parseColorSchemeItemPart(parameters, combine(param, LACING)),
            parseLacingLength(parameters, param),
            parseLacingThickness(parameters, param),
        )

        LamellarLacingType.Stripe -> LacingAndStripe(
            parseColorSchemeItemPart(parameters, combine(param, LACING)),
            parseLacingLength(parameters, param),
            parseLacingThickness(parameters, param),
            parseColorSchemeItemPart(parameters, combine(param, STRIPE)),
            parseStripeWidth(parameters, param),
        )
    }
}

private fun parseLacingLength(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LACING, LENGTH), DEFAULT_LENGTH)

private fun parseLacingThickness(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LACING, WIDTH), DEFAULT_THICKNESS)

private fun parseStripeWidth(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, STRIPE, WIDTH), DEFAULT_STRIPE_WIDTH)
