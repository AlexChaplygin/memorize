package com.alex.che.memorize.domain

import android.content.ContentResolver
import android.content.ContextWrapper
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.dto.WordCsvDto
import com.alex.che.memorize.repository.MemorizeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Single
class CsvService(
    private val memorizeDatabase: MemorizeDatabase,
    private val wordConverter: WordConverter
) {

    suspend fun import(url: Uri, dicitionaryId: Int, context: ContextWrapper) = withContext(Dispatchers.IO) {
        readCsv(url, context).map {
            wordConverter.convert(it).also {
                it.dictionaryId = dicitionaryId
            }
        }.forEach { memorizeDatabase.wordDao.insertWord(it) }
    }

    private fun readCsv(url: Uri, context: ContextWrapper): List<WordCsvDto> {
        val resolver: ContentResolver = context.contentResolver
        val reader = resolver.openInputStream(url)
            ?.use { stream -> stream.readBytes().toString(Charsets.UTF_8) }
            ?: throw IllegalStateException("could not open $url")

        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (word, translation) = it.split('|', ignoreCase = false, limit = 2)
                WordCsvDto(word.trim(), translation.trim())
            }.toList()
    }

    suspend fun export(dictionaryId: Int, name: String) = withContext(Dispatchers.IO) {
        val folder = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(folder, "$name.csv")
        FileOutputStream(file).writeCsv(dictionaryId)
    }

    private suspend fun OutputStream.writeCsv(dictionaryId: Int) {
        val words = memorizeDatabase.wordDao.loadWordsByDictId(dictionaryId)
        val writer = bufferedWriter()
        words?.forEach {
            writer.write("${it.word}| ${it.translation}")
            writer.newLine()
        }
        writer.flush()
    }
}