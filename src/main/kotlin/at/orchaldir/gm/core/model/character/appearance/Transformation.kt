package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.Beard
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.beard.NormalBeard
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.HairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.mouth.Mouth
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth

fun updateBeard(appearance: Appearance, beard: Beard): Appearance {
    return when (appearance) {
        is HeadOnly -> appearance.copy(updateBeard(appearance.head, beard))
        is HumanoidBody -> appearance.copy(head = updateBeard(appearance.head, beard))
        UndefinedAppearance -> appearance
    }
}

fun updateBeard(head: Head, beard: Beard) = head.copy(mouth = updateBeard(head.mouth, beard))

fun updateBeard(mouth: Mouth, beard: Beard): Mouth {
    return when (mouth) {
        is NormalMouth -> mouth.copy(beard)
        else -> mouth
    }
}

fun updateHairColor(appearance: Appearance, color: HairColor): Appearance {
    return when (appearance) {
        is HeadOnly -> appearance.copy(
            head = updateHairColor(appearance.head, color),
            skin = updateFurColor(appearance.skin, color),
        )

        is HumanoidBody -> appearance.copy(
            head = updateHairColor(appearance.head, color),
            skin = updateFurColor(appearance.skin, color),
        )

        UndefinedAppearance -> appearance
    }
}

fun updateHairColor(head: Head, color: HairColor) = head.copy(
    hair = updateHairColor(head.hair, color),
    mouth = updateHairColor(head.mouth, color),
)

fun updateHairColor(hair: Hair, color: HairColor) = when (hair) {
    NoHair -> hair
    is NormalHair -> hair.copy(color = color)
}

fun updateHairColor(mouth: Mouth, color: HairColor) = when (mouth) {
    is NormalMouth -> mouth.copy(updateHairColor(mouth.beard, color))
    else -> mouth
}

fun updateHairColor(beard: Beard, color: HairColor) = when (beard) {
    NoBeard -> beard
    is NormalBeard -> beard.copy(color = color)
}

fun updateFurColor(skin: Skin, color: HairColor) = when (skin) {
    is Fur -> Fur(color)
    else -> skin
}