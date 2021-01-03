package spontaniius

import android.app.Application
import spontaniius.DaggerApplicationComponent
import spontaniius.dependency_injection.ContextModule
import spontaniius.dependency_injection.CreateEventComponent
import spontaniius.dependency_injection.EventDaoModule
import spontaniius.dependency_injection.FindEventComponent
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [ContextModule::class,
        EventDaoModule::class]
)
interface ApplicationComponent {

    fun createEventComponent(): CreateEventComponent.Factory
    fun FindEventComponent(): FindEventComponent.Factory

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent
        fun contextModule(contextModule: ContextModule): Builder
    }
}

open class SpontaniiusApplication : Application() {
    open val applicationComponent: ApplicationComponent =
        DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()
//    open val applicationComponent: spontaniius.ApplicationComponent = DaggerApplicationComponent.builder().build()
}