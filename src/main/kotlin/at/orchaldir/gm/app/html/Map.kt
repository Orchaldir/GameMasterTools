package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.APP_TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.SomeOf
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import io.ktor.http.Parameters
import kotlinx.html.*

// show

fun HtmlBlockTag.showColorRarityMap(
    label: String,
    colors: RarityMap<Color>,
) = showRarityMap(label, colors) { color ->
    showColor(color)
}

fun HtmlBlockTag.showHairColorRarityMap(
    config: CharacterRenderConfig,
    label: String,
    colors: RarityMap<NormalHairColorEnum>,
) = showRarityMap(label, colors) { color ->
    showHairColor(config, color)
}

fun HtmlBlockTag.showSkinColorRarityMap(
    config: CharacterRenderConfig,
    label: String,
    colors: RarityMap<SkinColor>,
) = showRarityMap(label, colors) { color ->
    showSkinColor(config, color)
}

fun <K, V> HtmlBlockTag.showMap(
    label: String,
    map: Map<K, V>,
    content: LI.(K, V) -> Unit,
) {
    if (map.isNotEmpty()) {
        field(label) {
            showMap(map, content)
        }
    }
}

fun <K, V> HtmlBlockTag.showMap(
    map: Map<K, V>,
    content: LI.(K, V) -> Unit,
) {
    ul {
        map.forEach { (key, value) ->
            li {
                content(key, value)
            }
        }
    }
}

inline fun <reified T : Enum<T>> HtmlBlockTag.showRarityMap(
    label: String,
    rarityMap: RarityMap<T>,
    values: Set<T> = enumValues<T>().toSet(),
) {
    val sortedMap = reverseAndSort(rarityMap.getRarityFor(values))

    showDetails(label) {
        showMap(sortedMap) { rarity, values ->
            field(rarity.toString(), values.joinToString())
        }
    }
}

fun <T> HtmlBlockTag.showRarityMap(
    label: String,
    values: RarityMap<T>,
    isOpen: Boolean = false,
    content: LI.(T) -> Unit,
) {
    val sortedMap = reverseAndSort(values.getRarityMap())

    showDetails(label, isOpen) {
        showMap(sortedMap) { rarity, values ->
            fieldList(rarity.toString(), values) {
                content(it)
            }
        }
    }
}

// edit

fun <T> parseOneOf(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): OneOf<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return OneOf.init(map)
    }

    return OneOf(default)
}

fun <T> parseOneOrNone(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): OneOrNone<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return OneOrNone.init(map)
    }

    return OneOrNone(default)
}

fun <T> parseSomeOf(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
    default: Collection<T> = listOf(),
): SomeOf<T> {
    val map = parseRarityMap(parameters, selectId, converter)

    if (map != null) {
        return SomeOf.init(map)
    }

    return SomeOf(default)
}

private fun <T> parseRarityMap(
    parameters: Parameters,
    selectId: String,
    converter: (String) -> T,
) = parameters.getAll(selectId)
    ?.associate {
        val parts = it.split('-')
        val value = converter(parts[0])
        val rarity = Rarity.valueOf(parts[1])
        Pair(value, rarity)
    }