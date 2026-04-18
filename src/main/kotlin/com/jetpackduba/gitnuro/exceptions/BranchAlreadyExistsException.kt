package com.jetpackduba.gitnuro.exceptions

class BranchAlreadyExistsException(branchName: String) : GitnuroException("Branch '$branchName' already exists") {
    val branchName = branchName
}
