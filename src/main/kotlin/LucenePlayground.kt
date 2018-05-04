import com.mongodb.async.client.MongoClients
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.RAMDirectory
import java.util.concurrent.CountDownLatch

const val MAX_RESULTS = 100

fun main(args: Array<String>) {

    val inMemoryLuceneIndex = InMemoryLuceneIndex(RAMDirectory(), StandardAnalyzer(CharArraySet.EMPTY_SET))

    val latch = CountDownLatch(1)

    val mongoClient = MongoClients.create("mongodb://localhost:27017")

    val database = mongoClient.getDatabase("test")

    // https://raw.githubusercontent.com/ozlerhakan/mongodb-json-files/master/datasets/city_inspections.json

    database.getCollection("inspections").find().limit(MAX_RESULTS).forEach({ doc ->
        val fieldsToIndex = doc.keys.mapNotNull { field ->
            run {
                try {
                    Pair(field, doc.getString(field))
                } catch (_: Exception) {
                    null
                }
            }
        }.toMap()

        inMemoryLuceneIndex.indexDocument(fieldsToIndex)
    }, { _, err ->
        run {
            if (err != null) throw err

            do {
                print("Lucene Query: ")
                val input = readLine()!!

                if (input == "") break

                //result:(+Voilaton~ -No)
                val results = inMemoryLuceneIndex.searchIndex("", input, MAX_RESULTS)
                results?.forEach {
                    println("${it.get("business_name")}\t\t\t${it.get("result")}")
                }
                println("Found ${results?.size} documents (max $MAX_RESULTS)")
            } while (true)

            latch.countDown()
        }
    })

    latch.await()
}
