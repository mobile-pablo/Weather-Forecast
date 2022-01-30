package com.company.elverano.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class ResultEvent : Parcelable {
    @Parcelize
    object Success : ResultEvent()
    @Parcelize
    data class Error(var message: String) : ResultEvent()
}