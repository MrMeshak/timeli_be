val scala3Version = "3.6.3"
val http4sVersion = "0.23.30"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "timeli",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta"         %% "munit"               % "1.0.0" % Test,
      "org.typelevel"         %% "cats-effect"         % "3.5.7",
      "org.http4s"            %% "http4s-ember-client" % http4sVersion,
      "org.http4s"            %% "http4s-ember-server" % http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % http4sVersion,
      "org.typelevel"         %% "log4cats-slf4j"      % "2.7.0",
      "org.slf4j"              % "slf4j-simple"        % "2.0.16",
      "com.github.pureconfig" %% "pureconfig-core"     % "0.17.8",
      "org.tpolecat"          %% "skunk-core"          % "0.6.4",
      "io.github.jmcardon"    %% "tsec-http4s"         % "0.5.0",
      "at.favre.lib"           % "bcrypt"              % "0.10.2",
    ),
  )
