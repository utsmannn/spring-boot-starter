import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	application
}

group = "com.utsman"
version = "0.0.1-SNAPSHOT"
val packageName = "$group.rest"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Jar> {
	manifest {
		attributes["Main-Class"] = "$packageName.RestSimpleApplicationKt"
	}

	exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
	tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.register<Copy>("buildScript") {
	dependsOn("build")
	val jarFile = "$buildDir/libs/${project.name}-${project.version}-plain.jar"
	val intoFile = "$rootDir/dist/libs"
	from(jarFile)
	into(intoFile)

	doLast {
		val file = File("$rootDir/dist", project.name)
		file.writeText("""
            #/usr/bin
            java -jar ${projectDir.absolutePath}/dist/libs/${project.name}-${project.version}-plain.jar ${'$'}@
        """.trimIndent())

		exec {
			commandLine("chmod", "+x", file.absolutePath)
		}
	}

	val scriptLocation = "${projectDir.absolutePath}/dist/${project.name}"
	System.out.println("Script location created on $scriptLocation")
}
