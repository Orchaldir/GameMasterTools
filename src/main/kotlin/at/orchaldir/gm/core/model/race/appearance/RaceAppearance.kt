package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.AppearanceType
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.EarsLayout
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val RACE_APPEARANCE_TYPE = "Race Appearance"

@JvmInline
@Serializable
value class RaceAppearanceId(val value: Int) : Id<RaceAppearanceId> {

    override fun next() = RaceAppearanceId(value + 1)
    override fun type() = RACE_APPEARANCE_TYPE
    override fun value() = value

}

@Serializable
data class RaceAppearance(
    val id: RaceAppearanceId,
    val name: Name = Name.init(id),
    val appearanceTypes: OneOf<AppearanceType> = OneOf(AppearanceType.Body),
    val earsLayout: OneOf<EarsLayout> = OneOf(EarsLayout.NormalEars),
    val earShapes: OneOf<EarShape> = OneOf(EarShape.Round),
    val eyesLayout: OneOf<EyesLayout> = OneOf(EyesLayout.TwoEyes),
    val eye: EyeOptions = EyeOptions(),
    val foot: FootOptions = FootOptions(),
    val hair: HairOptions = HairOptions(),
    val horn: HornOptions = HornOptions(),
    val mouth: MouthOptions = MouthOptions(),
    val skin: SkinOptions = SkinOptions(),
    val tail: TailOptions = TailOptions(),
    val wing: WingOptions = WingOptions(),
) : ElementWithSimpleName<RaceAppearanceId> {

    override fun id() = id
    override fun name() = name.text

    fun contains(material: MaterialId) = skin.contains(material) ||
            horn.contains(material) ||
            tail.contains(material)

}
