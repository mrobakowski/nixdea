import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.grammarkit.GrammarKitPluginExtension
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.hurricup:gradle-grammar-kit-plugin:2017.1.1")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.1")
    }
}

val CI = System.getenv("CI") != null

plugins {
    idea
    kotlin("jvm") version "1.2.30"
    id("org.jetbrains.intellij") version "0.2.19"
    id("de.undercouch.download") version "3.2.0"
}

apply { plugin("org.junit.platform.gradle.plugin") }

idea {
    module {
        // https://github.com/gradle/kotlin-dsl/issues/537/
        excludeDirs = excludeDirs + file("testData") + file("deps")
    }
}

allprojects {
    apply {
        plugin("idea")
        plugin("kotlin")
        plugin("org.jetbrains.grammarkit")
        plugin("org.jetbrains.intellij")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testCompile("org.junit.jupiter:junit-jupiter-api:5.0.1")
        testRuntime(
                "org.junit.jupiter:junit-jupiter-engine:5.0.1",
                "org.junit.vintage:junit-vintage-engine:4.12.1",
                "org.junit.platform:junit-platform-launcher:1.0.1",
                "org.junit.platform:junit-platform-runner:1.0.1"
        )
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/gen"))
        }
    }

    intellij {
        version = prop("ideaVersion")
        downloadSources = !CI
        updateSinceUntilBuild = true
        instrumentCode = false
        ideaDependencyCachePath = file("deps").absolutePath

        tasks.withType<PatchPluginXmlTask> {
            sinceBuild(prop("sinceBuild"))
            untilBuild(prop("untilBuild"))
        }
    }

    configure<GrammarKitPluginExtension> {
        grammarKitRelease = "1.5.2"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.2"
            apiVersion = "1.2"
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }

    java.sourceSets {
        getByName("main").java.srcDirs("src/gen")
    }
}

project(":") {
    val clionVersion = prop("clionVersion")
    version = "0.2.0.${prop("buildNumber")}"
    intellij {
        pluginName = "nixdea"
        setPlugins("idea.plugin.psiviewer:3.28.93")
    }

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jre8")
    }

    val generateNixParser = task<GenerateParser>("generateNixParser") {
        source = "src/main/grammars/NixParser.bnf"
        targetRoot = "src/gen"
        pathToParser = "/org/nixdea/parser/NixParser.java"
        pathToPsiRoot = "/org/nixdea/psi"
        purgeOldFiles = true
    }

    val generateNixLexer = task<GenerateLexer>("generateNixLexer") {
        source = "src/main/grammars/_NixLexer.flex"
        targetDir = "src/gen/org/nixdea/lexer"
        targetClass = "_NixLexer"
        purgeOldFiles = true
    }

    val downloadClion = task<Download>("downloadClion") {
        onlyIf { !file("${project.projectDir}/deps/clion-$clionVersion.tar.gz").exists() }
        src("https://download.jetbrains.com/cpp/CLion-$clionVersion.tar.gz")
        dest(file("${project.projectDir}/deps/clion-$clionVersion.tar.gz"))
    }

    val unpackClion = task<Copy>("unpackClion") {
        onlyIf { !file("${project.projectDir}/deps/clion-$clionVersion").exists() }
        from(tarTree("deps/clion-$clionVersion.tar.gz"))
        into(file("${project.projectDir}/deps"))
        dependsOn(downloadClion)
    }

    tasks.withType<KotlinCompile> {
        dependsOn(generateNixLexer, generateNixParser, unpackClion)
    }

    tasks.withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    task("resolveDependencies") {
        dependsOn(unpackClion)
        doLast {
            rootProject.allprojects
                    .map { it.configurations }
                    .flatMap { listOf(it.compile, it.testCompile) }
                    .forEach { it.resolve() }
        }
    }
}

fun prop(name: String): String = extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")
