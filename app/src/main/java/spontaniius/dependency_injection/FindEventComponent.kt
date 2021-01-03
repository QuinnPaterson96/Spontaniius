package spontaniius.dependency_injection

import spontaniius.ui.find_event.FindEventActivity
import dagger.Subcomponent


@ActivityScope
@Subcomponent(modules = [BindRepositoryModule::class])
interface FindEventComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): FindEventComponent
    }


    fun inject(findEventActivity: FindEventActivity)

}