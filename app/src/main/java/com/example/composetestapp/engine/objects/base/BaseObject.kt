package com.example.composetestapp.engine.objects.base

import com.example.composetestapp.engine.ObjId

interface BaseObject {
    val objId: ObjId
    val traits: List<BaseTrait>
    fun addTrait(trait: BaseTrait): BaseObject
}