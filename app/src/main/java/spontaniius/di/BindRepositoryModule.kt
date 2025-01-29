package spontaniius.di


import spontaniius.data.DefaultRepository
import spontaniius.data.Repository
import dagger.Binds
import dagger.Module

@Module
abstract class BindRepositoryModule {

    @ActivityScope
    @Binds
    abstract fun bindRepository(repository: DefaultRepository): Repository
}