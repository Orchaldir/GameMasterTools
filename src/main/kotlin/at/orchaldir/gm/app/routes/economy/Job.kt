package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.economy.parseJob
import at.orchaldir.gm.core.action.CreateJob
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.JOB_TYPE
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.sortCharacters
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$JOB_TYPE")
class JobRoutes {
    @Resource("details")
    class Details(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("new")
    class New(val parent: JobRoutes = JobRoutes())

    @Resource("delete")
    class Delete(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("edit")
    class Edit(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("update")
    class Update(val id: JobId, val parent: JobRoutes = JobRoutes())
}

fun Application.configureJobRouting() {
    routing {
        get<JobRoutes> {
            logger.info { "Get all jobs" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllJobs(call, STORE.getState())
            }
        }
        get<JobRoutes.Details> { details ->
            logger.info { "Get details of job ${details.id.value}" }

            val state = STORE.getState()
            val job = state.getJobStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showJobDetails(call, state, job)
            }
        }
        get<JobRoutes.New> {
            logger.info { "Add new job" }

            STORE.dispatch(CreateJob)

            call.respondRedirect(
                call.application.href(
                    JobRoutes.Edit(
                        STORE.getState().getJobStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<JobRoutes.Delete> { delete ->
            logger.info { "Delete job ${delete.id.value}" }

            STORE.dispatch(DeleteJob(delete.id))

            call.respondRedirect(call.application.href(JobRoutes()))

            STORE.getState().save()
        }
        get<JobRoutes.Edit> { edit ->
            logger.info { "Get editor for job ${edit.id.value}" }

            val state = STORE.getState()
            val job = state.getJobStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showJobEditor(call, state, job)
            }
        }
        post<JobRoutes.Update> { update ->
            logger.info { "Update job ${update.id.value}" }

            val job = parseJob(update.id, call.receiveParameters())

            STORE.dispatch(UpdateJob(job))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllJobs(call: ApplicationCall, state: State) {
    val jobs = state.getJobStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(JobRoutes.New())

    simpleHtml("Jobs") {
        field("Count", jobs.size)
        showList(jobs) { nameList ->
            link(call, nameList)
        }
        showJobCount(call, state, state.getCharacterStorage().getAll(), "Distribution")
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showJobDetails(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    val backLink = call.application.href(JobRoutes())
    val deleteLink = call.application.href(JobRoutes.Delete(job.id))
    val editLink = call.application.href(JobRoutes.Edit(job.id))
    val characters = state.getEmployees(job.id).toSet()
    val previousCharacters = state.getPreviousEmployees(job.id).toSet() - characters

    simpleHtml("Job: ${job.name}") {
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
        action(editLink, "Edit")
        if (state.canDelete(job.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showJobEditor(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    val backLink = href(call, job.id)
    val updateLink = call.application.href(JobRoutes.Update(job.id))

    simpleHtml("Edit Job: ${job.name}") {
        form {
            selectName(job.name)
            selectRarityMap("Spells", SPELLS, state.getSpellStorage(), job.spells, false) { it.name }
            button("Update", updateLink)
        }
        back(backLink)
    }
}
