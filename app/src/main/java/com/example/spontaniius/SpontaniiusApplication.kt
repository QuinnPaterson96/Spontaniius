package com.example.spontaniius

import android.app.Application
import com.example.spontaniius.dependency_injection.ContextModule
import com.example.spontaniius.dependency_injection.CreateEventComponent
import com.example.spontaniius.dependency_injection.EventDaoModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [ContextModule::class,
        EventDaoModule::class]
)
interface ApplicationComponent {

    fun createEventComponent(): CreateEventComponent.Factory

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent
        fun contextModule(contextModule: ContextModule): Builder
    }
}

open class SpontaniiusApplication : Application() {
    open val applicationComponent: ApplicationComponent =
        DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()
//    open val applicationComponent: com.example.spontaniius.ApplicationComponent = DaggerApplicationComponent.builder().build()
}