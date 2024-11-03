package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import kotlin.math.abs

class Node<T>(
    val value: T,
    var next: Node<T>? = null,
    var prev: Node<T>? = null,
)

class NodeWithIndex<T>(
    val node: Node<T>,
    val index: Long,
)

private fun <T> Node<T>.asNodeWithIndex(index: Long) = NodeWithIndex(this, index)

interface LongDoublyLinkedList<out T> {
    val size: Long
    val lastIndex: Long

    operator fun get(index: Long): T
}

interface MutableLongDoublyLinkedList<T> : LongDoublyLinkedList<T> {
    fun add(index: Long, value: T)
    fun removeAt(index: Long): T
    fun clear()
}

class MutableLongDoublyLinkedListImpl<T> : MutableLongDoublyLinkedList<T> {
    private var firstNode: Node<T>? = null
    private var lastNode: Node<T>? = null
    private var lastAccessedNode: NodeWithIndex<T>? = null
    private var sizeValue: Long = 0

    override val size: Long
        get() = sizeValue
    override val lastIndex: Long
        get() = size - 1

    override fun get(index: Long): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        }
        val node = findNodeAtIndex(index)
        lastAccessedNode = node.asNodeWithIndex(index)
        return node.value
    }

    override fun add(index: Long, value: T) {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        }
        val newNode = Node(value)
        when {
            size == 0L -> {
                firstNode = newNode
                lastNode = newNode
            }
            index == 0L -> {
                newNode.next = firstNode
                firstNode?.prev = newNode
                firstNode = newNode
            }
            index == size -> {
                newNode.prev = lastNode
                lastNode?.next = newNode
                lastNode = newNode
            }
            else -> {
                val node = findNodeAtIndex(index)
                newNode.next = node
                newNode.prev = node.prev
                node.prev?.next = newNode
                node.prev = newNode
            }
        }
        lastAccessedNode = newNode.asNodeWithIndex(index)
        sizeValue++
    }

    override fun removeAt(index: Long): T {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        }
        if (size == 1L) {
            val node = firstNode
            firstNode = null
            lastNode = null
            sizeValue = 0
            lastAccessedNode = null
            return checkNotNull(node).value
        }
        val node = findNodeAtIndex(index)
        when {
            node.prev == null -> {
                firstNode = node.next
                node.next?.prev = null
                lastAccessedNode = firstNode?.asNodeWithIndex(0)
            }
            node.next == null -> {
                lastNode = node.prev
                node.prev?.next = null
                lastAccessedNode = lastNode?.asNodeWithIndex(size - 2)
            }
            else -> {
                node.prev?.next = node.next
                node.next?.prev = node.prev
                lastAccessedNode = node.prev?.asNodeWithIndex(index - 1)
            }
        }
        sizeValue--
        return node.value
    }

    override fun clear() {
        firstNode = null
        lastNode = null
        lastAccessedNode = null
        sizeValue = 0
    }

    private fun findNodeAtIndex(index: Long): Node<T> {
        val nodeWithIndex = checkNotNull(determineNearestStartingNode(index))
        var node = nodeWithIndex.node
        var currentIndex = nodeWithIndex.index
        while (currentIndex != index) {
            node = if (currentIndex < index) {
                currentIndex++
                checkNotNull(node.next)
            } else {
                currentIndex--
                checkNotNull(node.prev)
            }
        }
        return node
    }

    private fun determineNearestStartingNode(index: Long): NodeWithIndex<T>? {
        val distanceFromFirst = index
        val distanceFromLast = size - index - 1
        val distanceFromLastAccessed = lastAccessedNode?.index?.let {
            abs(it - index)
        } ?: Long.MAX_VALUE

        return when {
            distanceFromLastAccessed <= distanceFromFirst && distanceFromLastAccessed <= distanceFromLast -> {
                lastAccessedNode
            }
            distanceFromFirst <= distanceFromLast -> {
                firstNode?.asNodeWithIndex(0)
            }
            else -> {
                lastNode?.asNodeWithIndex(size - 1)
            }
        }
    }
}

fun <T> mutableLongListOf() = MutableLongDoublyLinkedListImpl<T>()

fun <T> mutableLongListOf(vararg elements: T) = MutableLongDoublyLinkedListImpl<T>().apply {
    elements.forEachIndexed { index, element ->
        add(index.toLong(), element)
    }
}