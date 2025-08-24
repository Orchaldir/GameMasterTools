package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.AUTHENTICITY
import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldAuthenticity(
    call: ApplicationCall,
    state: State,
    authenticity: Authenticity,
) = field("Authenticity") {
    showAuthenticity(call, state, authenticity)
}

fun HtmlBlockTag.showAuthenticity(
    call: ApplicationCall,
    state: State,
    authenticity: Authenticity,
    showUndefined: Boolean = true,
) {
    when (authenticity) {
        UndefinedAuthenticity -> if (showUndefined) {
            +"Undefined"
        }

        Authentic -> +"Authentic"
        Invented -> +"Invented"
        is MaskOfOtherGod -> {
            +"Mask of "
            link(call, state, authenticity.god)
        }
        is SecretIdentity -> {
            +"Secret Identity of "
            link(call, state, authenticity.character)
        }
    }
}

// edit

fun HtmlBlockTag.editAuthenticity(
    state: State,
    authenticity: Authenticity,
) {
    selectValue("Authenticity", AUTHENTICITY, AuthenticityType.entries, authenticity.getType())

    when (authenticity) {
        Authentic, Invented, UndefinedAuthenticity -> doNothing()
        is MaskOfOtherGod -> selectElement(
            state,
            combine(AUTHENTICITY, GOD),
            state.sortGods(),
            authenticity.god,
        )

        is SecretIdentity -> selectElement(
            state,
            combine(AUTHENTICITY, CHARACTER),
            state.sortCharacters(),
            authenticity.character,
        )
    }
}

// parse

fun parseAuthenticity(parameters: Parameters) = when (parse(parameters, AUTHENTICITY, AuthenticityType.Undefined)) {
    AuthenticityType.Undefined -> UndefinedAuthenticity
    AuthenticityType.Authentic -> Authentic
    AuthenticityType.Invented -> Invented
    AuthenticityType.Mask -> MaskOfOtherGod(
        parseGodId(parameters, combine(AUTHENTICITY, GOD)),
    )
    AuthenticityType.Secret -> SecretIdentity(
        parseCharacterId(parameters, combine(AUTHENTICITY, CHARACTER)),
    )
}