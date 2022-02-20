package com.example.composetestapp.engine.systems.moving.force

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObject
import com.example.composetestapp.engine.systems.moving.trait.MoveTrait

class ForceTouchController: ForceEngine {
    var touchCoordinates: Coords? = null
    private fun getTouchCoords(): Coords? {
        return touchCoordinates // TODO: получать координаты тача
    }
    override fun forceForObject(movingObject: MovingObject): Vector {
        return (getTouchCoords()?.let { touchCoords ->
            (touchCoords - movingObject.params.coords).normalize()
        } ?: ZERO_VECTOR)* FORCE_QUOTIOENT
    }

    companion object {
        const val FORCE_QUOTIOENT = 1.0/240.0
    }
}

class ForceTouchControllerForTraits: ForceEngineForTraits {
    var touchCoordinates: Coords? = null
    private fun getTouchCoords(): Coords? {
        return touchCoordinates // TODO: получать координаты тача
    }

    override fun forceForTrait(moveTrait: MoveTrait): Vector {
        return (getTouchCoords()?.let { touchCoords ->
            (touchCoords - moveTrait.params.coords).normalize()
        } ?: ZERO_VECTOR)* FORCE_QUOTIENT
    }

    companion object {
        const val FORCE_QUOTIENT = 1.0/240.0
    }
}