package com.example.composetestapp.engine

import com.example.composetestapp.engine.objects.createRandomAsteroid
import com.example.composetestapp.engine.objects.createRandomFrictingSpaceship
import com.example.composetestapp.engine.objects.createRandomMovingCoin
import com.example.composetestapp.engine.objects.object_dependent_handlers.AsteroidCollisionHandler
import com.example.composetestapp.engine.objects.object_dependent_handlers.CoinCollisionHandler
import com.example.composetestapp.engine.objects.random
import com.example.composetestapp.engine.systems.collision.CollisionEngine
import com.example.composetestapp.engine.systems.collision.CollisionEngineImpl
import com.example.composetestapp.engine.systems.collision.collision_detection.CollisionDetectorImpl
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerImpl
import com.example.composetestapp.engine.systems.collision.collision_handling.with
import com.example.composetestapp.engine.systems.moving.MoveEngine
import com.example.composetestapp.engine.systems.moving.MoveEngineImpl
import com.example.composetestapp.engine.systems.moving.MovingObject
import com.example.composetestapp.engine.systems.moving.force.ForceEngineImpl
import com.example.composetestapp.engine.systems.moving.force.ForceTouchController
import com.example.composetestapp.engine.systems.removation.RemoveObjectMediatorImpl

fun setupGameEngine(
    forceTouchController: ForceTouchController,
    gameField: GameField
): Pair<MoveEngine, CollisionEngine> {
    val forceEngine = ForceEngineImpl()
    val moveEngine = MoveEngineImpl(forceEngine)

    val collisionHandler = CollisionHandlerImpl()
    val collisionEngine = CollisionEngineImpl(
        collisionDetector = CollisionDetectorImpl(),
        collisionHandler = collisionHandler
    )

    val removeObjectMediator = RemoveObjectMediatorImpl(moveEngine, collisionEngine)
    var coinCreator: (()->Unit)? = null
    val coinOnCollisionRemover = CoinCollisionHandler(objectRemover = removeObjectMediator)
        .with { hittedObj, hittingObj ->
            coinCreator?.invoke()
        }
    coinCreator = {
        val coin = createRandomMovingCoin(gameField)
        collisionEngine.addCollidableObject(coin)
        collisionHandler.addCollisionHandlerForHittedObject(coin, coinOnCollisionRemover)
        if (coin is MovingObject) {
            moveEngine.addMovingObject(coin)
        }
    }
    val asteroidOnCollisionSpaceshipDestroyer =
        AsteroidCollisionHandler(objectRemover = removeObjectMediator)

    val spaceships = buildList(5) {
        createRandomFrictingSpaceship(
            gameField,
            random.nextInt(from = 95, until = 99)
        )
    }
    val coins = buildList(6) { createRandomMovingCoin(gameField) }
    val asteroids = buildList(3) { createRandomAsteroid(gameField) }

    spaceships.forEach { spaceship ->
        forceEngine.addObjectController(spaceship.objectId, forceTouchController)
        moveEngine.addMovingObject(spaceship)
        collisionEngine.addCollidableObject(spaceship)
    }

    coins.forEach { coin ->
        collisionEngine.addCollidableObject(coin)
        collisionHandler.addCollisionHandlerForHittedObject(coin, coinOnCollisionRemover)
        if (coin is MovingObject) {
            moveEngine.addMovingObject(coin)
        }
    }

    asteroids.forEach { asteroid ->
        collisionEngine.addCollidableObject(asteroid)
        collisionHandler.addCollisionHandlerForHittedObject(asteroid, asteroidOnCollisionSpaceshipDestroyer)
        moveEngine.addMovingObject(asteroid)
    }

    return moveEngine to collisionEngine
}