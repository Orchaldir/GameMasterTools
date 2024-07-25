package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.fashion.ClothingSet
import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parameters[param]?.toInt() ?: 0)

fun parseFashion(id: FashionId, parameters: Parameters): Fashion {
    val name = parameters.getOrFail(NAME)

    return Fashion(
        id,
        name,
        parseOneOf(parameters, CLOTHING_SETS, ClothingSet::valueOf),
        parseOneOf(parameters, DRESS, ::parseItemTemplateId),
        parseOneOf(parameters, FOOTWEAR, ::parseItemTemplateId),
        parseOneOf(parameters, HAT, ::parseItemTemplateId),
        parseOneOf(parameters, PANTS, ::parseItemTemplateId),
        parseOneOf(parameters, SHIRT, ::parseItemTemplateId),
        parseOneOf(parameters, SKIRT, ::parseItemTemplateId),
    )
}
