/* =========================================================================================
 * Copyright © 2013-2014 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

import sbt._
import Keys._

object Projects extends Build {
  import Settings._
  import Dependencies._

  /** Build aggregator. */
  lazy val root = Project("root", file("."))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(noPublishing: _*)
    .aggregate(sigarLoader, verifyAgent, verifyOsgi)

  /** Primary Sigar artifact. */
  lazy val sigarLoader = Project("sigar-loader", file("sigar-loader"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(Osgi.sigarLoader: _*)
    .settings(SigarRepack.settings: _*)
    .settings(
      libraryDependencies ++=
        external(sigarJar, sigarZip) ++
        provided(osgiCore, osgiCompendium) ++
        test(junit, junitInterface, slf4Api, slf4Jul, slf4Log4j, logback)
    )

  /** Sigar java agent integration test. */
  lazy val verifyAgent = Project("verify-agent", file("verify-agent"))
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)
    .settings(SigarAgent.settings: _*)
    .settings(
      libraryDependencies ++=
        test(junit, junitInterface, slf4Api, slf4Jul, slf4Log4j, logback)
    ).dependsOn(sigarLoader)

  /** Sigar OSGI bundle activator integration test. */
  lazy val verifyOsgi = Project("verify-osgi", file("verify-osgi"))
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)
    .dependsOn(sigarLoader)

  val noPublishing = Seq(publish := (), publishLocal := (), publishArtifact := false)

  override lazy val settings =
    super.settings ++
      Seq(shellPrompt := { s => Project.extract(s).currentProject.id + " > " })

}
