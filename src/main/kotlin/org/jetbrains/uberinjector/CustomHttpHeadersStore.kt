package org.jetbrains.uberinjector

import java.util.*
import kotlin.collections.ArrayList

object CustomHttpHeadersStore {
    private val items = Collections.synchronizedList(mutableListOf<String>())

    fun addItem(item: String) {
        items.add(item)
    }

    fun getItems(): Collection<String> {
        return ArrayList(items)
    }
}