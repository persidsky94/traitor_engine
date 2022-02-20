package com.example.composetestapp.engine.objects.base

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.SystemId
import com.example.composetestapp.engine.TraitObjId

interface BaseTrait {
    val parentObjId: ObjId
    val traitObjId: TraitObjId

    val traitTypeId: SystemId

    companion object {
        var nextFreeTraitId = 0
        fun generateNextTraitObjId(): TraitObjId {
            return TraitObjId(++nextFreeTraitId)
        }
    }
}