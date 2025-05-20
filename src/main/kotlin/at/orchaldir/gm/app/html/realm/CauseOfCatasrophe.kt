package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.parseCreator
import at.orchaldir.gm.app.html.util.selectCreator
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.realm.CauseOfCatastropheType.Undefined
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCauseOfCatastrophe(
    call: ApplicationCall,
    state: State,
    cause: CauseOfCatastrophe,
) {
    field("Cause") {
        displayCauseOfCatastrophe(call, state, cause)
    }
}

fun HtmlBlockTag.displayCauseOfCatastrophe(
    call: ApplicationCall,
    state: State,
    cause: CauseOfCatastrophe,
    showUndefined: Boolean = true,
) {
    when (cause) {
        is AccidentalCatastrophe -> {
            +"Accident caused by "
            showCreator(call, state, cause.creator)
        }

        is CreatedCatastrophe -> {
            +"Caused by "
            showCreator(call, state, cause.creator)
        }

        NaturalDisaster -> +"Natural Disasters"
        UndefinedCauseOfCatastrophe -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun FORM.editCauseOfCatastrophe(
    state: State,
    catastrophe: Catastrophe,
) {
    val cause = catastrophe.cause

    showDetails("Cause", true) {
        selectValue("Type", ORIGIN, CauseOfCatastropheType.entries, cause.getType())

        when (cause) {
            is AccidentalCatastrophe -> selectCreator(
                state,
                cause.creator,
                catastrophe.id,
                catastrophe.startDate,
                "Creator"
            )

            is CreatedCatastrophe -> selectCreator(
                state,
                cause.creator,
                catastrophe.id,
                catastrophe.startDate,
                "Creator"
            )

            NaturalDisaster -> doNothing()
            UndefinedCauseOfCatastrophe -> doNothing()
        }
    }
}

// parse

fun parseCauseOfCatastrophe(parameters: Parameters) = when (parse(parameters, ORIGIN, Undefined)) {
    CauseOfCatastropheType.Accidental -> AccidentalCatastrophe(
        parseCreator(parameters),
    )

    CauseOfCatastropheType.Created -> CreatedCatastrophe(
        parseCreator(parameters),
    )

    CauseOfCatastropheType.Natural -> NaturalDisaster
    Undefined -> UndefinedCauseOfCatastrophe
}