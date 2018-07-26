# start zookeeper
zookeeper-server-start $KAFKA_CONF/zookeeper.properties

# start kafka
$KAFKA_HOME/bin/kafka-server-start $KAFKA_CONF/server.properties

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic log-input-stream
$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic log-output-stream

# console producer
$KAFKA_HOME/bin/kafka-console-producer --broker-list localhost:9092 --topic log-input-stream

# consumers
$KAFKA_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic log-input-stream
$KAFKA_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic log-output-stream

# build jar
./gradlew clean shadowJar

# run app
java -cp "./build/libs/*" LogAggregatorApp

# execute producer

$KAFKA_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic log-output-stream
[{"code":401,"message":"Unauthorized","count":8},{"code":500,"message":"Internal Server Error","count":8},{"code":301,"message":"Moved Permanently","count":6},{"code":403,"message":"Forbidden","count":6},{"code":503,"message":"Service Unavailable","count":4},{"code":400,"message":"Bad Request","count":4},{"code":200,"message":"OK","count":4},{"code":418,"message":"I\u0027m a teapot","count":3},{"code":422,"message":"Unprocessable Entity","count":3},{"code":304,"message":"Not Modified","count":2}]
[{"code":503,"message":"Service Unavailable","count":7},{"code":302,"message":"Found","count":7},{"code":301,"message":"Moved Permanently","count":6},{"code":304,"message":"Not Modified","count":5},{"code":418,"message":"I\u0027m a teapot","count":5},{"code":400,"message":"Bad Request","count":5},{"code":422,"message":"Unprocessable Entity","count":4},{"code":200,"message":"OK","count":4},{"code":403,"message":"Forbidden","count":3},{"code":401,"message":"Unauthorized","count":2}]
