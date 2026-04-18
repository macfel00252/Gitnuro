package com.jetpackduba.gitnuro.git.branches

import com.jetpackduba.gitnuro.exceptions.BranchAlreadyExistsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import javax.inject.Inject

class CreateBranchUseCase @Inject constructor() {
    suspend operator fun invoke(git: Git, branchName: String): Ref = withContext(Dispatchers.IO) {
        val existingRef = git.repository.findRef(branchName)
        if (existingRef != null) {
            throw BranchAlreadyExistsException(branchName)
        }
        git
            .checkout()
            .setCreateBranch(true)
            .setName(branchName)
            .call()
    }
}
