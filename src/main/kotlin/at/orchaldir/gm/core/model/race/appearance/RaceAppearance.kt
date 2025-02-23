package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.EyesLayout
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.OneOf
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
    val name: String = "RaceAppearance ${id.value}",
    val appearanceTypes: OneOf<AppearanceType> = OneOf(AppearanceType.entries),
    val skinTypes: OneOf<SkinType> = OneOf(SkinType.entries),
    val furColors: OneOf<Color> = OneOf(Color.entries),
    val scalesColors: OneOf<Color> = OneOf(Color.entries),
    val normalSkinColors: OneOf<SkinColor> = OneOf(SkinColor.entries),
    val exoticSkinColors: OneOf<Color> = OneOf(Color.entries),
    val earsLayout: OneOf<EarsLayout> = OneOf(EarsLayout.entries),
    val earShapes: OneOf<EarShape> = OneOf(EarShape.entries),
    val eyesLayout: OneOf<EyesLayout> = OneOf(EyesLayout.entries),
    val eyeOptions: EyeOptions = EyeOptions(),
    val footOptions: FootOptions = FootOptions(),
    val hairOptions: HairOptions = HairOptions(),
    val mouthTypes: OneOf<MouthType> = OneOf(MouthType.entries),
    val wingOptions: WingOptions = WingOptions(),
) : ElementWithSimpleName<RaceAppearanceId> {

    override fun id() = id
    override fun name() = name

}
