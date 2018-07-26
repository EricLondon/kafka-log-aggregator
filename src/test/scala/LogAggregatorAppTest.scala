import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.TopologyTestDriver
import org.scalatest._
import scala.collection.JavaConversions._

class LogAggregatorAppTest extends FunSpec with Matchers with GivenWhenThen {

  def jsonLogs(): collection.mutable.ListBuffer[String] = {
    val logs = new collection.mutable.ListBuffer[String]()

    val jsonString1 = """{"code":200,"message":"OK"}"""
    val jsonString2 = """{"code":301,"message":"Moved Permanently"}"""
    val jsonString3 = """{"code":302,"message":"Found"}"""
    val jsonString4 = """{"code":304,"message":"Not Modified"}"""
    val jsonString5 = """{"code":400,"message":"Bad Request"}"""
    val jsonString6 = """{"code":401,"message":"Unauthorized"}"""
    val jsonString7 = """{"code":403,"message":"Forbidden"}"""
    val jsonString8 = """{"code":418,"message":"Im a teapot"}"""
    val jsonString9 = """{"code":422,"message":"Unprocessable Entity"}"""
    val jsonString10 = """{"code":500,"message":"Internal Server Error"}"""
    val jsonString11 = """{"code":503,"message":"Service Unavailable"}"""

    1 to 11 foreach { _ => logs += jsonString1 }
    1 to 10 foreach { _ => logs += jsonString2 }
    1 to 9 foreach { _ => logs += jsonString3 }
    1 to 8 foreach { _ => logs += jsonString4 }
    1 to 7 foreach { _ => logs += jsonString5 }
    1 to 6 foreach { _ => logs += jsonString6 }
    1 to 5 foreach { _ => logs += jsonString7 }
    1 to 4 foreach { _ => logs += jsonString8 }
    1 to 3 foreach { _ => logs += jsonString9 }
    1 to 2 foreach { _ => logs += jsonString10 }
    1 to 1 foreach { _ => logs += jsonString11 }

    logs
  }

  def lastRecordFromStream(app:LogAggregatorApp, testDriver:TopologyTestDriver, outputTopic:String): String = {
    var recordValue: String = null
    val stringDeserializer = Serdes.String().deserializer()
    var keepLooking = true
    while(keepLooking) {
      try {
        val record = testDriver.readOutput(outputTopic, stringDeserializer, stringDeserializer)
        recordValue = record.value()
      } catch {
        case e: Exception => {
          keepLooking = false
        }
      }
    }
    recordValue
  }

  describe("LogAggregatorApp") {
    it("aggregates json messages") {

      val bootstrapServers = "localhost:9092"
      val inputTopic = "log-input-stream"
      val outputTopic = "log-output-stream"

      val logAggregatorApp = new LogAggregatorApp(bootstrapServers)
      logAggregatorApp.build()

      val testDriver = new TopologyTestDriver(logAggregatorApp.topology, logAggregatorApp.streamsConfig)

      val stringSerializer = new StringSerializer
      val factory = new ConsumerRecordFactory(stringSerializer, stringSerializer)
      val key = "kafka-key"

      val logs = jsonLogs

      val records = logs.map(jsonString => factory.create(inputTopic, key, jsonString)).toList
      testDriver.pipeInput(records)

      val recordValue = lastRecordFromStream(logAggregatorApp, testDriver, outputTopic)
      testDriver.close()

      val expected =
        """[{"code":200,"message":"OK","count":11},
          |{"code":301,"message":"Moved Permanently","count":10},
          |{"code":302,"message":"Found","count":9},
          |{"code":304,"message":"Not Modified","count":8},
          |{"code":400,"message":"Bad Request","count":7},
          |{"code":401,"message":"Unauthorized","count":6},
          |{"code":403,"message":"Forbidden","count":5},
          |{"code":418,"message":"Im a teapot","count":4},
          |{"code":422,"message":"Unprocessable Entity","count":3},
          |{"code":500,"message":"Internal Server Error","count":2}]""".stripMargin.replaceAll("\n", "")

      recordValue shouldEqual expected
    }
  }
}
