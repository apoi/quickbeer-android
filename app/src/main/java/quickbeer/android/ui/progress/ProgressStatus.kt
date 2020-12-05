package quickbeer.android.ui.progress

enum class Status {
    IDLE,
    INDEFINITE,
    LOADING
}

data class ProgressStatus(val status: Status, val progress: Float) {
    companion object {
        val IDLE = ProgressStatus(Status.IDLE, 0.0f)
        val INDEFINITE = ProgressStatus(Status.INDEFINITE, 0.0f)
    }
}
