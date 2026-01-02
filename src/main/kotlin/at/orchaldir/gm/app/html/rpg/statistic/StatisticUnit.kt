package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.UNIT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
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

fun HtmlBlockTag.editStatisticUnit(
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
