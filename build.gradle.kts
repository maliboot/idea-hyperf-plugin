plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.20"
  id("org.jetbrains.intellij") version "1.16.0"
}

group = "io.maliboot.devkit.idea"
version = "1.0.0-rc.4"

repositories {
  mavenCentral()
}

intellij {
  pluginName.set("Hyperf Support")
  version.set("2023.1")
  type.set("PS")

  plugins.set(listOf("com.jetbrains.php"))
}

tasks {

  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }

  patchPluginXml {
    sinceBuild.set("231")
    untilBuild.set("233.*")
    changeNotes.set(file("src/main/resources/META-INF/change-notes.html").readText().replace("<html>", "").replace("</html>", ""))
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
