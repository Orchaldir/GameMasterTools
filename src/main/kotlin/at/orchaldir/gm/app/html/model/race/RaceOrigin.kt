package at.orchaldir.gm.app.html.model.race

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
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
}

fun HtmlBlockTag.displayRaceOrigin(
    call: ApplicationCall,
    state: State,
    origin: RaceOrigin,
) {
    when (origin) {
        is HybridRace -> {
            +"Hybrid of "
            link(call, state, origin.first)
            +" & "
            link(call, state, origin.second)
        }

        CosmicRace -> +"Cosmic"

        is CreatedRace -> {
            +"Created by "
            showCreator(call, state, origin.inventor)
        }

        is EvolvedRace -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        OriginalRace -> +"Original"
    }
}

// edit

fun FORM.editRaceOrigin(
    state: State,
    race: Race,
) {
    val possibleInventors = state.getCharacterStorage().getAll()
    val possibleParents = state.sortRaces(state.getPossibleParents(race.id))

    selectValue("Origin", ORIGIN, RaceOriginType.entries, race.origin.getType(), true) {
        when (it) {
            Hybrid -> possibleParents.size < 2
            Created -> possibleInventors.isEmpty()
            Evolved -> possibleParents.isEmpty()
            else -> false
        }
    }
    when (val origin = race.origin) {
        is CreatedRace -> {
            selectCreator(state, origin.inventor, race.id, origin.date, "Creator")
            selectDate(state, "Date", origin.date, DATE)
        }

        is EvolvedRace -> selectElement(state, "Parent", combine(ORIGIN, RACE), possibleParents, origin.parent)

        is HybridRace -> {
            val withoutFirst = possibleParents.filter { it.id != origin.first }
            val withoutSecond = possibleParents.filter { it.id != origin.second }

            selectElement(state, "First", combine(ORIGIN, 0), withoutSecond, origin.first, true)
            selectElement(state, "Second", combine(ORIGIN, 1), withoutFirst, origin.second, true)
        }

        else -> doNothing()
    }
}

// parse

fun parseRaceOrigin(parameters: Parameters, state: State) = when (parse(parameters, ORIGIN, RaceOriginType.Original)) {
    Hybrid -> HybridRace(
        parseRaceId(parameters, combine(ORIGIN, 0)),
        parseRaceId(parameters, combine(ORIGIN, 1)),
    )

    Cosmic -> CosmicRace

    Created -> CreatedRace(
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    Evolved -> EvolvedRace(parseRaceId(parameters, combine(ORIGIN, RACE)))

    Original -> OriginalRace
}