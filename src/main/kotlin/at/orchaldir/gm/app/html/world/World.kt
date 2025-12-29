package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldPosition
import at.orchaldir.gm.app.html.util.parsePosition
import at.orchaldir.gm.app.html.util.selectPosition
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.ALLOWED_WORLD_POSITIONS
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.WorldId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWorld(
    call: ApplicationCall,
    state: State,
    world: World,
) {
    optionalField("Title", world.title)
    fieldPosition(call, state, world.position)
    showLocalElements(call, state, world.id)
}

// edit

fun HtmlBlockTag.editWorld(
    call: ApplicationCall,
    state: State,
    world: World,
) {
    selectName(world.name)
    selectOptionalNotEmptyString("Optional Title", world.title, TITLE)
    selectPosition(
        state,
        world.position,
        null,
        ALLOWED_WORLD_POSITIONS,
    )
}

// parse

fun parseWorldId(parameters: Parameters, param: String) = WorldId(parseInt(parameters, param))

fun parseWorld(state: State, parameters: Parameters, id: WorldId) = World(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parsePosition(parameters, state),
)
