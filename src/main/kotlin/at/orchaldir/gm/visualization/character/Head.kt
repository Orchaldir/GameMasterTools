package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig

fun visualizeHead(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head, skin: Skin) {
    renderer.renderRectangle(aabb, config.getOptions(skin))
}