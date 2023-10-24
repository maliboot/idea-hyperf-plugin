fun properties(key: String) = project.findProperty(key).toString()

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.0"
  id("org.jetbrains.intellij") version "1.15.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
  mavenCentral()
}

intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType")) // Target IDE Platform

//  plugins.set(listOf("com.jetbrains.php"))
  plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
  properties("javaVersion").let {
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions.jvmTarget = it
    }
  }

  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))
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
