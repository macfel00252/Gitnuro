package com.jetpackduba.gitnuro.git.branches

import com.jetpackduba.gitnuro.extensions.isBranch
import com.jetpackduba.gitnuro.extensions.simpleName
import com.jetpackduba.gitnuro.logging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Ref
import javax.inject.Inject

class CheckoutRefUseCase @Inject constructor() {
    suspend operator fun invoke(git: Git, ref: Ref): Unit = withContext(Dispatchers.IO) {
        val branchName = ref.simpleName
        println("Tworzenie nowego brancha '$branchName' na podstawie remote...")
        git.checkout().apply {
            setName(ref.name)
            if (ref.isBranch && ref.name.startsWith("refs/remotes/")) {
                setCreateBranch(true)
                setName(branchName)
                setStartPoint(ref.name)
                setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
            }
            call()
        }
        println("Branch '$branchName' utworzony i wykonano checkout.")
    }
}