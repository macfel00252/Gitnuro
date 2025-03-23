package com.jetpackduba.gitnuro.git.branches

import com.jetpackduba.gitnuro.extensions.isBranch
import com.jetpackduba.gitnuro.extensions.simpleName
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
        if (hasLocalChanges(git))
            stashChanges(git)

        if (shouldResetLocalBranch(git, branchName, ref))
            checkoutAndResetToRemote(git, branchName, ref)
        else
            createBranchFromRemote(git, branchName, ref)
        if (hasLocalChanges(git))
            applyStash(git)
    }

    private fun hasLocalChanges(git: Git): Boolean {
        return !git.status().call().isClean
    }

    private fun stashChanges(git: Git) {
        println("Wykryto zmiany w drzewie roboczym. Stashowanie przed checkoutem...")
        git.stashCreate().call()
    }

    private fun applyStash(git: Git) {
        println("Odstashowywanie zmian po checkoutowaniu...")
        try {
            git.stashApply().call()
            println("Pomyślnie odstashowano zmiany.")
        } catch (e: Exception) {
            println("Błąd podczas odstashowywania zmian: ${e.message}")
        }
    }

    private fun shouldResetLocalBranch(git: Git, branchName: String, ref: Ref): Boolean {
        return git.repository.refDatabase.refs
            .map { it.name }
            .any { it == "refs/heads/$branchName" && ref.name.startsWith("refs/remotes/") }
    }

    private fun checkoutAndResetToRemote(git: Git, branchName: String, ref: Ref) {
        println("Branch '$branchName' istnieje lokalnie. Wykonano checkout i reset do remote.")
        git.checkout().setName(branchName).call()
        git.reset()
            .setMode(ResetCommand.ResetType.HARD)
            .setRef(ref.name) // Pełna nazwa brancha remote, np. "refs/remotes/origin/branch"
            .call()
    }

    private fun createBranchFromRemote(git: Git, branchName: String, ref: Ref) {
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