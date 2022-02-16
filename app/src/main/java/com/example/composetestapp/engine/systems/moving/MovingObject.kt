package com.example.composetestapp.engine.systems.moving

import com.example.composetestapp.engine.EngineObject
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.ZERO_VECTOR

interface MovingObject: EngineObject {
    fun update(deltaT: Long, force: Vector = ZERO_VECTOR)
    val params: MovingObjectParams
}