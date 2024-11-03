package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUILDER
import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showBuilder(
    call: ApplicationCall,
    state: State,
    builder: Builder,
) {
    when (builder) {
        is BuildByBusiness -> fieldLink("Builder", call, state, builder.business)
        is BuildByCharacter -> fieldLink("Builder", call, state, builder.character)
        UndefinedBuilder -> doNothing()
    }
}

fun FORM.selectBuilder(
    state: State,
    builder: Builder,
) {
    selectValue("Builder Type", BUILDER, BuilderType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == builder.getType()
    }
    when (builder) {
        is BuildByBusiness -> selectValue(
            "Builder",
            combine(BUILDER, BUSINESS),
            state.getBusinessStorage().getAll(),
            true
        ) { business ->
            label = business.name
            value = business.id.value.toString()
            selected = builder.business == business.id
        }

        is BuildByCharacter -> selectValue(
            "Builder",
            combine(BUILDER, CHARACTER),
            state.getCharacterStorage().getAll(),
            true
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = builder.character == character.id
        }

        UndefinedBuilder -> doNothing()
    }
}

fun parseBuilder(parameters: Parameters): Builder {
    return when (parse(parameters, BUILDER, BuilderType.Undefined)) {
        BuilderType.Undefined -> UndefinedBuilder
        BuilderType.BuildByBusiness -> BuildByBusiness(parseBusinessId(parameters, combine(BUILDER, BUSINESS)))
        BuilderType.BuildByCharacter -> BuildByCharacter(parseCharacterId(parameters, combine(BUILDER, CHARACTER)))
    }
}