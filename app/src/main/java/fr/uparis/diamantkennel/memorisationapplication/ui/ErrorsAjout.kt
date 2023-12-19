package fr.uparis.diamantkennel.memorisationapplication.ui

enum class ErrorsAjout { BAD_ENTRY, DUPLICATE }

class Duplicate : Exception(ErrorsAjout.DUPLICATE.toString())
