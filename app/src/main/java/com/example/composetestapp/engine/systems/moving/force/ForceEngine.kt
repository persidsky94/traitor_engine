package com.example.composetestapp.engine.systems.moving.force

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObject

interface ForceEngine {
    fun forceForObject(movingObject: MovingObject): Vector
}


