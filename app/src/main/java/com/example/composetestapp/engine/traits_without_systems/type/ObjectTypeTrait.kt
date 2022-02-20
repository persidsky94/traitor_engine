package com.example.composetestapp.engine.traits_without_systems.type

import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.objects.base.BaseTrait

interface ObjectTypeTrait: BaseTrait {
    val objectType: ObjectType

    override val traitTypeId: SystemId
        get() = SystemId(TYPE_SYSTEM_ID)

    companion object {
        const val TYPE_SYSTEM_ID = "TypeSystemId"
    }
}