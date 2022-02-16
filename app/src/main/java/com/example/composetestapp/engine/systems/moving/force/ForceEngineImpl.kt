package com.example.composetestapp.engine.systems.moving.force

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.ZERO_VECTOR
import com.example.composetestapp.engine.systems.moving.MovingObject

class ForceEngineImpl: ForceEngine {
    private val objectIdsToControllers = mutableMapOf<ObjId, ForceEngine>()

    fun addObjectController(objectId: ObjId, forceEngine: ForceEngine) {
        objectIdsToControllers[objectId] = forceEngine
    }

    override fun forceForObject(movingObject: MovingObject): Vector {
        return objectIdsToControllers[movingObject.objectId]
            ?.forceForObject(movingObject)
            ?: ZERO_VECTOR
    }
}