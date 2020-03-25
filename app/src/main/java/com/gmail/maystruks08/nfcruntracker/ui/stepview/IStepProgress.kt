package com.gmail.maystruks08.nfcruntracker.ui.stepview

interface IStepProgress {

    /**
     * Set number of steps for progress
     * @param stepsCount steps number
     */
    @Throws(IllegalStateException::class)
    fun setStepsCount(stepsCount: Int)
    /**
     * Go to next step
     * @param isCurrentDone if true marks current selected step as done
     * @return true if all steps are finished
     */
    fun nextStep(isCurrentDone: Boolean): Boolean

    /**
     * Mark current selected step as done
     */
    fun markCurrentAsDone()

    /**
     * Mark current selected step as undone
     */
    fun markCurrentAsUndone()

    /**
     * Set title for each step
     * @param titles list of titles to apply to step views. Size should be the same as steps count
     */
    fun setStepTitles(titles: List<String>)

    /**
     * Checks if step is finished
     * @param stepPosition step position to check
     */
    fun isStepDone(stepPosition: Int = -1): Boolean

    /**
     * Checks if all steps of a progress are finished
     * @return true if all steps marked as finished
     */
    fun isProgressFinished(): Boolean
}