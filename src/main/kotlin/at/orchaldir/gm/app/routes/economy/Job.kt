package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.editJob
import at.orchaldir.gm.app.html.economy.parseJob
import at.orchaldir.gm.app.html.economy.showJob
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.*
import at.orchaldir.gm.core.model.util.SortJob
import at.orchaldir.gm.core.selector.character.countCharactersWithCurrentOrFormerJob
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
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$JOB_TYPE")
class JobRoutes: Routes<JobId> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: JobId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: JobId) = call.application.href(Edit(id))
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
            handleShowElement(details.id, JobRoutes(), HtmlBlockTag::showJob)
        }
        get<JobRoutes.New> {
            handleCreateElement(STORE.getState().getJobStorage()) { id ->
                JobRoutes.Edit(id)
            }
        }
        get<JobRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, JobRoutes.All())
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
            val job = parseJob(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showJobEditor(call, state, job)
            }
        }
        post<JobRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseJob)
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
                thMultiLines(listOf("Employer", "Type"))
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
                        when (job.employerType) {
                            EmployerType.Business -> doNothing()
                            else -> +job.employerType.name
                        }
                    }
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


