package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.character.editInventory
import at.orchaldir.gm.app.html.character.parseInventory
import at.orchaldir.gm.app.routes.handleEditElementSplit
import at.orchaldir.gm.app.routes.handlePreviewElementSplit
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.showSplitEditor
import at.orchaldir.gm.core.generator.DateGenerator
import at.orchaldir.gm.core.generator.EquipmentGenerator
import at.orchaldir.gm.core.model.character.UniqueEquipment
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import at.orchaldir.gm.utils.RandomNumberGenerator
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

fun Application.configureInventoryRouting() {
    routing {
        get<CharacterRoutes.Inventory.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                CharacterRoutes.Inventory(),
                HtmlBlockTag::editInventory,
                HtmlBlockTag::showCharacterFrontAndBack,
                "Edit inventory of",
            )
        }
        post<CharacterRoutes.Inventory.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                CharacterRoutes.Inventory(),
                ::parseInventory,
                HtmlBlockTag::editInventory,
                HtmlBlockTag::showCharacterFrontAndBack,
                "Preview inventory of",
            )
        }
        post<CharacterRoutes.Inventory.Update> { update ->
            handleUpdateElement(update.id, ::parseInventory, "Update inventory of")
        }
        post<CharacterRoutes.Inventory.Generate> { update ->
            logger.info { "Generate ${update.id.print()}'s inventory" }

            val state = STORE.getState()
            val character = state.getCharacterStorage().getOrThrow(update.id)
            val generator = EquipmentGenerator.create(state, character)
            val updatedCharacter = if (generator != null) {
                character.copy(equipped = UniqueEquipment(generator.generate()))
            } else {
                character
            }

            showSplitEditor(
                CharacterRoutes.Inventory(),
                state,
                updatedCharacter,
                HtmlBlockTag::editInventory,
                HtmlBlockTag::showCharacterFrontAndBack,
            )
        }
    }
}

