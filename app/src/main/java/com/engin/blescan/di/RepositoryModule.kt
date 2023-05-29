package com.engin.blescan.di

import com.engin.blescan.repo.BluetoothRepository
import com.engin.blescan.repo.BluetoothRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindRepositories {
        @Binds
        abstract fun bindBluetoothRepository(bluetoothRepositoryImpl: BluetoothRepositoryImpl): BluetoothRepository
    }
}