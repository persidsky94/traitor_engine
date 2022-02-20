package com.example.composetestapp.engine.systems.base_system

import com.example.composetestapp.engine.objects.base.BaseTrait

abstract class BaseTimeSystem<ConcreteTrait: BaseTrait>: BaseSystem<ConcreteTrait>() {
    abstract suspend fun update(deltaT: Long)
}