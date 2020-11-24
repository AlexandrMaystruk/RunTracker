package com.gmail.maystruks08.nfcruntracker.core.di.checkpoint_editor

import com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor.CheckpointEditorFragment
import dagger.Subcomponent

@Subcomponent(modules = [CheckpointEditorModule::class])
@CheckpointEditorScope
interface CheckpointEditorComponent {

    fun inject(fragment: CheckpointEditorFragment)

}