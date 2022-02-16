package com.example.composetestapp.engine

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun repeat(times: Int, action: (() -> Unit)) {
    if (times >= 1) {
        for (i in 1..times) {
            action()
        }
    }
}

fun <T> buildList(times: Int, createElement: (() -> T)): List<T> {
    val result = mutableListOf<T>()
    repeat(times) { result.add(createElement()) }
    return result
}

suspend fun <A, B> Iterable<A>.mapParallel(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

suspend fun <A> Iterable<A>.forEachParallel(f: suspend (A) -> Unit) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}