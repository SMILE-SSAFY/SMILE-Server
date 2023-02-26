## Multi Module

> 서로 독립적인 프로젝트를 하나의 프로젝트로 묶어 **모듈**로서 사용되는 구조

- 공통적인 기능을 모아 하나의 모듈로 만드는 것이 가능함.

[참고 URL](https://techblog.woowahan.com/2637/)

---

### 사용 이유

- 서로 독립적인 프로젝트로 존재할 때, 시스템의 중심이 가져야할 규칙이나 구조 등을 균일하게 보장해주는 메커니즘이 존재하지 않음.
- 서로 다른 프로젝트 생성 시 같은 파일을 여러 번 생성해야 할 수 있음.
- 개발 과정에서 여러 윈도우를 띄우고 번갈아가면서 확인해야 하는 경우가 생김.

### build.gradle

```groovy
// gradle이 빌드되기 전에 실행되는 설정
buildscript {
    ext {
        queryDslVersion = "5.0.0"
        springBootVersion = '2.7.7'
    }
    repositories {
        maven {url 'https://repo.spring.io/release'}
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.7'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'

    //querydsl 추가
    id 'com.ewerk.gradle.plugins.querydsl' version '1.0.10'
}

group = 'com.ssafy'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

// 현재 root 프로젝트와 앞으로 추가될 서브 모듈에 대한 설정
allprojects {}

// 전체 서브 모듈에 해당되는 설정
subprojects {
    apply plugin: 'java'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

    group = 'com.ssafy'
    version = '1.0'
    sourceCompatibility = '11'
    compileJava.options.encoding = 'UTF-8'

    repositories {
        mavenCentral()

        flatDir {
            dirs 'libs'
        }
    }

    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'org.springframework.boot:spring-boot-starter-security'
        implementation('org.projectlombok:lombok')
        implementation 'io.jsonwebtoken:jjwt:0.9.1'
        implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'
        implementation 'com.vladmihalcea:hibernate-types-52:2.16.2'
        developmentOnly 'org.springframework.boot:spring-boot-devtools'
        runtimeOnly 'com.h2database:h2'
        runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        implementation 'org.springframework.boot:spring-boot-starter-validation'
        implementation 'io.github.bootpay:backend:+'
        implementation("com.mysql:mysql-connector-j:8.0.32")

        // Add querydsl
        implementation 'com.querydsl:querydsl-jpa'
        implementation 'com.querydsl:querydsl-apt'
        annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa"
        annotationProcessor "jakarta.persistence:jakarta.persistence-api"
        annotationProcessor "jakarta.annotation:jakarta.annotation-api"

        // Add nurigo
        implementation 'net.nurigo:sdk:4.1.3'

        //  retrofit2
        implementation 'com.squareup.retrofit2:adapter-rxjava2:2.7.2'

        // smile-clustering
        implementation 'com.github.haifengl:smile-core:3.0.0'

        // redis
        implementation 'org.springframework.boot:spring-boot-starter-data-redis'

        // firebase
        implementation("com.google.firebase:firebase-admin:9.1.1")
        implementation("com.squareup.okhttp3:okhttp:4.10.0")

        // google cloud language
        implementation platform('com.google.cloud:libraries-bom:26.1.4')
        implementation 'com.google.cloud:google-cloud-language'


    }
}

// core-module에 설정
project(":core-module") {
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

  // core-module은 main()이 존재하지 않기 때문에 bootJar 실행 시 실행되는 jar를 만들 수 없음
    bootJar.enabled = false
    jar.enabled = true

    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar'])
    }
}

// api-module에 설정
project(":api-module") {
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'
    apply plugin: 'java-library'

    bootJar {
        archivesBaseName = 'ssafy'
        archiveFileName = "ssafy-api-module-0.0.1.jar"
        archiveVersion = "0.0.1"
    }
    dependencies {
      // api-module에 core-module의 의존성을 추가
        implementation project(':core-module')
        implementation fileTree(dir: 'libs', include: ['*.jar'])
    }
}
```

### Main Class

```java
@EnableJpaAuditing
@ComponentScan({"com.ssafy.core"})
@ComponentScan({"com.ssafy.api"})
@EntityScan("com.ssafy.core")
@EnableJpaRepositories("com.ssafy.core")
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name",
                "application,application-real,application-aws,application-coolsms,application-login,application-pay");
        SpringApplication.run(ApiApplication.class, args);
    }

}
```