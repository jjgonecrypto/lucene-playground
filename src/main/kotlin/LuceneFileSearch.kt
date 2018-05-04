import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import java.io.FileReader
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Paths
import java.util.*

class LuceneFileSearch(private val indexDirectory: Directory, private val analyzer: StandardAnalyzer) {

    @Throws(IOException::class, URISyntaxException::class)
    fun addFileToIndex(filepath: String) {

        val path = Paths.get(javaClass.classLoader.getResource(filepath)!!.toURI())
        val file = path.toFile()
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val indexWriter = IndexWriter(indexDirectory, indexWriterConfig)
        val document = Document()

        val fileReader = FileReader(file)
        document.add(TextField("contents", fileReader))
        document.add(StringField("path", file.path, Field.Store.YES))
        document.add(StringField("filename", file.name, Field.Store.YES))

        indexWriter.addDocument(document)

        indexWriter.close()
    }

    fun searchFiles(inField: String, queryString: String): List<Document>? {
        try {
            val query = QueryParser(inField, analyzer).parse(queryString)

            val indexReader = DirectoryReader.open(indexDirectory)
            val searcher = IndexSearcher(indexReader)
            val topDocs = searcher.search(query, 10)
            val documents = ArrayList<Document>()
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

}
