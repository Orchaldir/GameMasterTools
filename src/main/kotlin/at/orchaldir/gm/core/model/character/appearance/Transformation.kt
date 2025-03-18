package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.Beard
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.beard.NormalBeard
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.mouth.Mouth
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.util.Color

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

fun updateHairColor(appearance: Appearance, color: Color): Appearance {
    return when (appearance) {
        is HeadOnly -> appearance.copy(updateHairColor(appearance.head, color))
        is HumanoidBody -> appearance.copy(
            updateSkinColor(appearance.body, color),
            updateHairColor(appearance.head, color),
        )

        UndefinedAppearance -> appearance
    }
}

fun updateHairColor(head: Head, color: Color) = head.copy(
    hair = updateHairColor(head.hair, color),
    mouth = updateHairColor(head.mouth, color),
    skin = updateSkinColor(head.skin, color),
)

fun updateHairColor(hair: Hair, color: Color) = when (hair) {
    NoHair -> hair
    is NormalHair -> hair.copy(color = color)
}

fun updateHairColor(mouth: Mouth, color: Color) = when (mouth) {
    is NormalMouth -> mouth.copy(updateHairColor(mouth.beard, color))
    else -> mouth
}

fun updateHairColor(beard: Beard, color: Color) = when (beard) {
    NoBeard -> beard
    is NormalBeard -> beard.copy(color = color)
}

fun updateSkinColor(body: Body, color: Color) = body.copy(
    skin = updateSkinColor(body.skin, color),
)

fun updateSkinColor(skin: Skin, color: Color) = when (skin) {
    is Fur -> Fur(color)
    else -> skin
}