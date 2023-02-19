package kr.disdong.diff.checker.core

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
internal class DiffCheckerTest {

    val baseDir = "/Users/gksrlf/Desktop/spring/diff-checker/diff-checker"
    val targetDir = "**/diffcheckertest"
    val targetFile = "**/*.json"
    @Test
    fun `기능 테스트`() {

        DiffChecker(baseDir, targetDir, targetFile).run()
    }
}