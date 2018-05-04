# Lucene Playground

Application to tinker with Lucene and MongoDB. Uses Kotlin and Gradle. 

## Requirements

JDK 8, Gradle, MongoDB.

## Installation

`brew install gradle`

`brew install kotlin`

## To Run

Have a mongodb running on 27017. Preload db "something" with inspection data. `mongoimport --db test --collection something --file $(curl https://raw.githubusercontent.com/ozlerhakan/mongodb-json-files/master/datasets/city_inspections.json -o city_inspections.json)`

Run via `TERM=dumb gradle` (use `dumb` to prevent noisy progress bar from bothering the CLI)
