package com.gmail.maystruks08.domain.entities

enum class ModifierType { UPDATE, ADD, REMOVE }

class Change<T>(val entity: T, val modifierType: ModifierType)