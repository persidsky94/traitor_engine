package com.example.composetestapp.engine.systems.removation

import com.example.composetestapp.engine.ObjId

interface RemoveObjectMediator {
    fun onRemoveObject(objId: ObjId)
}