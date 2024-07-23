package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.fashion.Fashion
import at.orchaldir.gm.core.model.fashion.FashionId
import io.ktor.http.*
import io.ktor.server.util.*


fun parseFashion(id: FashionId, parameters: Parameters): Fashion {
    val name = parameters.getOrFail(NAME)

    return Fashion(id, name)
}
