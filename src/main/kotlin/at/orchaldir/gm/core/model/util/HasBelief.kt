package at.orchaldir.gm.core.model.util

interface HasBelief {
    fun belief(): History<BeliefStatus>
}