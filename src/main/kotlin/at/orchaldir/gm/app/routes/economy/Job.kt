package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.editJob
import at.orchaldir.gm.app.html.model.economy.parseJob
import at.orchaldir.gm.app.html.model.economy.showJob
import at.orchaldir.gm.core.action.CreateJob
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.model.util.SortJob
import at.orchaldir.gm.core.selector.character.countCharactersWithCurrentOrFormerJob
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.core.selector.economy.money.display
import at.orchaldir.gm.core.selector.getDefaultCurrency
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.core.selector.util.sortJobs
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$JOB_TYPE")
class JobRoutes {
    @Resource("all")
    class All(
        val sort: SortJob = SortJob.Name,
        val parent: JobRoutes = JobRoutes(),
    )

    @Resource("details")
    class Details(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("new")
    class New(val parent: JobRoutes = JobRoutes())

    @Resource("delete")
    class Delete(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("edit")
    class Edit(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("preview")
    class Preview(val id: JobId, val parent: JobRoutes = JobRoutes())

    @Resource("update")
    class Update(val id: JobId, val parent: JobRoutes = JobRoutes())
}

fun Application.configureJobRouting() {
    routing {
        get<JobRoutes.All> { all ->
            logger.info { "Get all jobs" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllJobs(call, STORE.getState(), all.sort)
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

            call.respondRedirect(call.application.href(JobRoutes.All()))

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
        post<JobRoutes.Preview> { preview ->
            logger.info { "Preview job ${preview.id.value}" }

            val state = STORE.getState()
            val job = parseJob(preview.id, call.receiveParameters())

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

private fun HTML.showAllJobs(call: ApplicationCall, state: State, sort: SortJob) {
    val currency = state.getDefaultCurrency()
    val jobs = state.sortJobs(sort)
    val createLink = call.application.href(JobRoutes.New())

    simpleHtml("Jobs") {
        field("Count", jobs.size)
        fieldLink("Currency", call, currency)
        showSortTableLinks(call, SortJob.entries, JobRoutes(), JobRoutes::All)
        table {
            tr {
                th { +"Name" }
                thMultiLines(listOf("Yearly", "Income"))
                th { +"Gender" }
                th { +"Uniform" }
                th { +"Characters" }
                th { +"Domains" }
                th { +"Spells" }
            }
            jobs.forEach { job ->
                tr {
                    tdLink(call, state, job)
                    td {
                        when (job.income) {
                            UndefinedIncome -> doNothing()
                            is AffordableStandardOfLiving -> link(call, state, job.income.standard)
                            is Salary -> +currency.display(job.income.yearlySalary)
                        }
                    }
                    tdOptionalEnum(job.preferredGender)
                    tdInlineIds(call, state, job.uniforms.getValues().filterNotNull())
                    tdSkipZero(state.countCharactersWithCurrentOrFormerJob(job.id))
                    tdSkipZero(state.countDomains(job.id))
                    tdSkipZero(job.spells.getRarityMap().size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showJobDetails(
    call: ApplicationCall,
    state: State,
    job: Job,
) {
    val backLink = call.application.href(JobRoutes.All())
    val deleteLink = call.application.href(JobRoutes.Delete(job.id))
    val editLink = call.application.href(JobRoutes.Edit(job.id))

    simpleHtmlDetails(job) {
        showJob(call, state, job)

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
    val previewLink = call.application.href(JobRoutes.Preview(job.id))
    val updateLink = call.application.href(JobRoutes.Update(job.id))

    simpleHtmlEditor(job) {
        formWithPreview(previewLink, updateLink, backLink) {
            editJob(state, job)
        }
    }
}


