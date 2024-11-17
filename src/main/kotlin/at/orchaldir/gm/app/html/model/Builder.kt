package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUILDER
import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.economy.parseBusinessId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseCharacterId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.economy.getOpenBusinesses
import at.orchaldir.gm.core.selector.getLiving
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldBuilder(
    call: ApplicationCall,
    state: State,
    builder: Creator,
) {
    field("Builder") {
        showBuilder(call, state, builder)
    }
}

fun HtmlBlockTag.showBuilder(
    call: ApplicationCall,
    state: State,
    builder: Creator,
) {
    when (builder) {
        is CreatedByBusiness -> link(call, state, builder.business)
        is CreatedByCharacter -> link(call, state, builder.character)
        UndefinedCreator -> +"Undefined"
    }
}

fun FORM.selectBuilder(
    state: State,
    builder: Creator,
    date: Date,
) {
    selectValue("Builder Type", BUILDER, CreatorType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == builder.getType()
    }
    when (builder) {
        is CreatedByBusiness -> selectValue(
            "Builder",
            combine(BUILDER, BUSINESS),
            state.getOpenBusinesses(date),
            true
        ) { business ->
            label = business.name
            value = business.id.value.toString()
            selected = builder.business == business.id
        }

        is CreatedByCharacter -> selectValue(
            "Builder",
            combine(BUILDER, CHARACTER),
            state.getLiving(date),
            true
        ) { character ->
            label = character.name(state)
            value = character.id.value.toString()
            selected = builder.character == character.id
        }

        UndefinedCreator -> doNothing()
    }
}

fun parseBuilder(parameters: Parameters): Creator {
    return when (parse(parameters, BUILDER, CreatorType.Undefined)) {
        CreatorType.Undefined -> UndefinedCreator
        CreatorType.CreatedByBusiness -> CreatedByBusiness(parseBusinessId(parameters, combine(BUILDER, BUSINESS)))
        CreatorType.CreatedByCharacter -> CreatedByCharacter(parseCharacterId(parameters, combine(BUILDER, CHARACTER)))
    }
}