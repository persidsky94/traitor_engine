package com.example.composetestapp.engine.objects.base

import com.example.composetestapp.engine.EngineObject
import com.example.composetestapp.engine.ObjId

class BaseObjectImpl(
    override val objId: ObjId = EngineObject.generateNextObjId()
): BaseObject {

    override val traits: MutableList<BaseTrait> = mutableListOf()

    override fun addTrait(trait: BaseTrait): BaseObject {
        traits.add(trait)
        return this
    }
}