import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.BytesRef
import org.junit.Assert
import org.junit.Test

class LuceneInMemorySearchTest {

    @Test
    fun givenSearchQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("Hello world", "Some hello world ")

        val documents = inMemoryLuceneIndex.searchIndex("body", "world")

        Assert.assertEquals("Hello world", documents!![0].get("title"))
        Assert.assertEquals(1, documents!!.size)
    }

    @Test
    fun givenSearchQueryWithoutField() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("Hello world", "Some hello world ")

        val documents = inMemoryLuceneIndex.searchIndex("", "body:worl title:Hello")

        Assert.assertEquals("Hello world", documents!![0].get("title"))
        Assert.assertEquals(1, documents!!.size)
    }

    @Test
    fun givenTermQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("activity", "running in track")
        inMemoryLuceneIndex.indexDocument("activity", "Cars are running on road")

        val term = Term("body", "running")
        val query = TermQuery(term)

        val documents = inMemoryLuceneIndex.searchIndex(query)
        Assert.assertEquals(2, documents?.size)
    }

    @Test
    fun givenPrefixQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("article", "Lucene introduction")
        inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene")

        val term = Term("body", "intro")
        val query = PrefixQuery(term)

        val documents = inMemoryLuceneIndex.searchIndex(query)
        Assert.assertEquals(2, documents?.size)
    }

    @Test
    fun givenBooleanQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("Destination", "Las Vegas singapore car")
        inMemoryLuceneIndex.indexDocument("Commutes in singapore", "Bus Car Bikes")

        val term1 = Term("body", "singapore")
        val term2 = Term("body", "car")

        val query1 = TermQuery(term1)
        val query2 = TermQuery(term2)

        val booleanQuery = BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
                .add(query2, BooleanClause.Occur.MUST).build()

        val documents = inMemoryLuceneIndex.searchIndex(booleanQuery)
        Assert.assertEquals(1, documents?.size)
    }

    @Test
    fun givenPhraseQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("quotes", "A rose by any other name would smell as sweet.")

        val query = PhraseQuery(1, "body", BytesRef("smell"), BytesRef("sweet"))
        val documents = inMemoryLuceneIndex.searchIndex(query)

        Assert.assertEquals(1, documents?.size)
    }

    @Test
    fun givenFuzzyQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("article", "Halloween Festival")
        inMemoryLuceneIndex.indexDocument("decoration", "Decorations for Halloween")

        val term = Term("body", "hallowen")
        val query = FuzzyQuery(term)

        val documents = inMemoryLuceneIndex.searchIndex(query)
        Assert.assertEquals(2, documents?.size)
    }

    @Test
    fun givenWildCardQueryWhenFetchedDocumentThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("article", "Lucene introduction")
        inMemoryLuceneIndex.indexDocument("article", "Introducing Lucene with Spring")

        val term = Term("body", "intro*")
        val query = WildcardQuery(term)

        val documents = inMemoryLuceneIndex.searchIndex(query)
        Assert.assertEquals(2, documents?.size)
    }

    @Test
    fun givenSortFieldWhenSortedThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India")
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia")
        inMemoryLuceneIndex.indexDocument("Amazon", "Rain forest river")
        inMemoryLuceneIndex.indexDocument("Rhine", "Belongs to Europe")
        inMemoryLuceneIndex.indexDocument("Nile", "Longest River")

        val term = Term("body", "river")
        val query = WildcardQuery(term)

        val sortField = SortField("title", SortField.Type.STRING_VAL, false)
        val sortByTitle = Sort(sortField)

        val documents = inMemoryLuceneIndex.searchIndex(query, sortByTitle)
        Assert.assertEquals(4, documents?.size)
        Assert.assertEquals("Amazon", documents?.get(0)?.getField("title")?.stringValue())
    }

    @Test
    fun whenDocumentDeletedThenCorrect() {
        val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer())
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India")
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia")

        val term = Term("title", "ganges")
        inMemoryLuceneIndex.deleteDocument(term)

        val query = TermQuery(term)

        val documents = inMemoryLuceneIndex.searchIndex(query)
        Assert.assertEquals(0, documents?.size)
    }

}