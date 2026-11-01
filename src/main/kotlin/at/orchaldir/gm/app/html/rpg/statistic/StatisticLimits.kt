package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.rpg.statistic.StatisticDataType
import at.orchaldir.gm.core.model.rpg.statistic.StatisticLimits
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatisticLimits(
    limits: StatisticLimits,
    label: String,
) {
    showDetails(label, true) {
        fieldStatisticLimit("Attributes", limits.attributes)
        fieldStatisticLimit("Damage Attributes", limits.damage)
        fieldStatisticLimit("Derived Attributes", limits.derivedAttributes)
        fieldStatisticLimit("Saves", limits.saves)
        fieldStatisticLimit("Skills", limits.skills)
    }
}

// edit

fun HtmlBlockTag.editStatisticLimits(
    label: String,
    limits: StatisticLimits,
    param: String,
) {
    showDetails(label, true) {
        editStatisticLimit("Attributes", limits.attributes, StatisticDataType.Attribute, param)
        editStatisticLimit("Damage Attributes", limits.damage, StatisticDataType.Damage, param)
        editStatisticLimit("Derived Attributes", limits.derivedAttributes, StatisticDataType.DerivedAttribute, param)
        editStatisticLimit("Saves", limits.saves, StatisticDataType.Save, param)
        editStatisticLimit("Skills", limits.skills, StatisticDataType.Skill, param)
    }
}

// parse

fun parseStatisticLimits(
    parameters: Parameters,
    param: String,
) = StatisticLimits(
    parseStatisticLimit(parameters, StatisticDataType.Attribute, param),
    parseStatisticLimit(parameters, StatisticDataType.Damage, param),
    parseStatisticLimit(parameters, StatisticDataType.DerivedAttribute, param),
    parseStatisticLimit(parameters, StatisticDataType.Save, param),
    parseStatisticLimit(parameters, StatisticDataType.Skill, param),
)
