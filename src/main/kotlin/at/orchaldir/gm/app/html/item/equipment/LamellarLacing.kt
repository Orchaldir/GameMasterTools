package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.LACING
import at.orchaldir.gm.app.STRIPE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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
            is DiagonalLacing -> showColorSchemeItemPart(call, state, lacing.lacing, "Lacing")
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

fun FORM.editLamellarLacing(state: State, lacing: LamellarLacing) {
    showDetails("Lacing", true) {
        selectValue("Type", LACING, LamellarLacingType.entries, lacing.getType())

        when (lacing) {
            NoLacing -> doNothing()
            is DiagonalLacing -> editColorSchemeItemPart(state, lacing.lacing, LACING, "Lacing")
            is FourSidesLacing -> editColorSchemeItemPart(state, lacing.lacing, LACING, "Lacing")
            is LacingAndStripe -> {
                editColorSchemeItemPart(state, lacing.lacing, LACING, "Lacing")
                editColorSchemeItemPart(state, lacing.stripe, STRIPE, "Stripe")
                selectFactor(
                    "Stripe Width",
                    combine(STRIPE, WIDTH),
                    lacing.stripeWidth,
                    MIN_STRIPE_WIDTH,
                    MAX_STRIPE_WIDTH,
                )
            }
        }
    }
}

// parse

fun parseLamellarLacing(parameters: Parameters): LamellarLacing {
    val type = parse(parameters, LACING, LamellarLacingType.FourSides)

    return when (type) {
        LamellarLacingType.None -> NoLacing
        LamellarLacingType.Diagonal -> DiagonalLacing(
            parseColorSchemeItemPart(parameters, LACING),
        )

        LamellarLacingType.FourSides -> FourSidesLacing(
            parseColorSchemeItemPart(parameters, LACING),
        )

        LamellarLacingType.Stripe -> LacingAndStripe(
            parseColorSchemeItemPart(parameters, LACING),
            parseColorSchemeItemPart(parameters, STRIPE),
            parseFactor(parameters, combine(STRIPE, WIDTH))
        )
    }
}
