package com.ryu.downloadercat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_history ORDER BY dateMillis DESC LIMIT 500")
    fun observeAll(): Flow<List<DownloadRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DownloadRecord)

    @Query("DELETE FROM download_history WHERE id = :id")
    suspend fun delete(id: String)

    @Query(
        "DELETE FROM download_history WHERE id NOT IN " +
            "(SELECT id FROM download_history ORDER BY dateMillis DESC LIMIT 500)"
    )
    suspend fun trim()
}
