package github.desafio

import android.app.Application
import github.desafio.repository.PullRequestRepository
import github.desafio.repository.RepositoriesRepository
import github.desafio.viewmodel.PullRequestViewModel
import github.desafio.viewmodel.RepositoryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CustomApplication)
            modules(myModule)
        }
    }
}

val myModule = module {
    single { RepositoriesRepository() }
    single { PullRequestRepository() }
    single { RepositoryViewModel(get()) }
    single { PullRequestViewModel(get()) }
}