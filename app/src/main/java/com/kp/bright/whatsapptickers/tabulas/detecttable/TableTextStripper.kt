package com.kp.bright.whatsapptickers.tabulas.detecttable

import android.app.Activity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//
//import android.app.Activity
//import android.graphics.Rect
//import android.util.Log
//import com.tom_roush.pdfbox.pdmodel.PDDocument
//import com.tom_roush.pdfbox.text.PDFTextStripper
//import com.tom_roush.pdfbox.text.PDFTextStripperByArea
//import com.tom_roush.pdfbox.text.TextPosition
//import java.io.File
//import java.io.FileWriter
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//
//class TableTextStripper : PDFTextStripper() {
//    val textPositions: MutableList<TextPosition> = mutableListOf()
//
//    override fun processTextPosition(text: TextPosition) {
//        textPositions.add(text)
//        super.processTextPosition(text)
//    }
//}
//
//
//@Throws(IOException::class)
//fun findTableAreas(file: File): List<Rect> {
//    val tableAreas: MutableList<Rect> = ArrayList<Rect>()
//
//    val document = PDDocument.load(file)
//    val page = document.getPage(0) // Assuming table is on the first page
//
//    val pdfStripper: PDFTextStripper = object : PDFTextStripper() {
//        @Throws(IOException::class)
//        override fun writeString(text: String, textPositions: List<TextPosition>) {
//            for (textPosition in textPositions) {
//                // Example logic to detect potential table areas based on text position
//                if (textPosition.unicode.matches("\\d+".toRegex())) { // Simple heuristic to detect table content
//                    val x = textPosition.xDirAdj.toInt()
//                    val y = textPosition.yDirAdj.toInt()
//                    val width = textPosition.widthDirAdj.toInt()
//                    val height = textPosition.heightDir.toInt()
//
//                    val area = Rect(x, y, width, height)
//                    tableAreas.add(area)
//                }
//            }
//            super.writeString(text, textPositions)
//        }
//    }
//
//    pdfStripper.sortByPosition = true
//    pdfStripper.getText(document) // Trigger text extraction
//
//    document.close()
//
//    // Process tableAreas to refine and filter out irrelevant areas
//    // Implement your logic to identify and merge areas based on table patterns
//    return tableAreas
//}
fun Activity.saveToJsonFile(json: String) {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())
    val cacheDir = File(cacheDir, "jsons")
    if (!cacheDir.exists()) cacheDir.mkdirs()
    val jsonFile = File(cacheDir, "${timestamp}_statement.json")

    FileWriter(jsonFile).use { writer ->
        writer.write(json)
    }
}
//fun extractTextPositions(file: File): List<List<String>> {
//    val table: MutableList<List<String>> = ArrayList()
//
//    val areaStripper = PDFTextStripperByArea()
//
//    PDDocument.load(file).use { document ->
//        val stripper = TableTextStripper()
//        stripper.getText(document)
//
//        var rect = detectTableBoundaries(stripper.textPositions)
//        areaStripper.addRegion("table", rect)
//        areaStripper.extractRegions(document.getPage(0))
//
//        val tableText = areaStripper.getTextForRegion("table")
//
//        // Use the extracted text to locate and parse the table
//        table.addAll(parseTable(tableText))
//        return  table
//    }
//}
//fun parseTable(text: String): List<List<String>> {
//    val table: MutableList<List<String>> = ArrayList()
//    val lines = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
//        .toTypedArray()
//
//    var headersExtracted = false
//    val headers: MutableList<String> = ArrayList()
//    for (line in lines) {
//        if (!headersExtracted) {
//            headers.addAll(parseLine(line))
//            headersExtracted = true
//        } else {
//            val row = parseLine(line)
//            if (!row.isEmpty()) {
//                table.add(row)
//            }
//        }
//    }
//
//    return table
//}
//
//private fun parseLine(line: String): List<String> {
//    val columns = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//    val row: MutableList<String> = ArrayList()
//
//    Log.e("TAG--", "parseLine:${line} ", )
//    for (column in columns) {
//        row.add(column.trim { it <= ' ' })
//    }
//    return row
//}