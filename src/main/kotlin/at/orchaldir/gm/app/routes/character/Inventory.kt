package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.html.character.editInventory
import at.orchaldir.gm.app.html.character.parseInventory
import at.orchaldir.gm.app.routes.handleEditElementSplit
import at.orchaldir.gm.app.routes.handlePreviewElementSplit
import at.orchaldir.gm.app.routes.handleUpdateElement
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

fun Application.configureInventoryRouting() {
    routing {
        get<CharacterRoutes.Inventory.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                CharacterRoutes.Inventory(),
                HtmlBlockTag::editInventory,
                HtmlBlockTag::showCharacterFrontAndBack,
            )
        }
        post<CharacterRoutes.Inventory.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                CharacterRoutes.Inventory(),
                ::parseInventory,
                HtmlBlockTag::editInventory,
                HtmlBlockTag::showCharacterFrontAndBack,
            )
        }
        post<CharacterRoutes.Inventory.Update> { update ->
            handleUpdateElement(update.id, ::parseInventory, "Update inventory of")
        }
    }
}

