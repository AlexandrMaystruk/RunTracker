package com.gmail.maystruks08.domain.entities.account


/**
 * AssesLevel.Admin can:
 *  - start/stop distance work
 *  - create/remove new races, distances
 *  - add/remove new checkpoints
 *
 *  AssesLevel.User can:
 *  - view data
 *  - change runner state
 *  - select current checkpoint
 */
sealed class AssesLevel{
    object Admin: AssesLevel()
    object User: AssesLevel()
}
