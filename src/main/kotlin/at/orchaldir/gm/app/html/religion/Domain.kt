package at.orchaldir.gm.app.html.religion

import at.orchaldir.gm.app.JOB
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.parseJobId
import at.orchaldir.gm.app.html.magic.parseSpellId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.selector.religion.getGodsWith
import io.ktor.http.*
import io.ktor.server.application.*
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
    fieldIds(call, state, domain.jobs)
    fieldElements(call, state, state.getGodsWith(domain.id))
}

// edit

fun HtmlBlockTag.editDomain(
    call: ApplicationCall,
    state: State,
    domain: Domain,
) {
    selectName(domain.name)
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), domain.spells)
    selectElements(state, "Jobs", JOB, state.getJobStorage().getAll(), domain.jobs)
}

// parse

fun parseDomainId(parameters: Parameters, param: String) = DomainId(parseInt(parameters, param))

fun parseDomainId(value: String) = DomainId(value.toInt())

fun parseDomain(
    state: State,
    parameters: Parameters,
    id: DomainId,
) = Domain(
    id,
    parseName(parameters),
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
    parseElements(parameters, JOB, ::parseJobId),
)
