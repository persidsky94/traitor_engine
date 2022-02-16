package com.example.composetestapp.engine.systems.moving

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.ObjectType
import com.example.composetestapp.engine.forEachParallel
import com.example.composetestapp.engine.systems.moving.force.ForceEngine

class MoveEngineImpl(
    val forceEngine: ForceEngine
): MoveEngine {
    private val movingObjects = mutableListOf<MovingObject>()
    override val movingObjectsParamsToTypes: List<Pair<MovingObjectParams, ObjectType>>
        get() = movingObjects.map { it.params to it.objectType}

    override fun addMovingObject(movingObject: MovingObject) {
        movingObjects.add(movingObject)
    }

    override fun removeMovingObject(movingObject: MovingObject) {
        movingObjects.remove(movingObject)
    }

    override fun removeObjectById(id: ObjId) {
        movingObjects.removeAll { it.objectId == id }
    }

    override suspend fun update(deltaT: Long) {
        movingObjects.forEachParallel { movingObject ->
            val force = forceEngine.forceForObject(movingObject)
            movingObject.update(deltaT = deltaT, force = force)
        }
    }
}