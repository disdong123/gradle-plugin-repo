# gradle plugin repo

custom plugin 을 위한 multi module 입니다. 

https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry


### 준비
Github Personal access token 을 발급받고, github username 과 함께 환경변수(.zshrc 등)로 저장해야합니다.

### 사용법

settings.gradle 파일에 아래와 같이 작성합니다.

```kotlin
pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/disdong123/gradle-plugin-repo")
            credentials {
                username = System.getenv("DISDONG_USERNAME")
                password = System.getenv("DISDONG_TOKEN")
            }
        }
        // maven {
        // 여러 repository 작성 가능    
        // }
        
        gradlePluginPortal()
    }
}
```

build.gradle.kts 파일에 아래와 같이 작성합니다.

```kotlin
plugins {
    id("kr.disdong.diff.checker") version "0.0.12"    
}
```

### 배포
```
./gradlew publish
```


### TODO
- test
- workflows