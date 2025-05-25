package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Shield
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.currency.visualizeComplexShape

data class ShieldConfig(
    val radius: SizeConfig<Factor>,
) {
    fun getRadius(aabb: AABB, size: Size) =
        aabb.convertHeight(radius.convert(size))
}

fun visualizeShield(
    state: CharacterRenderState,
    body: Body,
    shield: Shield,
) {
    val fill = shield.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val (left, right) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val radius = state.config.equipment.shield.getRadius(state.aabb, shield.size)
    val renderer = state.renderer.getLayer(HELD_EQUIPMENT_LAYER)

    visualizeComplexShape(renderer, left, radius, shield.shape, options)
}