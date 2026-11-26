package com.ryu.downloadercat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_history")
data class DownloadRecord(
    @PrimaryKey val id: String,
    val title: String,
    val site: String,
    val kind: String,
    val quality: String,
    val sizeBytes: Long?,
    val dateMillis: Long,
    val uri: String?,
    val filePath: String?,
    val mimeType: String?,
)
