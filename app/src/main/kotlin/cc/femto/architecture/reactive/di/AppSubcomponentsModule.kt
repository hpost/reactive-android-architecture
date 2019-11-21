package cc.femto.architecture.reactive.di

import cc.femto.architecture.reactive.components.home.di.HomeComponent
import dagger.Module

@Module(subcomponents = [HomeComponent::class])
class AppSubcomponentsModule {}
