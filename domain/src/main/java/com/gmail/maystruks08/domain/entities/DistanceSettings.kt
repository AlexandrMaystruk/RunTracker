package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

data class DistanceSettings(val checkpoints: List<Checkpoint>, val currentCheckpoint: Checkpoint)
