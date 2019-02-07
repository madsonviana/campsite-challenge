package com.upgrade.benchmark

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import scala.concurrent.duration._

class Benchmark extends Simulation {

  val scn = scenario("FindNotExistingReservation").repeat(10000, "counter") {
    exec(
      http("Find by id")
        .get("http://localhost:8080/server/5c5a198b3acf52388d17c8e1")
        .check(status.is(404))
    )
  }

  setUp(scn.inject(atOnceUsers(30))).maxDuration(FiniteDuration.apply(10, "minutes"))
}
