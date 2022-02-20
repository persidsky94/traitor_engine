package com.example.composetestapp.engine.systems.base_system

import com.example.composetestapp.engine.objects.base.BaseTrait

abstract class BaseRulesSystem<ConcreteTrait: BaseTrait>: BaseSystem<ConcreteTrait>() {
    abstract fun applyRules()
}