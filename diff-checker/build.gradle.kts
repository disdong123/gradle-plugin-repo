plugins {
    `java-gradle-plugin`
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jackson.module.kotlin)

    // https://mvnrepository.com/artifact/org.apache.ant/ant
    implementation("org.apache.ant:ant:1.10.13")
    // https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
    implementation("com.flipkart.zjsonpatch:zjsonpatch:0.4.14")
    // string to json
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.kotlinx.coroutines.test)
}

gradlePlugin {
    // Define the plugin
    plugins {
        create("diffChecker") {
            id = "kr.disdong.diff.checker"
            group = "kr.disdong"
            version = "0.0.1"
            implementationClass = "kr.disdong.diff.checker.DiffCheckerPlugin"
        }
    }
}


// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}
