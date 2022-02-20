package com.example.composetestapp.engine.systems.moving.trait

import com.example.composetestapp.engine.*
import com.example.composetestapp.engine.objects.base.BaseTrait
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.traits_without_systems.position.MutablePositionTrait
import com.example.composetestapp.engine.traits_without_systems.type.ObjectTypeTrait
import kotlin.math.absoluteValue

class MoveTraitImpl(
    override val objectTypeTrait: ObjectTypeTrait,
    startVelocity: Vector,
    private val formula: (Coords, Vector, Long, Vector) -> MovingObjectParams,
    private val positionTrait: MutablePositionTrait,
    override val parentObjId: ObjId,
    override val traitObjId: TraitObjId = BaseTrait.generateNextTraitObjId()
) : MoveTrait {
    private var _coords: Coords
        get() {
            return positionTrait.coords
        }
        set(value) {
            positionTrait.coords = value
        }

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