package com.example.composetestapp.engine.systems.moving.system

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.TraitObjId
import com.example.composetestapp.engine.forEachParallel
import com.example.composetestapp.engine.systems.base_system.BaseTimeSystem
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.systems.moving.force.ForceEngineForTraits
import com.example.composetestapp.engine.systems.moving.trait.MoveTrait
import com.example.composetestapp.engine.systems.moving.trait.MoveTrait.Companion.MOVE_SYSTEM_ID
import com.example.composetestapp.engine.traits_without_systems.type.ObjectTypeTrait

class MoveSystem(
    val forceEngine: ForceEngineForTraits
): BaseTimeSystem<MoveTrait>() {

    override val systemId: SystemId = SystemId(MOVE_SYSTEM_ID)

    private val movingObjects = mutableListOf<MoveTrait>()
    //TODO: is this needed still?
    val movingObjectsParamsToTypes: List<Pair<MovingObjectParams, ObjectTypeTrait>>
        get() = movingObjects.map { it.params to it.objectTypeTrait}

    override fun addTrait(trait: MoveTrait) {
        movingObjects.add(trait)
    }

    override fun removeTrait(trait: MoveTrait) {
        movingObjects.remove(trait)
    }

    override fun removeTraitById(id: TraitObjId) {
        movingObjects.removeAll { it.traitObjId == id }
    }

    override fun removeTraitsByParentObjectId(id: ObjId) {
        movingObjects.removeAll { it.parentObjId == id }
    }

    override suspend fun update(deltaT: Long) {
        movingObjects.forEachParallel { moveTrait ->
            val force = forceEngine.forceForTrait(moveTrait)
            moveTrait.update(deltaT = deltaT, force = force)
        }
    }
}