package com.jetpackduba.gitnuro.git.rebase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.RebaseCommand
import org.eclipse.jgit.api.RebaseResult
import javax.inject.Inject

class RebaseBranchOntoCurrentUseCase @Inject constructor() {
    suspend operator fun invoke(git: Git, branchName: String): RebaseResult.Status = withContext(Dispatchers.IO) {
        val branchRef = git.repository.findRef(branchName)
            ?: throw IllegalArgumentException("Branch '$branchName' not found")

        val originalHead = git.repository.resolve("HEAD")
            ?: throw RuntimeException("HEAD not found")

        git.checkout()
            .setName(branchName)
            .setCreateBranch(false)
            .call()

        val rebaseResult = git.rebase()
            .setOperation(RebaseCommand.Operation.BEGIN)
            .setUpstream(originalHead)
            .call()

        return@withContext rebaseResult.status
    }
}
