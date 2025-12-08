package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.selector.rpg.getAttributes
import at.orchaldir.gm.core.selector.rpg.getBaseDamageValues
import at.orchaldir.gm.core.selector.rpg.getDerivedAttributes
import at.orchaldir.gm.core.selector.rpg.getSkills
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showStatblockUpdate(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    update: StatblockUpdate,
) {
    val resolved = update.resolve(statblock)
    val attributes = state.sortStatistics(state.getAttributes())
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val damageValues = state.sortStatistics(state.getBaseDamageValues())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Stateblock Update", true) {
        table {
            showStatistics(call, state, statblock, update, resolved, attributes, "Attribute")
            showStatistics(call, state, statblock, update, resolved, derivedAttributes, "Derived Attribute")
            showStatistics(call, state, statblock, update, resolved, damageValues, "Base Damage Value")
            showStatistics(call, state, statblock, update, resolved, skills, "Skills")
        }
        field("Cost", statblock.calculateCost(state))
    }
}

private fun TABLE.showStatistics(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    update: StatblockUpdate,
    resolved: Statblock,
    statistics: List<Statistic>,
    label: String,
) {
    tr {
        th { +label }
        th { +"Base" }
        th { +"Modifier" }
        th { +"Result" }
        th { +"Cost" }
    }
    statistics.forEach { statistic ->
        val base = statblock.resolve(state, statistic)
        val modifier = update.statistics[statistic.id] ?: 0
        val result = resolved.resolve(state, statistic) ?: return@forEach

        tr {
            tdLink(call, state, statistic)
            tdSkipZero(base)
            tdSkipZero(modifier)
            tdInt(result)
            tdInt(statistic.data.cost().calculate(modifier))
        }
    }
}