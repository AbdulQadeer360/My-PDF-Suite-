package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfFileDao {
    @Query("SELECT * FROM pdf_files ORDER BY dateAdded DESC")
    fun getAllFiles(): Flow<List<PdfFile>>

    @Query("SELECT * FROM pdf_files WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteFiles(): Flow<List<PdfFile>>

    @Query("SELECT * FROM pdf_files WHERE id = :id")
    suspend fun getFileById(id: Int): PdfFile?

    @Query("SELECT * FROM pdf_files WHERE name LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchFiles(query: String): Flow<List<PdfFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: PdfFile): Long

    @Update
    suspend fun updateFile(file: PdfFile)

    @Delete
    suspend fun deleteFile(file: PdfFile)

    @Query("DELETE FROM pdf_files WHERE id = :id")
    suspend fun deleteFileById(id: Int)

    @Query("DELETE FROM pdf_files")
    suspend fun deleteAllFiles()
}
