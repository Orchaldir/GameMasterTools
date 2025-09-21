package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Attribute
import at.orchaldir.gm.core.model.character.statistic.StatisticData
import at.orchaldir.gm.core.model.character.statistic.StatisticDataType
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatisticData(
    call: ApplicationCall,
    state: State,
    data: StatisticData,
) {
    field("Type", data.getType())

    when (data) {
        is Attribute -> showBaseValue(call, state, data.base)
    }
}

// edit

fun FORM.editStatisticData(
    state: State,
    statistic: StatisticId,
    data: StatisticData,
) {
    selectValue(
        "Type",
        TYPE,
        StatisticDataType.entries,
        data.getType(),
    )

    when (data) {
        is Attribute -> editBaseValue(state, statistic, data.base)
    }
}

// parse

fun parseStatisticData(
    parameters: Parameters,
) = when (parse(parameters, TYPE, StatisticDataType.Attribute)) {
    StatisticDataType.Attribute -> Attribute(
        parseBaseValue(parameters),
    )
}
