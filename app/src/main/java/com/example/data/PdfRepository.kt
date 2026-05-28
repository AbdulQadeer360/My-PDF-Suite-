package com.example.data

import kotlinx.coroutines.flow.Flow

class PdfRepository(private val pdfFileDao: PdfFileDao) {
    val allFiles: Flow<List<PdfFile>> = pdfFileDao.getAllFiles()
    val favoriteFiles: Flow<List<PdfFile>> = pdfFileDao.getFavoriteFiles()

    fun searchFiles(query: String): Flow<List<PdfFile>> {
        return pdfFileDao.searchFiles(query)
    }

    suspend fun getFileById(id: Int): PdfFile? {
        return pdfFileDao.getFileById(id)
    }

    suspend fun insertFile(file: PdfFile): Long {
        return pdfFileDao.insertFile(file)
    }

    suspend fun updateFile(file: PdfFile) {
        pdfFileDao.updateFile(file)
    }

    suspend fun deleteFile(file: PdfFile) {
        pdfFileDao.deleteFile(file)
    }

    suspend fun deleteFileById(id: Int) {
        pdfFileDao.deleteFileById(id)
    }

    suspend fun deleteAllFiles() {
        pdfFileDao.deleteAllFiles()
    }
}
