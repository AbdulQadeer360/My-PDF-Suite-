package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_files")
data class PdfFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val path: String,
    val size: String,
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isLocked: Boolean = false,
    val password: String? = null,
    val category: String = "All", // "CONVERTED", "EDITED", "COMPRESSED", "SCANNED"
    val pageCount: Int = 1
)
