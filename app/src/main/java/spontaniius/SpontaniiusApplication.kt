package spontaniius

import android.app.Application
import spontaniius.di.ContextModule
import spontaniius.di.CreateEventComponent
import spontaniius.di.EventDaoModule
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

//    open val applicationComponent: spontaniius.ApplicationComponent = DaggerApplicationComponent.builder().build()
}