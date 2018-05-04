import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.SortedDocValuesField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.Sort
import org.apache.lucene.store.Directory
import org.apache.lucene.util.BytesRef
import java.io.IOException
import java.util.*


class InMemoryLuceneIndex(private val memoryIndex: Directory, private val analyzer: StandardAnalyzer) {

    fun indexDocument(title: String, body: String) {
        val indexWriterConfig = IndexWriterConfig(analyzer)
        try {
            val writter = IndexWriter(memoryIndex, indexWriterConfig)
            val document = Document()

            document.add(TextField("title", title, Field.Store.YES))
            document.add(TextField("body", body, Field.Store.YES))
            document.add(SortedDocValuesField("title", BytesRef(title)))

            writter.addDocument(document)
            writter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun indexDocument(contents: Map<String, String>) {
        val indexWriterConfig = IndexWriterConfig(analyzer)
        if (contents.isEmpty()) return
        try {
            val writer = IndexWriter(memoryIndex, indexWriterConfig)
            val document = Document()

            contents.forEach { key, value -> document.add(TextField(key, value, Field.Store.YES)) }
            println("Added ${contents} to index")
            writer.addDocument(document)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun searchIndex(inField: String, queryString: String, results: Int = 10): List<Document>? {
        try {
            val query = QueryParser(inField, analyzer).parse(queryString)

            val indexReader = DirectoryReader.open(memoryIndex)
            val searcher = IndexSearcher(indexReader)
            val topDocs = searcher.search(query, results)
            val documents = ArrayList<Document>()
//            println("explain ${searcher.explain(query, 0)}")

            for (scoreDoc in topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc))
            }

            return documents
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun searchIndex(query: Query): List<Document>? {
        try {
            val indexReader = DirectoryReader.open(memoryIndex)
            val searcher = IndexSearcher(indexReader)
            val topDocs = searcher.search(query, 10)
            val documents = ArrayList<Document>()
            for (scoreDoc in topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc))
            }

            return documents
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null

    }

    fun searchIndex(query: Query, sort: Sort): List<Document>? {
        try {
            val indexReader = DirectoryReader.open(memoryIndex)
            val searcher = IndexSearcher(indexReader)
            val topDocs = searcher.search(query, 10, sort)
            val documents = ArrayList<Document>()
            for (scoreDoc in topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc))
            }

            return documents
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null

    }


    fun deleteDocument(term: Term) {
        try {
            val indexWriterConfig = IndexWriterConfig(analyzer)
            val writter = IndexWriter(memoryIndex, indexWriterConfig)
            writter.deleteDocuments(term)
            writter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}