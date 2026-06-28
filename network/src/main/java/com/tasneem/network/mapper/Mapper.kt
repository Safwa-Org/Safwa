package com.tasneem.network.mapper

fun interface Mapper<in I, out O> {
    fun map(input: I): O
}

fun <I, O> Mapper<I, O>.mapList(items: List<I>): List<O> = items.map { map(it) }