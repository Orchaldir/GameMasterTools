package at.orchaldir.gm.app.html.model.race

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.RACE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.util.optionalField
import at.orchaldir.gm.app.html.model.util.parseCreator
import at.orchaldir.gm.app.html.model.util.parseDate
import at.orchaldir.gm.app.html.model.util.selectCreator
import at.orchaldir.gm.app.html.model.util.selectDate
import at.orchaldir.gm.app.html.model.util.showCreator
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.*
import at.orchaldir.gm.core.model.race.RaceOriginType.*
import at.orchaldir.gm.core.selector.getPossibleParents
import at.orchaldir.gm.core.selector.util.sortRaces
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRaceOrigin(
    call: ApplicationCall,
    state: State,
    origin: RaceOrigin,
) {
    field("Origin") {
        displayRaceOrigin(call, state, origin)
    }
    optionalField(call, state, "Date", origin.startDate())
}

fun HtmlBlockTag.displayRaceOrigin(
    call: ApplicationCall,
    state: State,
    origin: RaceOrigin,
    displayOriginal: Boolean = true,
) {
    when (origin) {
        is CreatedRace -> {
            +"Created by "
            showCreator(call, state, origin.creator)
        }

        is EvolvedRace -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        is HybridRace -> {
            +"Hybrid of "
            link(call, state, origin.first)
            +" & "
            link(call, state, origin.second)
        }

        is ModifiedRace -> {
            link(call, state, origin.parent)
            +" modified by "
            showCreator(call, state, origin.modifier)
        }

        OriginalRace -> if (displayOriginal) {
            +"Original"
        }
    }
}

// edit

fun FORM.editRaceOrigin(
    state: State,
    race: Race,
) {
    val possibleParents = state.sortRaces(state.getPossibleParents(race.id))

    showDetails("Origin", true) {
        selectValue("Type", ORIGIN, RaceOriginType.entries, race.origin.getType()) {
            when (it) {
                Evolved, Modified -> possibleParents.isEmpty()
                Hybrid -> possibleParents.size < 2
                else -> false
            }
        }
        when (val origin = race.origin) {
            is CreatedRace -> {
                selectCreator(state, origin.creator, race.id, origin.date, "Creator")
                selectDate(state, "Date", origin.date, DATE)
            }

            is EvolvedRace -> selectElement(state, "Parent", combine(ORIGIN, RACE), possibleParents, origin.parent)

            is HybridRace -> {
                val withoutFirst = possibleParents.filter { it.id != origin.first }
                val withoutSecond = possibleParents.filter { it.id != origin.second }

                selectElement(state, "First", combine(ORIGIN, 0), withoutSecond, origin.first)
                selectElement(state, "Second", combine(ORIGIN, 1), withoutFirst, origin.second)
            }

            is ModifiedRace -> {
                selectElement(state, "Parent", combine(ORIGIN, RACE), possibleParents, origin.parent)
                selectCreator(state, origin.modifier, race.id, origin.date, "Creator")
                selectDate(state, "Date", origin.date, DATE)
            }

            else -> doNothing()
        }
    }
}

// parse

fun parseRaceOrigin(parameters: Parameters, state: State) = when (parse(parameters, ORIGIN, Original)) {
    Created -> CreatedRace(
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    Evolved -> EvolvedRace(parseRaceId(parameters, combine(ORIGIN, RACE)))

    Hybrid -> HybridRace(
        parseRaceId(parameters, combine(ORIGIN, 0)),
        parseRaceId(parameters, combine(ORIGIN, 1)),
    )

    Modified -> ModifiedRace(
        parseRaceId(parameters, combine(ORIGIN, RACE)),
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    Original -> OriginalRace
}