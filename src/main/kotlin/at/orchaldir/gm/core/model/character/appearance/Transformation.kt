package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.Beard

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