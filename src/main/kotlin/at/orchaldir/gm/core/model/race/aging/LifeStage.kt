package at.orchaldir.gm.core.model.race.aging

interface LifeStage {
    fun name(): String
    fun maxAge(): Int
}