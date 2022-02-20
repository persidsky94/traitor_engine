package com.example.composetestapp.engine.traits_without_systems.type

sealed class ObjectType {
    class Spaceship: ObjectType()
    class Coin(val radius: Double): ObjectType()
    class Asteroid(val radius: Double): ObjectType()
}