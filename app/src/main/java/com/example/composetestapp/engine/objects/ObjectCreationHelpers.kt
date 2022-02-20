package com.example.composetestapp.engine.objects

import com.example.composetestapp.engine.Coords
import com.example.composetestapp.engine.GameField
import com.example.composetestapp.engine.Vector
import com.example.composetestapp.engine.ZERO_VECTOR
import com.example.composetestapp.engine.objects.base.BaseObject
import com.example.composetestapp.engine.objects.concrete_objects.*
import com.example.composetestapp.engine.systems.moving.ignoreForceFormula
import com.example.composetestapp.engine.systems.moving.verticalSinCoordinateFormula
import kotlin.random.Random

val random = Random(1)
fun createRandomFrictingSpaceship(gameField: GameField, friction: Int): FrictingSpaceship {
    return FrictingSpaceship(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = ZERO_VECTOR,
        friction = friction
    )
}

fun createRandomFrictingSpaceshipOnTraits(gameField: GameField, friction: Int): BaseObject {
    return frictingSpaceshipOnTraits(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = ZERO_VECTOR,
        friction = friction
    )
}

fun createRandomCoin(): CoinObject {
    return CoinObject(
        startCoords = Coords(
            random.nextDouble(1000.0),
            random.nextDouble(1000.0)
        ),
        startVelocity = ZERO_VECTOR,
        formula = ignoreForceFormula,
        radius = 30.0
    )
}

fun createRandomCoinOnTraits(): BaseObject {
    return coinObjectOnTraits(
        startCoords = Coords(
            random.nextDouble(1000.0),
            random.nextDouble(1000.0)
        ),
        startVelocity = ZERO_VECTOR,
        formula = ignoreForceFormula,
        radius = 30.0
    )
}

fun createRandomMovingCoin(gameField: GameField, coinRadius: Double = 30.0, maxDeviation: Double = 50.0): CoinObject {
    val startCoords = Coords(
        random.nextDouble(gameField.width.toDouble()),
        random.nextDouble(gameField.height.toDouble())
    )
    return CoinObject(
        startCoords = startCoords,
        startVelocity = ZERO_VECTOR,
        formula = verticalSinCoordinateFormula(startingCoords = startCoords, maxDeviation = maxDeviation),
        radius = coinRadius
    )
}

fun createRandomMovingCoinOnTraits(
    gameField: GameField,
    coinRadius: Double = 30.0,
    maxDeviation: Double = 50.0
): BaseObject {
    val startCoords = Coords(
        random.nextDouble(gameField.width.toDouble()),
        random.nextDouble(gameField.height.toDouble())
    )
    return coinObjectOnTraits(
        startCoords = startCoords,
        startVelocity = ZERO_VECTOR,
        formula = verticalSinCoordinateFormula(startingCoords = startCoords, maxDeviation = maxDeviation),
        radius = coinRadius
    )
}

fun createRandomAsteroid(
    gameField: GameField,
    asteroidRadius: Double = 20.0,
    maxVelocityCoordinate: Double = 0.1
): AsteroidObject {
    return AsteroidObject(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = Vector(
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate),
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate)
        ),
        formula = ignoreForceFormula,
        radius = asteroidRadius
    )
}

fun createRandomAsteroidOnTraits(
    gameField: GameField,
    asteroidRadius: Double = 20.0,
    maxVelocityCoordinate: Double = 0.1
): BaseObject {
    return asteroidObjectOnTraits(
        startCoords = Coords(
            random.nextDouble(gameField.width.toDouble()),
            random.nextDouble(gameField.height.toDouble())
        ),
        startVelocity = Vector(
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate),
            random.nextDouble(from = -maxVelocityCoordinate, until = maxVelocityCoordinate)
        ),
        formula = ignoreForceFormula,
        radius = asteroidRadius
    )
}