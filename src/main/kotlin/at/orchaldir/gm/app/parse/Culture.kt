package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.name.NoNamingConvention
import at.orchaldir.gm.core.model.culture.style.HairStyleType
import at.orchaldir.gm.core.model.culture.style.StyleOptions
import at.orchaldir.gm.core.model.race.appearance.BeardStyleType
import io.ktor.http.*
import io.ktor.server.util.*

fun parseCulture(
    parameters: Parameters,
    id: CultureId,
): Culture {
    val name = parameters.getOrFail(NAME)

    return Culture(
        id,
        name,
        NoNamingConvention,
        StyleOptions(
            parseRarityMap(parameters, BEARD_STYLE, BeardStyleType::valueOf),
            parseRarityMap(parameters, GOATEE_STYLE, GoateeStyle::valueOf),
            parseRarityMap(parameters, MOUSTACHE_STYLE, MoustacheStyle::valueOf),
            parseRarityMap(parameters, HAIR_STYLE, HairStyleType::valueOf),
            parseRarityMap(parameters, LIP_COLORS, Color::valueOf),
        ),
    )
}