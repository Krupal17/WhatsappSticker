package com.kp.bright.whatsapptickers.tabulas

data class Record(val data: Map<String, String>)

//class PDFTableExtractor(private val file: File) {
//
//    fun extractText():String{
//        val document = PDDocument.load(file)
//        val stripper = PDFTextStripper()
//        val text = stripper.getText(document)
//        document.close()
//        return text
//    }
//
//    fun extractTable(): List<Record> {
//        val document = PDDocument.load(file)
//        val stripper = PDFTextStripper()
//        val text = stripper.getText(document)
//        document.close()
//
//        val lines = text.split("\n")
//        if (lines.isEmpty()) return emptyList()
//
//        val header = lines.first().split("\\s+".toRegex())
//        val records = mutableListOf<Record>()
//
//        lines.drop(1).forEach { line ->
//            val values = line.split("\\s+".toRegex())
//            val recordMap = mutableMapOf<String, String>()
//            header.forEachIndexed { index, key ->
//                recordMap[key] = values.getOrNull(index) ?: ""
//            }
//            records.add(Record(recordMap))
//        }
//
//        return records
//    }
//
//    fun saveToJsonFile(json: String) {
//        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//        val timestamp = dateFormat.format(Date())
//        val cacheDir = File(file.parentFile, "cache")
//        if (!cacheDir.exists()) cacheDir.mkdirs()
//        val jsonFile = File(cacheDir, "${timestamp}_statement.json")
//
//        FileWriter(jsonFile).use { writer ->
//            writer.write(json)
//        }
//    }
//
//    fun convertToJSON(records: List<Record>): String {
//        val gson = Gson()
//        return gson.toJson(records)
//    }
//}
