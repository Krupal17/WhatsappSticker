package com.kp.bright.whatsapptickers.tabulas.detecttable

//import com.tom_roush.pdfbox.text.TextPosition
//
//fun detectTableBoundaries(textPositions: List<TextPosition>): RectF {
//    val xCoordinates = textPositions.map { it.xDirAdj }.sorted()
//    val yCoordinates = textPositions.map { it.yDirAdj }.sorted()
//
//    val minX = xCoordinates.first()
//    val maxX = xCoordinates.last()
//    val minY = yCoordinates.first()
//    val maxY = yCoordinates.last()
//
//    return RectF(minX, minY, maxX, maxY)
//}

//fun Activity.extractTableSaveCSV(pdfFile:File) {
//
//    // Open document
//    val inputStream: InputStream = pdfFile.inputStream()
//    val document = Document(inputStream)
//
//    val file: File = File(cacheDir, "PDFToXLS_out.csv")
//    // Instantiate ExcelSave Option object
//    val excelSave: ExcelSaveOptions = ExcelSaveOptions()
//    excelSave.format = ExcelSaveOptions.ExcelFormat.CSV
//    try {
//        document.save(file.toString(), excelSave)
//    } catch (e: Exception) {
//        Log.e("PDF_OPEN", "extractTableSaveCSV2: Exception: ${e.message}", )
//        return
//    }
//}
