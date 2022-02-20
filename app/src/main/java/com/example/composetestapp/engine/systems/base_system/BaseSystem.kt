package com.example.composetestapp.engine.systems.base_system

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.TraitObjId
import com.example.composetestapp.engine.objects.base.BaseTrait

interface IBaseSystem<ConcreteTrait: BaseTrait> {
    val systemId: SystemId
    fun addBaseTrait(trait: BaseTrait)
    fun removeTrait(trait: ConcreteTrait)
    fun removeTraitById(id: TraitObjId)
    fun removeTraitsByParentObjectId(id: ObjId)
}

abstract class BaseSystem<ConcreteTrait: BaseTrait>: IBaseSystem<ConcreteTrait> {
    override fun addBaseTrait(trait: BaseTrait) {
        val concreteTrait = trait as? ConcreteTrait
        concreteTrait?.let {
            addTrait(it)
        }
    }

    protected abstract fun addTrait(trait: ConcreteTrait)
}