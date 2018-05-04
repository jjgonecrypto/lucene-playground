import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.FSDirectory
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Paths

class LuceneFileSearchTest {

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun givenSearchQueryWhenFetchedFileNamehenCorrect() {
        val indexPath = "index"
        val dataPath = "data/file1.txt"

        val directory = FSDirectory.open(Paths.get(indexPath))
        val luceneFileSearch = LuceneFileSearch(directory, StandardAnalyzer())

        luceneFileSearch.addFileToIndex(dataPath)

        val docs = luceneFileSearch.searchFiles("contents", "consectetur")

        Assert.assertEquals("file1.txt", docs!![0].get("filename"))
    }

}