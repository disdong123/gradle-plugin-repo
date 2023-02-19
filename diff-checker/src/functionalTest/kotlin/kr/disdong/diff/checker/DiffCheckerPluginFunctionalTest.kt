package kr.disdong.diff.checker

import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import kotlin.test.assertEquals

class DiffCheckerPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    @Test
    fun `can run task`() {
        settingsFile.writeText("rootProject.name = \"functional-test\"")
        buildFile.writeText(
            """
            plugins {
                id("kr.disdong.diff.checker")
            }
            
            diffChecker {
                baseDir.set(project.projectDir.toString())
                targetDir.set("**/diffcheckertest")
                targetFile.set("**/*.json")
            }
            """
                .trimIndent())

        // Run the build
        val runner = GradleRunner.create()
            .withDebug(true)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("diffChecker")
            .withProjectDir(projectDir)
            .build()

        assertEquals(runner.task(":diffChecker")!!.outcome, TaskOutcome.SUCCESS)
    }
}
