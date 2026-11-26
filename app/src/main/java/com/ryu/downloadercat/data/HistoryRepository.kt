package com.ryu.downloadercat.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class HistoryRepository(context: Context) {
    private val dao = AppDatabase.get(context).downloadDao()

    val items: Flow<List<DownloadRecord>> = dao.observeAll()

    suspend fun add(record: DownloadRecord) {
        dao.insert(record)
        dao.trim()
    }

    suspend fun delete(id: String) {
        dao.delete(id)
    }
}
