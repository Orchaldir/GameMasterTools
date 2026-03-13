package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.LAMELLAR_STRIPE_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.LINE_MATERIALS
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
                showItemPart(call, state, lacing.lacing)
                fieldFactor("Thickness", lacing.thickness)
            }

            is FourSidesLacing -> showItemPart(call, state, lacing.lacing)
            is LacingAndStripe -> {
                showItemPart(call, state, lacing.lacing, "Lacing")
                showItemPart(call, state, lacing.stripe, "Stripe")
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
                selectLacing(state, param, lacing.lacing)
                selectLacingThickness(lacing.thickness, param)
            }

            is FourSidesLacing -> {
                selectLacing(state, param, lacing.lacing)
                selectLacingLength(lacing.lacingLength, param)
                selectLacingThickness(lacing.lacingThickness, param)
            }

            is LacingAndStripe -> {
                selectLacing(state, param, lacing.lacing)
                selectLacingLength(lacing.lacingLength, param)
                selectLacingThickness(lacing.lacingThickness, param)
                editItemPart(
                    state,
                    lacing.stripe,
                    combine(param, STRIPE),
                    "Stripe",
                    LAMELLAR_STRIPE_MATERIALS,
                )
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

private fun DETAILS.selectLacing(
    state: State,
    param: String,
    lacing: ItemPart,
) = editItemPart(
    state,
    lacing,
    combine(param, LACING),
    allowedTypes = LINE_MATERIALS,
)

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

fun parseLamellarLacing(
    state: State,
    parameters: Parameters,
    param: String,
): LamellarLacing {
    val type = parse(parameters, combine(param, TYPE), LamellarLacingType.FourSides)

    return when (type) {
        LamellarLacingType.None -> NoLacing
        LamellarLacingType.Diagonal -> DiagonalLacing(
            parseLacing(state, parameters, param),
            parseLacingThickness(parameters, param),
        )

        LamellarLacingType.FourSides -> FourSidesLacing(
            parseLacing(state, parameters, param),
            parseLacingLength(parameters, param),
            parseLacingThickness(parameters, param),
        )

        LamellarLacingType.Stripe -> LacingAndStripe(
            parseLacing(state, parameters, param),
            parseLacingLength(parameters, param),
            parseLacingThickness(parameters, param),
            parseItemPart(state, parameters, combine(param, STRIPE), LAMELLAR_STRIPE_MATERIALS),
            parseStripeWidth(parameters, param),
        )
    }
}

private fun parseLacing(
    state: State,
    parameters: Parameters,
    param: String,
) = parseItemPart(state, parameters, combine(param, LACING), LINE_MATERIALS)

private fun parseLacingLength(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LACING, LENGTH), DEFAULT_LENGTH)

private fun parseLacingThickness(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, LACING, WIDTH), DEFAULT_THICKNESS)

private fun parseStripeWidth(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, STRIPE, WIDTH), DEFAULT_STRIPE_WIDTH)
