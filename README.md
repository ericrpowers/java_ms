# java_ms

java_ms is a CLI-based Minesweeper and solver written in Java.

Additionally, there is Docker support and unit tests utilizing JUnit.

## Prerequisites

> Suggest using built-in means like `brew` or `apt-get` to avoid human error.

* OpenJDK (verified working with 7 & 8) - http://openjdk.java.net/install/
* (optional) JUnit - https://github.com/junit-team/junit4/wiki/Download-and-Install
* (optional) Docker - https://docs.docker.com/engine/getstarted/

## Running java_ms

From the repo directory:

    javac MineSweeper.java
    java MineSweeper

## Solver

An automatic way to solve Minesweeper. The current thought process is the following:

1. Identify all safe moves and mines based on immediate neighbors
2. Identify all safe moves based on known mines
3. Identify all safe moves by using neighbors' info
4. Identify least risky move
5. Blind click if no other options are found

The pass rate is a little above 90% (100,000 iterations) currently.
