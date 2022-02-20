package com.example.composetestapp.engine.traits_without_systems.type

import com.example.composetestapp.engine.ObjId
import com.example.composetestapp.engine.TraitObjId
import com.example.composetestapp.engine.objects.base.BaseTrait

class ObjectTypeTraitImpl(
    override val objectType: ObjectType,
    override val parentObjId: ObjId,
    override val traitObjId: TraitObjId = BaseTrait.generateNextTraitObjId()
) : ObjectTypeTrait