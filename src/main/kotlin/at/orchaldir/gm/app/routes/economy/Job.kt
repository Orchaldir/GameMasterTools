package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.economy.editJob
import at.orchaldir.gm.app.html.economy.parseJob
import at.orchaldir.gm.app.html.economy.showJob
import at.orchaldir.gm.app.routes.*
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
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$JOB_TYPE")
class JobRoutes : Routes<JobId, SortJob> {
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
    override fun all(call: ApplicationCall, sort: SortJob) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: JobId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: JobId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureJobRouting() {
    routing {
        get<JobRoutes.All> { all ->
            val state = STORE.getState()
            val currency = state.getDefaultCurrency()

            handleShowAllElements(
                JobRoutes(),
                state.sortJobs(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column(listOf("Employer", "Type")) {
                        when (it.employerType) {
                            EmployerType.Business -> doNothing()
                            else -> tdEnum(it.employerType)
                        }
                    },
                    tdColumn(listOf("Yearly", "Income")) {
                        when (it.income) {
                            UndefinedIncome -> doNothing()
                            is AffordableStandardOfLiving -> link(call, state, it.income.standard)
                            is Salary -> +currency.display(it.income.yearlySalary)
                        }
                    },
                    Column("Gender") { tdOptionalEnum(it.preferredGender) },
                    Column("Uniforms") { tdInlineIds(call, state, it.uniforms.getValues().filterNotNull()) },
                    countColumnForId("Characters", state::countCharactersWithCurrentOrFormerJob),
                    countColumnForId("Domains", state::countDomains),
                    countColumn("Spells") { it.spells.getRarityMap().size },
                ),
            )
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


