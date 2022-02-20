package com.example.composetestapp.engine

import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.systems.base_system.BaseRulesSystem
import com.example.composetestapp.engine.systems.base_system.BaseSystem
import com.example.composetestapp.engine.systems.base_system.BaseTimeSystem

class GameEngineImpl : GameEngine {

    private val systems: MutableMap<SystemId, BaseSystem<*>> = mutableMapOf()
    private var timeSystems: List<BaseTimeSystem<*>> = listOf()
    private var rulesSystems: List<BaseRulesSystem<*>> = listOf()

    override fun addSystem(system: BaseSystem<*>): GameEngine {
        systems[system.systemId] = system
        timeSystems = systems.values.filterIsInstance<BaseTimeSystem<*>>()
        rulesSystems = systems.values.filterIsInstance<BaseRulesSystem<*>>()
        return this
    }

    override fun addObject(baseObject: BaseObject) {
        baseObject.traits.forEach { trait ->
            val system = systems[trait.traitTypeId]
            system?.addBaseTrait(trait)
        }
    }

    override fun removeObjectById(objId: ObjId) {
        systems.values.forEach { system ->
            system.removeTraitsByParentObjectId(objId)
        }
    }

    override suspend fun update(deltaT: Long) {
        timeSystems.forEach { timeSystem ->
            timeSystem.update(deltaT)
        }
        rulesSystems.forEach { rulesSystem ->
            rulesSystem.applyRules()
        }
    }
}