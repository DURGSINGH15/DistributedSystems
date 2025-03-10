plugins {
    id("java")
    id("com.google.protobuf") version "0.9.1"
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.grpc:grpc-netty-shaded:1.56.0")
    implementation("io.grpc:grpc-protobuf:1.56.0")
    implementation("io.grpc:grpc-stub:1.56.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.protobuf:protobuf-java:3.21.12")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.12" //specifies the protobuf compiler
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.56.0" //specifies the grpc plugin
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc") //use create instead of id
            }
        }
    }
}


sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc", "build/generated/source/proto/main/java")
        }
    }
}

tasks.register<JavaExec>("runServer") {
    mainClass.set("org.example.GrpcServer")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("runClient") {
    mainClass.set("org.example.GrpcClient")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named<JavaExec>("runClient") {
    standardInput = System.`in`
}

tasks.register<Copy>("copyLibs") {
    from(configurations.runtimeClasspath)
    into("build/libs")
}

tasks.getByName("build").dependsOn("copyLibs")
