package at.orchaldir.gm.app.html.model.economy

import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.html.selectRarityMap
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.html.showRarityMap
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseSomeOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
import at.orchaldir.gm.core.selector.religion.getGodsAssociatedWith
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortDomains
import at.orchaldir.gm.core.selector.util.sortGods
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showJob(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    val characters = state.getEmployees(job.id).toSet()
    val previousCharacters = state.getPreviousEmployees(job.id).toSet() - characters
    val domains = state.getDomainsAssociatedWith(job.id)
    val gods = state.getGodsAssociatedWith(job.id)

    showRarityMap("Spells", job.spells) { spell ->
        link(call, state, spell)
    }
    showList("Businesses", state.getBusinesses(job.id)) { business ->
        link(call, state, business)
    }
    showList("Current Characters", state.sortCharacters(characters)) { (character, name) ->
        link(call, character.id, name)
    }
    showList("Previous Characters", state.sortCharacters(previousCharacters)) { (character, name) ->
        link(call, character.id, name)
    }
    showList("Associated Domains", state.sortDomains(domains)) { domain ->
        link(call, domain)
    }
    showList("Associated Gods", state.sortGods(gods)) { god ->
        link(call, god)
    }
}

// edit

fun FORM.editJob(
    state: State,
    job: Job,
) {
    selectName(job.name)
    selectRarityMap("Spells", SPELLS, state.getSpellStorage(), job.spells, false) { it.name.text }
}

// parse

fun parseJobId(parameters: Parameters, param: String) = JobId(parseInt(parameters, param))

fun parseJobId(value: String) = JobId(value.toInt())

fun parseJob(id: JobId, parameters: Parameters) = Job(
    id,
    parseName(parameters),
    parseSomeOf(parameters, SPELLS, ::parseSpellId),
)
