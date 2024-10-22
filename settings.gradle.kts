pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "ai-chat-android"
include(":app")
include(":core:model")
include(":core:designsystem")
include(":core:network")
include(":core:navigation")
include(":core:data")
include(":feature:channels")
include(":feature:messages")
include(":baselineprofile")
