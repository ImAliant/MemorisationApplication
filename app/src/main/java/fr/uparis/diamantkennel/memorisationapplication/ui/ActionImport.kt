package fr.uparis.diamantkennel.memorisationapplication.ui

enum class ActionImport {
    FILE, INTERNET;

    override fun toString() = when (this) {
        FILE -> "Locale"
        INTERNET -> "Internet"
    }

}
