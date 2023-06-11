package com.example.militaryaccountingapp.presenter

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.helper.Results
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<T>(initial: T) : ViewModel() {

    protected open val screenName: String get() = javaClass.simpleName
    protected val log: Timber.Tree
        get() {
            val methodName = Thread.currentThread().stackTrace[3].methodName
            return Timber.tag("${screenName}::${methodName}")
        }

    // Runner
    private var mainJob: Job? = null

    // Data declaration
    protected val _data: MutableStateFlow<T> = MutableStateFlow(initial)
    open val data: StateFlow<T> = _data.asStateFlow()

    protected fun safeRunJob(
        dispatcher: CoroutineDispatcher,
        task: suspend CoroutineScope.() -> Unit,
    ) {
        stopRunningJob()
        mainJob = viewModelScope.launch(dispatcher) {
            task.invoke(this)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun stopRunningJob() = stopRunningJob(mainJob)

    fun stopRunningJob(job: Job?) {
        if (job?.isActive == true) job.cancel()
    }

    @VisibleForTesting
    fun setInitialData(initial: T) {
        _data.value = initial
    }


    // Toast declaration
    protected val _toast: MutableStateFlow<Any?> = MutableStateFlow("")
    open val toast: StateFlow<Any?> = _toast.asStateFlow()
    fun onToastShown() {
        _toast.value = ""
    }


    // Spinner declaration
    protected val _spinner: MutableStateFlow<Boolean> = MutableStateFlow(false)
    open val spinner: StateFlow<Boolean> = _spinner.asStateFlow()

    protected fun safeRunJobWithLoading(
        dispatcher: CoroutineDispatcher,
        task: suspend CoroutineScope.() -> Unit,
    ) {
        createJobWithLoading(dispatcher) {
            safeRunJob(dispatcher, task)
        }
    }

    protected fun createJobWithLoading(
        dispatcher: CoroutineDispatcher,
        block: suspend () -> Unit
    ) = viewModelScope.launch(dispatcher) {
        try {
            _spinner.value = true
            block()
        } catch (error: Throwable) {
            Timber.e(error.message.toString())
            _toast.value = error.message
        } finally {
            _spinner.value = false
        }
    }


    /**
     * Wrapper for Result object to handle success, failure, loading and cancel states
     * @param results Result object
     * @param onSuccessAction action to perform on success
     * @return Any
     * @see Results
     */
    protected suspend fun <D, O> resultWrapper(
        results: Results<D>,
        onSuccessAction: suspend (data: D) -> Results<O>
    ): Results<O> = when (results) {
        is Results.Success -> {
            log.e("Result.Success | data:${results.data}")
            _spinner.value = false
            onSuccessAction(results.data)
        }

        is Results.Failure -> {
            log.e("Result.Failure | type:${results.type} - ${results.throwable}")
            Results.Failure(results.throwable, results.type)
        }

        is Results.Canceled -> {
            log.e("Result.Canceled | ${results.throwable}")
            Results.Canceled(results.throwable)
        }

        is Results.Loading -> {
            log.e("Result.Loading | oldData:${results.oldData}}")
            _spinner.value = true
            Results.Loading()
        }
    }
}