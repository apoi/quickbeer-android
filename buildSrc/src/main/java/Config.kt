import org.gradle.api.Project
import java.io.ByteArrayOutputStream

object App {
    const val AppId = "quickbeer.android.v4"

    object Version {
        internal const val Major = 1
        internal const val Minor = 0
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

fun Project.appVersionName(): String {
    return "${App.Version.Major}.${App.Version.Minor}.${appBuildVersion()}"
}
