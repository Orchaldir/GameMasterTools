package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.JOB
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.parse.economy.parseJobId
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.selector.util.sortJobs
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDomain(
    call: ApplicationCall,
    state: State,
    domain: Domain,
) {
    showRarityMap("Spells", domain.spells) { spell ->
        link(call, state, spell)
    }
    showList("Jobs", domain.jobs) { job ->
        link(call, state, job)
    }
}

// edit

fun FORM.editDomain(
    call: ApplicationCall,
    state: State,
    domain: Domain,
) {
    selectName(domain.name)
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), domain.spells, false) { it.name }
    selectElements(state, "Jobs", JOB, state.sortJobs(), domain.jobs)
}

// parse

fun parseDomainId(parameters: Parameters, param: String) = DomainId(parseInt(parameters, param))

fun parseDomainId(value: String) = DomainId(value.toInt())

fun parseDomain(parameters: Parameters, id: DomainId) = Domain(
    id,
    parameters.getOrFail(NAME),
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
    parseElements(parameters, JOB, ::parseJobId),
)
