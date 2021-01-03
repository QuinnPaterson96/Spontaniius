package spontaniius.dependency_injection

import spontaniius.ui.BottomNavigationActivity
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope


@ActivityScope
@Subcomponent(modules = [BindRepositoryModule::class])
interface CreateEventComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CreateEventComponent
    }


    fun inject(mainActivity: BottomNavigationActivity)

}