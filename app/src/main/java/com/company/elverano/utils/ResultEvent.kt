package com.company.elverano.utils

sealed class ResultEvent {
    object Success : ResultEvent()
    data class Error(var message: String) : ResultEvent()
}