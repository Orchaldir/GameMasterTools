package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.CHARACTER_TRAIT
import at.orchaldir.gm.app.REMOVE
import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.trait.editCharacterTraitGroups
import at.orchaldir.gm.app.html.rpg.trait.parseCharacterTraitId
import at.orchaldir.gm.app.html.rpg.trait.parseCharacterTraits
import at.orchaldir.gm.app.html.rpg.trait.showCharacterTraits
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.selector.rpg.getAttributes
import at.orchaldir.gm.core.selector.rpg.getBaseDamageValues
import at.orchaldir.gm.core.selector.rpg.getDerivedAttributes
import at.orchaldir.gm.core.selector.rpg.getSkills
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showStatblockUpdate(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    update: StatblockUpdate,
    resolved: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val damageValues = state.sortStatistics(state.getBaseDamageValues())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Statblock Update", true) {
        table {
            showStatistics(call, state, statblock, update, resolved, attributes, "Attribute")
            showStatistics(call, state, statblock, update, resolved, derivedAttributes, "Derived Attribute")
            showStatistics(call, state, statblock, update, resolved, damageValues, "Base Damage Value")
            showStatistics(call, state, statblock, update, resolved, skills, "Skills")
        }
        showCharacterTraits(call, state, update.removedTraits, "Removed Traits")
        showCharacterTraits(call, state, update.addedTraits, "Added Traits")
        field("Update Cost", update.calculateCost(state))
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
    val filtered = statistics
        .filter { update.statistics.containsKey(it.id) }

    if (filtered.isEmpty()) {
        return
    }

    tr {
        th { +label }
        th { +"Base" }
        th { +"Modifier" }
        th { +"Result" }
        th { +"Cost" }
    }

    filtered
        .forEach { statistic ->
            val base = statblock.resolve(state, statistic)
            val modifier = update.statistics[statistic.id] ?: return@forEach
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

// edit

fun HtmlBlockTag.editStatblockUpdate(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
    update: StatblockUpdate,
    resolved: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val damageValues = state.sortStatistics(state.getBaseDamageValues())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Statblock Update", true) {
        table {
            editStatistics(call, state, statblock, update, resolved, attributes, "Attribute")
            editStatistics(call, state, statblock, update, resolved, derivedAttributes, "Derived Attribute")
            editStatistics(call, state, statblock, update, resolved, damageValues, "Base Damage Value")
            editStatistics(call, state, statblock, update, resolved, skills, "Skills")
        }
        selectElements(
            state,
            "Removed Traits",
            combine(REMOVE, CHARACTER_TRAIT),
            state.getCharacterTraitStorage().get(statblock.traits),
            update.removedTraits,
        )
        editCharacterTraitGroups(
            call,
            state,
            update.addedTraits,
            isOpen = true,
            label = "Added Traits",
            blockedTraits = statblock.traits - update.removedTraits,
        )
        field("Update Cost", update.calculateCost(state))
    }
}

private fun TABLE.editStatistics(
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
        val result = resolved.resolve(state, statistic)

        tr {
            tdLink(call, state, statistic)
            tdSkipZero(base)
            td {
                selectInt(
                    modifier,
                    -10,
                    +10,
                    1,
                    combine(STATISTIC, statistic.id.value),
                )
            }
            tdSkipZero(result)
            tdInt(statistic.data.cost().calculate(modifier))
        }
    }
}

// parse

fun parseStatblockUpdate(
    state: State,
    parameters: Parameters,
) = StatblockUpdate(
    parseStatistics(state, parameters),
    parseCharacterTraits(parameters, CHARACTER_TRAIT),
    parseElements(
        parameters,
        combine(REMOVE, CHARACTER_TRAIT),
        ::parseCharacterTraitId,
    ),
)
