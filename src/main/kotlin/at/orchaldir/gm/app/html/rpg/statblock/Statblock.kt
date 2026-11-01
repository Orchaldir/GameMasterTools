package at.orchaldir.gm.app.html.rpg.statblock

import at.orchaldir.gm.app.STATISTIC
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.trait.editCharacterTraitGroups
import at.orchaldir.gm.app.html.rpg.trait.parseCharacterTraits
import at.orchaldir.gm.app.html.rpg.trait.showCharacterTraits
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statistic.AbsoluteStatisticLimit
import at.orchaldir.gm.core.model.rpg.statistic.RelativeStatisticLimit
import at.orchaldir.gm.core.model.rpg.statistic.Statistic
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticLimitLayer
import at.orchaldir.gm.core.model.rpg.statistic.StatisticLimits
import at.orchaldir.gm.core.model.rpg.statistic.UndefinedStatisticLimit
import at.orchaldir.gm.core.selector.rpg.*
import at.orchaldir.gm.core.selector.util.sortStatistics
import at.orchaldir.gm.utils.math.RangeInt
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showStatblock(
    call: ApplicationCall,
    state: State,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val attributeValues = statblock.resolve(state, attributes)
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val derivedValues = statblock.resolve(state, derivedAttributes)
    val baseDamages = state.sortStatistics(state.getBaseDamageValues())
    val baseDamageValues = statblock.resolve(state, baseDamages)
    val saves = state.sortStatistics(state.getSaves())
    val saveValues = statblock.resolve(state, saves)
    val skills = state.sortStatistics(state.getSkills())
    val skillValues = statblock.resolve(state, skills)

    showDetails("Stateblock", true) {
        showStatistics(call, state, attributeValues, "Attributes")
        showStatistics(call, state, derivedValues, "Derived Attributes")
        showStatistics(call, state, baseDamageValues, "Base Damage Values")
        showStatistics(call, state, saveValues, "Saves")
        showStatistics(call, state, skillValues, "Skills")
        showCharacterTraits(call, state, statblock.traits)
        field("Cost", statblock.calculateCost(state))
    }
}

private fun DETAILS.showStatistics(
    call: ApplicationCall,
    state: State,
    values: List<Pair<Statistic, Int>>,
    label: String,
) {
    fieldList(label, values) { (statistic, value) ->
        link(call, state, statistic)
        +": "
        +statistic.data.display(value)
    }
}

// edit

fun HtmlBlockTag.editStatblock(
    call: ApplicationCall,
    state: State,
    layer: StatisticLimitLayer,
    statblock: Statblock,
) {
    val attributes = state.sortStatistics(state.getAttributes())
    val derivedAttributes = state.sortStatistics(state.getDerivedAttributes())
    val damageValues = state.sortStatistics(state.getBaseDamageValues())
    val saves = state.sortStatistics(state.getSaves())
    val skills = state.sortStatistics(state.getSkills())

    showDetails("Stateblock", true) {
        table {
            editStatistics(state, call, layer, statblock, attributes, "Attribute")
            editStatistics(state, call, layer, statblock, derivedAttributes, "Derived Attribute")
            editStatistics(state, call, layer, statblock, damageValues, "Base Damage Value")
            editStatistics(state, call, layer, statblock, saves, "Saves")
            editStatistics(state, call, layer, statblock, skills, "Skills")
        }
        editCharacterTraitGroups(call, state, statblock.traits, isOpen = true)
        field("Cost", statblock.calculateCost(state))
    }
}

private fun TABLE.editStatistics(
    state: State,
    call: ApplicationCall,
    layer: StatisticLimitLayer,
    statblock: Statblock,
    statistics: List<Statistic>,
    label: String,
) {
    if (statistics.isEmpty()) {
        return
    }

    tr {
        th { +label }
        th { +"Offset" }
        th { +"Result" }
        th { +"Cost" }
    }
    statistics.forEach { statistic ->
        val offset = statblock.statistics[statistic.id] ?: 0
        val value = statblock.resolve(state, statistic)
        val base = (value ?: 0) - offset
        val limit = statistic.getLimit(state, layer)
        val range = limit.getRelativeRange(base, 10)

        tr {
            tdLink(call, state, statistic)
            td {
                selectInt(
                    offset,
                    range,
                    1,
                    combine(STATISTIC, statistic.id.value),
                )
            }
            tdSkipZero(value)
            tdInt(statistic.data.cost().calculate(offset))
        }
    }
}

// parse

fun parseStatblock(
    state: State,
    parameters: Parameters,
) = Statblock(
    parseStatistics(state, parameters),
    parseCharacterTraits(parameters),
)

fun parseStatistics(
    state: State,
    parameters: Parameters,
): MutableMap<StatisticId, Int> {
    val values = mutableMapOf<StatisticId, Int>()

    state.getStatisticStorage()
        .getAll()
        .forEach { attribute ->
            parseSimpleOptionalInt(parameters, combine(STATISTIC, attribute.id.value))?.let {
                if (it != 0) {
                    values[attribute.id] = it
                }
            }
        }
    return values
}