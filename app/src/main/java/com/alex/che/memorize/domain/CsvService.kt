package com.alex.che.memorize.domain

import android.content.ContentResolver
import android.content.ContextWrapper
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.dto.WordCsvDto
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CsvService : KoinComponent {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private val wordConverter: WordConverter by inject()

    fun import(url: Uri, dicitionaryId: Int, context: ContextWrapper) {
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

    fun export(dictionaryId: Int, name: String) {
        val folder = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(folder, "$name.csv")
        FileOutputStream(file).writeCsv(dictionaryId)
    }

    private fun OutputStream.writeCsv(dictionaryId: Int) {
        val words = memorizeDatabase.wordDao.loadWordsByDictId(dictionaryId)
        val writer = bufferedWriter()
        words?.forEach {
            writer.write("${it.word}| ${it.translation}")
            writer.newLine()
        }
        writer.flush()
    }
}