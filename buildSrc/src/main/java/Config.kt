import org.gradle.api.Project
import java.io.ByteArrayOutputStream

object App {
    const val AppId = "quickbeer.android"

    object Version {
        internal const val Major = 4
        internal const val Minor = 2
    }
}

fun Project.appBuildVersion(): Int {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine = listOf("git", "rev-list", "--first-parent", "--count", "master")
            standardOutput = stdout
        }
        stdout.toString().trim().toInt()
    } catch (_: Throwable) {
        0
    }
}

fun Project.appVersionCode(): Int {
    // Fixed increase to pass v3 versioning
    return appBuildVersion() + 1000
}

fun Project.appVersionName(): String {
    return "${App.Version.Major}.${App.Version.Minor}.${appBuildVersion()}"
}
