package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.appearance.EnumRarity
import at.orchaldir.gm.core.model.appearance.Rarity
import io.ktor.http.*


fun <T> parseEnumRarity(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
) = EnumRarity(parameters.getAll(selectId)
    ?.associate {
        val parts = it.split('-')
        val value = converter(parts[0])
        val rarity = Rarity.valueOf(parts[1])
        Pair(value, rarity)
    } ?: mapOf())