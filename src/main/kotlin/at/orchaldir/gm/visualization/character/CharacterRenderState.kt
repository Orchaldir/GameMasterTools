package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.EquipmentElementMap
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

data class CharacterRenderState(
    val state: State,
    val aabb: AABB,
    val config: CharacterRenderConfig,
    val renderer: MultiLayerRenderer,
    val renderFront: Boolean,
    val equipped: EquipmentElementMap,
    val colors: Colors = UndefinedColors,
) : RenderState {

    override fun state() = state
    override fun renderer() = renderer
    override fun lineOptions() = config.line

    fun getColor(part: ColorSchemeItemPart) = part.getColor(state, colors)

    fun getBeardLayer() = getLayer(ABOVE_EQUIPMENT_LAYER)
    fun getTailLayer() = getLayer(-ABOVE_EQUIPMENT_LAYER)

    fun getLayer(layer: Int, offset: Int = 0) = renderer
        .getLayer(getLayerIndex(layer, offset))

    fun getLayerIndex(layer: Int, offset: Int = 0) = if (renderFront) {
        layer
    } else {
        -layer
    } + offset

    fun getSideOffset(offset: Factor) = if (renderFront) {
        offset
    } else {
        -offset
    }

    fun getCenter(
        left: Point2d,
        right: Point2d,
        set: Set<BodySlot>,
        preferredSlot: BodySlot,
    ) = if (renderFront) {
        if (set.contains(preferredSlot)) {
            right
        } else {
            left
        }
    } else {
        if (set.contains(preferredSlot)) {
            left
        } else {
            right
        }
    }
}