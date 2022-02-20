package com.example.composetestapp.engine.traits_without_systems.position

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.TraitObjId
import com.example.composetestapp.engine.objects.base.BaseTrait

class MutablePositionTrait(
    var coords: Coords,
    override val parentObjId: ObjId,
    override val traitObjId: TraitObjId = BaseTrait.generateNextTraitObjId()
): PositionTrait {
    override val data: Coords
        get() = coords
}