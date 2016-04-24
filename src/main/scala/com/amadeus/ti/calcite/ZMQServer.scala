package com.amadeus.ti.calcite

import org.zeromq.ZMQ
import org.zeromq.ZMQ.{Context,Socket}

object ZMQServer {

  def main(_args : Array[String]) {

    val context = ZMQ.context(1)
    val receiver = context.socket(ZMQ.REP)
    val binding = "tcp://*:5555"

    receiver.bind(binding)

    println("Listening on " + binding)

    while (true) {
      // Wait for next request from client
      val message = receiver.recv(0)
      val request = new String(message)

      println("Received request: " + request)

      val response = Processor(request)

      println("Sending response: " + response)

      // Send reply back to client
      val reply = response.getBytes
      receiver.send(reply, 0)
    }
  }
}
