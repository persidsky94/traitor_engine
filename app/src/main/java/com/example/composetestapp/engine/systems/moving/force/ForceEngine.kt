package com.example.composetestapp.engine.systems.moving.force

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObject
import com.example.composetestapp.engine.systems.moving.trait.MoveTrait

interface ForceEngine {
    fun forceForObject(movingObject: MovingObject): Vector
}

interface ForceEngineForTraits {
    fun forceForTrait(moveTrait: MoveTrait): Vector
}


