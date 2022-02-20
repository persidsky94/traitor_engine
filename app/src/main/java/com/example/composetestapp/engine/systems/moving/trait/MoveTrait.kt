package com.example.composetestapp.engine.systems.moving.trait

import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.ZERO_VECTOR
import com.example.composetestapp.engine.objects.base.BaseTrait
import com.example.composetestapp.engine.systems.moving.MovingObjectParams
import com.example.composetestapp.engine.traits_without_systems.type.ObjectTypeTrait

interface MoveTrait: BaseTrait {
    fun update(deltaT: Long, force: Vector = ZERO_VECTOR)
    val params: MovingObjectParams
    val objectTypeTrait: ObjectTypeTrait

    override val traitTypeId: SystemId
        get() = SystemId(MOVE_SYSTEM_ID)

    companion object {
        const val MOVE_SYSTEM_ID = "MoveSystemId"
    }
}