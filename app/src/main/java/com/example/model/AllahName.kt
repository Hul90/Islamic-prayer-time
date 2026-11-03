package com.example.model

/**
 * Represents one of the 99 Beautiful Names of Allah (Asmaul Husna).
 */
data class AllahName(
    val id: Int,
    val arabic: String,
    val transliterationBn: String,
    val transliterationEn: String,
    val meaningBn: String,
    val meaningEn: String,
    val fazilatBn: String,
    val category: String = "সাধারণ"
)
