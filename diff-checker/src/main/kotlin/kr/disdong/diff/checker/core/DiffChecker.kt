package kr.disdong.diff.checker.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.google.gson.JsonParser.parseReader
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.tools.ant.DirectoryScanner
import java.io.FileReader

// fun <T> println(msg: T) {
//     kotlin.io.println("[${Thread.currentThread().name}] $msg")
// }

class DiffChecker(
    private val baseDir: String,
    private val targetDir: String,
    private val targetFile: String,
) {

    /**
     * baseDir/**/targetDir 에 해당하는 모든 파일을 비교합니다.
     * 디렉토리 내의 파일은 정렬된 순서대로 2개씩 비교됩니다.
     * promise.all 처럼 동작합니다.
     */
    fun run() = runBlocking {
        println(baseDir)
        val directories = getDirectories()
        val dirDefferedResults = mutableListOf<Deferred<Unit>>()

        directories.map { dir ->
            val defferedResult = async(Dispatchers.Default) {
                printDiffInformation(dir)
            }

            dirDefferedResults.add(defferedResult)
        }

        dirDefferedResults.awaitAll()
    }

    private fun printDiffInformation(dir: String) = runBlocking {
        val files = getFiles(dir)
        val errors = mutableListOf<JsonNode>()
        val errorFileNames = mutableListOf<String>()
        val fileDefferedResults = mutableListOf<Deferred<Unit>>()

        for (i in 0..files.size step 2) {
            if (i + 1 >= files.size) {
                break
            }

            val defferedResult = async(Dispatchers.Default) {
                // delay(i * 1000L)
                val response = compareFiles(
                    "$baseDir/${dir}/${files[i]}",
                    "$baseDir/${dir}/${files[i+1]}"
                )

                if (response != null) {
                    errors.add(response.error)
                    errorFileNames.addAll(response.errorFilePath)
                }
            }

            fileDefferedResults.add(defferedResult)
        }

        fileDefferedResults.awaitAll()

        if (errors.isEmpty()) {
            println("$dir 의 모든 결과는 동일합니다.")
        } else {
            println("$dir 에 결과가 동일하지 않은 응답이 존재합니다. $errorFileNames")
        }
    }

    /**
     * 모든 targetDir 을 가져옵니다.
     * @return
     */
    private fun getDirectories(): Array<String> {
        val dirScanner = DirectoryScanner()

        dirScanner.setBasedir(baseDir)
        dirScanner.setIncludes(arrayOf(targetDir))
        dirScanner.isCaseSensitive = false
        dirScanner.scan()

        return dirScanner.includedDirectories
    }

    /**
     * 모든 targetFile 을 가져옵니다.
     * @param dir
     * @return
     */
    private fun getFiles(dir: String): Array<String> {
        val fileScanner = DirectoryScanner()

        fileScanner.setIncludes(arrayOf(targetFile))
        fileScanner.setBasedir(dir)
        fileScanner.isCaseSensitive = false

        fileScanner.scan()

        return fileScanner.includedFiles
    }

    /**
     * 두 파일을 비교하여 다른 부분이 있으면 반환합니다.
     * @param filePath1
     * @param filePath2
     * @return
     */
    private fun compareFiles(filePath1: String, filePath2: String): DiffResponse? {
        val tree1 = jacksonObjectMapper()
            .readTree(
                parseReader(FileReader(filePath1))
                    .toString()
            )
        val tree2 = jacksonObjectMapper()
            .readTree(
                parseReader(FileReader(filePath2))
                    .toString()
            )

        val response = JsonDiff.asJson(tree1, tree2)

        if (response.size() != 0) {
            return DiffResponse(response, listOf(filePath1, filePath2))
        }

        return null
    }
}

class DiffResponse(
    val error: JsonNode,
    val errorFilePath: List<String>
)