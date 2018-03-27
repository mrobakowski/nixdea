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
    }
}

val CI = System.getenv("CI") != null

plugins {
    idea
    kotlin("jvm") version "1.2.31"
    id("org.jetbrains.intellij") version "0.2.19"
    id("de.undercouch.download") version "3.2.0"
}

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
    version = "0.0.1.${prop("buildNumber")}"
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

    tasks.withType<KotlinCompile> {
        dependsOn(generateNixLexer, generateNixParser)
    }

    tasks.withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    task("resolveDependencies") {
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
