package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.hasNoHasPositionsIn

fun State.canDeleteMoon(moon: MoonId) = getCharactersLivingIn(moon).isEmpty()
        && getCharactersPreviouslyLivingIn(moon).isEmpty()
        && hasNoHasPositionsIn(moon)

fun State.getMoons(plane: PlaneId) = getMoonStorage()
    .getAll()
    .filter { it.plane == plane }

fun State.getMoonsContaining(material: MaterialId) = getMoonStorage()
    .getAll()
    .filter { it.resources.contains(material) }


