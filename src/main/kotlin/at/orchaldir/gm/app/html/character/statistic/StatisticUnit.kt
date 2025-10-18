package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.UNIT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.character.statistic.DerivedAttribute
import at.orchaldir.gm.core.model.character.statistic.StatisticData
import at.orchaldir.gm.core.model.character.statistic.StatisticUnit
import at.orchaldir.gm.core.model.character.statistic.StatisticUnitType
import at.orchaldir.gm.core.model.character.statistic.SuffixedStatisticUnit
import at.orchaldir.gm.core.model.character.statistic.UnitlessStatistic
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldStatisticUnit(
    unit: StatisticUnit,
) {
    field("Unit") {
        displayStatisticUnit(unit)
    }
}

fun HtmlBlockTag.displayStatisticUnit(
    data: StatisticData,
    showUnitless: Boolean = true,
) {
    if (data is DerivedAttribute) {
        displayStatisticUnit(data.unit, showUnitless)
    }
}

fun HtmlBlockTag.displayStatisticUnit(
    unit: StatisticUnit,
    showUnitless: Boolean = true,
) {
    when (unit) {
        is SuffixedStatisticUnit -> +"Suffix '${unit.suffix.text}'"
        UnitlessStatistic -> if (showUnitless) {
            +"Unitless"
        }
    }
}

// edit

fun FORM.editStatisticUnit(
    unit: StatisticUnit,
) {
    showDetails("Unit", true) {
        selectValue(
            "Type",
            combine(UNIT, TYPE),
            StatisticUnitType.entries,
            unit.getType(),
        )

        when (unit) {
            is SuffixedStatisticUnit -> selectNotEmptyString("Suffix", unit.suffix, UNIT)
            UnitlessStatistic -> doNothing()
        }
    }
}

// parse

fun parseStatisticUnit(
    parameters: Parameters,
) = when (parse(parameters, combine(UNIT, TYPE), StatisticUnitType.Unitless)) {
    StatisticUnitType.Suffix -> SuffixedStatisticUnit(
        parseNotEmptyString(parameters, UNIT, "?"),
    )

    StatisticUnitType.Unitless -> UnitlessStatistic
}
