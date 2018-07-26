import org.scalatest._

class LogAggregatorTest extends FunSpec with Matchers with GivenWhenThen {

  describe("groupedLimitedBy") {

    it("groups, limits, and appends count to JSON messages") {

      val logEntry = new LogEntry(200, "OK")
      val jsonString1 = logEntry.asJsonString()

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

      val logAggregator = new LogAggregator()
      1 to 11 foreach { _ => logAggregator.add(jsonString1) }
      1 to 10 foreach { _ => logAggregator.add(jsonString2) }
      1 to 9 foreach { _ => logAggregator.add(jsonString3) }
      1 to 8 foreach { _ => logAggregator.add(jsonString4) }
      1 to 7 foreach { _ => logAggregator.add(jsonString5) }
      1 to 6 foreach { _ => logAggregator.add(jsonString6) }
      1 to 5 foreach { _ => logAggregator.add(jsonString7) }
      1 to 4 foreach { _ => logAggregator.add(jsonString8) }
      1 to 3 foreach { _ => logAggregator.add(jsonString9) }
      1 to 2 foreach { _ => logAggregator.add(jsonString10) }
      1 to 1 foreach { _ => logAggregator.add(jsonString11) }

      val grouped = logAggregator.groupedLimitedBy(10)
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

      grouped shouldEqual expected
    }

  }

}