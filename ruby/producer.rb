#!/usr/bin/env ruby

require 'kafka'
require 'json'

kafka = Kafka.new(['localhost:9092'], client_id: "sample-producer")

kafka_key = 'logs'
kafka_topic = 'log-input-stream'

messages = [
  { code: 200, message: 'OK' }.to_json,
  { code: 301, message: 'Moved Permanently' }.to_json,
  { code: 302, message: 'Found' }.to_json,
  { code: 304, message: 'Not Modified' }.to_json,
  { code: 400, message: 'Bad Request' }.to_json,
  { code: 401, message: 'Unauthorized' }.to_json,
  { code: 403, message: 'Forbidden' }.to_json,
  { code: 418, message: "I'm a teapot" }.to_json,
  { code: 422, message: 'Unprocessable Entity' }.to_json,
  { code: 500, message: 'Internal Server Error' }.to_json,
  { code: 503, message: 'Service Unavailable' }.to_json,
]

50.times do
  kafka.deliver_message(messages.sample, topic: kafka_topic, key: kafka_key)
end

sleep(60)

50.times do
  kafka.deliver_message(messages.sample, topic: kafka_topic, key: kafka_key)
end
