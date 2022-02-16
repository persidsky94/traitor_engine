package com.example.composetestapp.engine.systems.moving

import com.example.composetestapp.engine.*
import kotlin.math.absoluteValue

open class MovingObjectImpl(
    override val objectType: ObjectType,
    startCoords: Coords,
    startVelocity: Vector,
    private val formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    override val objectId: ObjId = EngineObject.generateNextObjId()
): MovingObject {
    private var _coords: Coords = startCoords

    private var _velocity: Vector = startVelocity
        set(value) {
            field = value
            _direction = value
        }

    private var _direction: Vector = 1.0 to 0.0
        set(value) {
            if (value.first.absoluteValue > 0.01 || value.second.absoluteValue > 0.01) {
                field = value
            }
        }

    override val params: MovingObjectParams
        get() = MovingObjectParams(_coords, _velocity, _direction)

    override fun update(deltaT: Long, force: Vector) {
        val movingObjectParams = formula(_coords, _velocity, deltaT, force)
        _coords = movingObjectParams.coords
        _velocity = movingObjectParams.velocity
    }
}