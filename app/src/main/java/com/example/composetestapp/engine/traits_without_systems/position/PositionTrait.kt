package com.example.composetestapp.engine.traits_without_systems.position

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.objects.base.InfoTrait

interface PositionTrait: InfoTrait<Coords> {
    override val traitTypeId: SystemId
        get() = SystemId(POSITION_SYSTEM_ID)

    companion object {
        const val POSITION_SYSTEM_ID = "PositionSystemId"
    }
}