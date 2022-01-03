package app.git

import app.app.Error
import app.app.newErrorNow
import app.di.TabScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.eclipse.jgit.api.Git
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@TabScope
class TabState @Inject constructor() {
    var git: Git? = null
    val safeGit: Git
        get() {
            val git = this.git
            if (git == null) {
//                _repositorySelectionStatus.value = RepositorySelectionStatus.None
                throw CancellationException("Null git object")
            } else
                return git
        }

    val mutex = Mutex()

    private val _refreshData = MutableSharedFlow<RefreshType>()
    val refreshData: Flow<RefreshType> = _refreshData
    suspend fun refreshData(refreshType: RefreshType) = _refreshData.emit(refreshType)

    private val _errors = MutableSharedFlow<Error>()
    val errors: Flow<Error> = _errors
    val managerScope = CoroutineScope(SupervisorJob())


    /**
     * Property that indicates if a git operation is running
     */
    @set:Synchronized
    var operationRunning = false


    private val _processing = MutableStateFlow(false)
    val processing: StateFlow<Boolean>
        get() = _processing

    fun safeProcessing(showError: Boolean = true, callback: suspend (git: Git) -> RefreshType) =
        managerScope.launch {
            mutex.withLock {
                _processing.value = true
                operationRunning = true

                try {
                    val refreshType = callback(safeGit)

                    if (refreshType != RefreshType.NONE)
                        _refreshData.emit(refreshType)
                } catch (ex: Exception) {
                    ex.printStackTrace()

                    if (showError)
                        _errors.emit(newErrorNow(ex, ex.localizedMessage))
                } finally {
                    _processing.value = false
                    operationRunning = false
                }
            }
        }

    fun runOperation(block: suspend (git: Git) -> RefreshType) = managerScope.launch {
        operationRunning = true
        try {
            val refreshType = block(safeGit)

            if (refreshType != RefreshType.NONE)
                _refreshData.emit(refreshType)
        } finally {
            operationRunning = false
        }
    }
}

enum class RefreshType {
    NONE,
    ALL_DATA,
    ONLY_LOG,

    /**
     * Requires to update the status if currently selected and update the log if there has been a change
     * in the "uncommited changes" state (if there were changes before but not anymore and vice-versa)
     */
    UNCOMMITED_CHANGES,
}