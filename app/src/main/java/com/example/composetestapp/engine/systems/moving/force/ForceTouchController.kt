package com.example.composetestapp.engine.systems.moving.force

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.systems.moving.MovingObject

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