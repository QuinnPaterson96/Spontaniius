package com.example.spontaniius.dependency_injection

import com.example.spontaniius.ui.create_event.CreateEventActivity
import com.example.spontaniius.ui.find_event.FindEventActivity
import dagger.Subcomponent
import javax.inject.Scope





@ActivityScope
@Subcomponent(modules = [BindRepositoryModule::class])
interface FindEventComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): FindEventComponent
    }


    fun inject(findEventActivity: FindEventActivity)

}