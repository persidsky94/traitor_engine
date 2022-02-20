package com.example.composetestapp.engine

import com.example.composetestapp.engine.objects.*
import com.example.composetestapp.engine.objects.object_dependent_handlers.AsteroidCollisionHandler
import com.example.composetestapp.engine.objects.object_dependent_handlers.AsteroidCollisionHandlerForTraits
import com.example.composetestapp.engine.objects.object_dependent_handlers.CoinCollisionHandler
import com.example.composetestapp.engine.objects.object_dependent_handlers.CoinCollisionHandlerForTraits
import com.example.composetestapp.engine.systems.collision.CollisionEngine
import com.example.composetestapp.engine.systems.collision.CollisionEngineImpl
import com.example.composetestapp.engine.systems.collision.collision_detection.CollisionDetectorImpl
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerForTraitsImpl
import com.example.composetestapp.engine.systems.collision.collision_handling.CollisionHandlerImpl
import com.example.composetestapp.engine.systems.collision.collision_handling.with
import com.example.composetestapp.engine.systems.collision.system.CollisionSystem
import com.example.composetestapp.engine.systems.collision.trait.CollidableTrait
import com.example.composetestapp.engine.systems.moving.MoveEngine
import com.example.composetestapp.engine.systems.moving.MoveEngineImpl
import com.example.composetestapp.engine.systems.moving.MovingObject
import com.example.composetestapp.engine.systems.moving.force.ForceEngineForTraitsImpl
import com.example.composetestapp.engine.systems.moving.force.ForceEngineImpl
import com.example.composetestapp.engine.systems.moving.force.ForceTouchController
import com.example.composetestapp.engine.systems.moving.force.ForceTouchControllerForTraits
import com.example.composetestapp.engine.systems.moving.system.MoveSystem
import com.example.composetestapp.engine.systems.removation.RemoveObjectMediatorForGameEngine
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

fun setupGameEngineOnTraits(
    forceTouchController: ForceTouchControllerForTraits,
    gameField: GameField
): Pair<MoveSystem, GameEngine> {
    val gameEngine = GameEngineImpl()

    val forceEngine = ForceEngineForTraitsImpl()
    val moveSystem = MoveSystem(forceEngine)

    val collisionHandlerForTraits = CollisionHandlerForTraitsImpl()
    val collisionSystem = CollisionSystem(
        collisionDetector = CollisionDetectorImpl(),
        collisionHandler = collisionHandlerForTraits
    )

    gameEngine
        .addSystem(moveSystem)
        .addSystem(collisionSystem)

    val removeObjectMediator = RemoveObjectMediatorForGameEngine(gameEngine)
    var coinCreator: (()->Unit)? = null
    val coinOnCollisionRemover = CoinCollisionHandlerForTraits(objectRemover = removeObjectMediator)
        .with { hittedObj, hittingObj ->
            coinCreator?.invoke()
        }
    coinCreator = {
        val coin = createRandomMovingCoinOnTraits(gameField)
        coin.traits.filterIsInstance<CollidableTrait<*,*>>().forEach { collidableTrait ->
            collisionHandlerForTraits.addCollisionHandlerForHittedTrait(collidableTrait , coinOnCollisionRemover)
        }
        gameEngine.addObject(coin)
    }
    val asteroidOnCollisionSpaceshipDestroyer =
        AsteroidCollisionHandlerForTraits(objectRemover = removeObjectMediator)

    val spaceships = buildList(5) {
        createRandomFrictingSpaceshipOnTraits(
            gameField = gameField,
            friction = random.nextInt(from = 95, until = 99)
        )
    }
    val coins = buildList(6) { createRandomMovingCoinOnTraits(gameField) }
    val asteroids = buildList(3) { createRandomAsteroidOnTraits(gameField) }

    spaceships.forEach { spaceship ->
        forceEngine.addObjectController(spaceship.objId, forceTouchController)
        gameEngine.addObject(spaceship)
    }

    coins.forEach { coin ->
        coin.traits.filterIsInstance<CollidableTrait<*,*>>().forEach { collidableTrait ->
            collisionHandlerForTraits.addCollisionHandlerForHittedTrait(collidableTrait , coinOnCollisionRemover)
        }
        gameEngine.addObject(coin)
    }

    asteroids.forEach { asteroid ->
        asteroid.traits.filterIsInstance<CollidableTrait<*,*>>().forEach { collidableTrait ->
            collisionHandlerForTraits.addCollisionHandlerForHittedTrait(collidableTrait, asteroidOnCollisionSpaceshipDestroyer)
        }
        gameEngine.addObject(asteroid)
    }

    return moveSystem to gameEngine
}

