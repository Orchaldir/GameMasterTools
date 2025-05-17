package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.CATASTROPHE
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.WAR
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.realm.getExistingWars
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRealmStatus(
    call: ApplicationCall,
    state: State,
    status: RealmStatus,
) {
    field("Status") {
        displayRealmStatus(call, state, status)
    }
}

fun HtmlBlockTag.displayRealmStatus(
    call: ApplicationCall,
    state: State,
    status: RealmStatus,
    showAlive: Boolean = true,
) {
    when (status) {
        LivingRealm -> if (showAlive) {
            +"Alive"
        }
        is DestroyedByCatastrophe -> {
            +"Destroyed by "
            link(call, state, status.catastrophe)
        }

        is DestroyedByWar -> {
            +"Destroyed by "
            link(call, state, status.war)
        }

        is UndefinedEndOfRealm -> "Ended"
    }
}

// edit

fun FORM.editRealmStatus(
    state: State,
    status: RealmStatus,
    startDate: Date?,
) {
    val endDate = status.endDate()
    val catastrophes = state.getExistingCatastrophes(endDate)
    val wars = state.getExistingWars(endDate)
    val type = status.getType()
    val validType = if (
        (type == RealmStatusType.Catastrophe && catastrophes.isEmpty()) ||
        (type == RealmStatusType.War && wars.isEmpty())
    ) {
        RealmStatusType.Undefined
    } else {
        type
    }

    showDetails("Status", true) {
        selectValue("Type", END, RealmStatusType.entries, validType) { type ->
            when (type) {
                RealmStatusType.Living -> false
                RealmStatusType.Catastrophe -> catastrophes.isEmpty()
                RealmStatusType.War -> wars.isEmpty()
                RealmStatusType.Undefined -> false
            }
        }

        when (status) {
            LivingRealm -> doNothing()
            is DestroyedByCatastrophe -> {
                selectElement(
                    state,
                    "Catastrophe",
                    combine(END, CATASTROPHE),
                    catastrophes,
                    status.catastrophe,
                )
                selectEndDate(state, startDate, status.date)
            }

            is DestroyedByWar -> {
                selectElement(
                    state,
                    "War",
                    combine(END, WAR),
                    wars,
                    status.war,
                )
                selectEndDate(state, startDate, status.date)
            }

            is UndefinedEndOfRealm -> selectEndDate(state, startDate, status.date)
        }
    }
}

private fun DETAILS.selectEndDate(
    state: State,
    startDate: Date?,
    date: Date?,
) = selectOptionalDate(
    state,
    "Date",
    date,
    combine(END, DATE),
    startDate,
)

// parse

fun parseRealmStatus(parameters: Parameters, state: State) = when (parse(parameters, END, RealmStatusType.Living)) {
    RealmStatusType.Living -> LivingRealm
    RealmStatusType.Catastrophe -> DestroyedByCatastrophe(
        parseCatastropheId(parameters, combine(END, CATASTROPHE)),
        parseEndDate(parameters, state),
    )

    RealmStatusType.War -> DestroyedByWar(
        parseWarId(parameters, combine(END, WAR)),
        parseEndDate(parameters, state),
    )

    RealmStatusType.Undefined -> UndefinedEndOfRealm(
        parseEndDate(parameters, state),
    )
}

private fun parseEndDate(
    parameters: Parameters,
    state: State,
) = parseOptionalDate(parameters, state, combine(END, DATE))