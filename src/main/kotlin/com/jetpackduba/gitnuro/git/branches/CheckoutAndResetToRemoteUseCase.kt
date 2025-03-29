package com.jetpackduba.gitnuro.git.branches

import com.jetpackduba.gitnuro.extensions.simpleName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Ref
import javax.inject.Inject

class CheckoutAndResetToRemoteUseCase @Inject constructor() {
    suspend operator fun invoke(git: Git, ref: Ref): Unit = withContext(Dispatchers.IO) {
        val branchName = ref.simpleName
        println("Branch '$branchName' istnieje lokalnie. Wykonano checkout i reset do remote.")
        git.checkout().setName(branchName).call()
        git.reset()
            .setMode(ResetCommand.ResetType.HARD)
            .setRef(ref.name)
            .call()
    }
}
