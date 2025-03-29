package com.jetpackduba.gitnuro.git.workspace

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.ignore.FastIgnoreRule
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.nio.file.FileSystems
import javax.inject.Inject


class GetIgnoreRulesUseCase @Inject constructor() {
    suspend operator fun invoke(repository: Repository): List<FastIgnoreRule> = withContext(Dispatchers.IO) {
        val ignoreLines = mutableListOf<String>()
        ignoreLines += readExcludeFile(File(repository.directory, ".git/info/exclude"))
        ignoreLines += collectGitignoreRules(repository.workTree)
        ignoreLines += readGlobalExcludes(repository)
        val ignoreRules = ignoreLines.map { FastIgnoreRule(it) }
        return@withContext ignoreRules
    }

    private fun readExcludeFile(file: File): List<String> {
        return if (file.exists() && file.isFile) file.readLines() else emptyList()
    }

    private fun collectGitignoreRules(root: File): List<String> {
        val result = mutableListOf<String>()
        root.walkTopDown()
            .filter { it.name == ".gitignore" && it.isFile }
            .forEach { file ->
                val relativeDir = file.parentFile.relativeTo(root).invariantSeparatorsPath
                val prefix = if (relativeDir.isNotEmpty()) "$relativeDir/" else ""
                val lines = file.readLines().map { line ->
                    val trimmed = line.trim()
                    when {
                        trimmed.isEmpty() || trimmed.startsWith("#") -> trimmed
                        trimmed.startsWith("!") -> {
                            val pattern = trimmed.removePrefix("!")
                            "!$prefix$pattern"
                        }

                        else -> "$prefix$trimmed"
                    }
                }
                result += lines
            }
        return result
    }

    private fun readGlobalExcludes(repository: Repository): List<String> {
        repository.config.load()
        val baseConfig: Config? = repository.config.baseConfig

        val excludesFilePath = baseConfig?.getString("core", null, "excludesFile") ?: return emptyList()
        val expandedPath = if (excludesFilePath.startsWith("~")) {
            excludesFilePath.replace("~", System.getProperty("user.home").orEmpty())
        } else excludesFilePath
        val excludesFile = FileSystems.getDefault().getPath(expandedPath).normalize().toFile();

        return readExcludeFile(excludesFile)
    }
}