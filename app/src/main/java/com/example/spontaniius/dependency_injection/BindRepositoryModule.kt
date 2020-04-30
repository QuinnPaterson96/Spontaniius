package com.example.spontaniius.dependency_injection

import com.example.spontaniius.data.DefaultRepository
import com.example.spontaniius.data.Repository
import dagger.Binds
import dagger.Module

@Module
abstract class BindRepositoryModule {

    @ActivityScope
    @Binds
    abstract fun bindRepository(repository: DefaultRepository): Repository
}