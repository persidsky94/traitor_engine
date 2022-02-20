package com.example.composetestapp.engine.systems.moving

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.traits_without_systems.type.ObjectType

interface MoveEngine {
    suspend fun update(deltaT: Long)
    fun addMovingObject(movingObject: MovingObject)
    fun removeMovingObject(movingObject: MovingObject)
    fun removeObjectById(id: ObjId)
    val movingObjectsParamsToTypes: List<Pair<MovingObjectParams, ObjectType>>
}

