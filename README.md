# Lucene Playground

Application to tinker with Lucene and MongoDB. Uses Kotlin and Gradle.

## Requirements

JDK 8, Gradle, MongoDB.

## Installation

`brew install gradle`

`brew install kotlin`

Have a mongodb running on 27017 `brew install mongodb && mongod`. Preload db "inspections" with inspection data as follows:

```
curl https://raw.githubusercontent.com/ozlerhakan/mongodb-json-files/master/datasets/city_inspections.json -O
mongoimport --db test --collection inspections --file city_inspections.json
```

## Run app

`TERM=dumb gradle`

> use env variable `TERM=dumb` to prevent noisy progress bar from bothering the CLI

This loads up 100 inspection documents into Lucene and listens for lucene queries.

Try `result:"No Violation" AND business_name:Constructon~`
