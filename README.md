# Sherlock: Anomaly Detector

[![build](https://github.com/yahoo/sherlock/workflows/build/badge.svg?branch=master)](https://github.com/yahoo/sherlock/actions?query=workflow%3Abuild)
[![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases]
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]
[![Coverage Status](https://coveralls.io/repos/github/yahoo/sherlock/badge.svg?branch=master)](https://coveralls.io/github/yahoo/sherlock)
[![GPL 3.0](https://img.shields.io/badge/license-GPL%203.0-blue.svg?style=flat)](LICENSE)


## Table of Contents

  * [Introduction](#introduction-to-sherlock)
  * [Components](#components)
  * [Detailed Description](#detailed-description)
  * [How to build](#how-to-build)
      * [Build JAR](#build-jar)
      * [How to run](#how-to-run)
      * [CLI args usage](#cli-args-usage)
  * [Committers](#committers)
  * [Contributors](#contributors)
  * [License](#license)

## Introduction to Sherlock

Sherlock is an anomaly detection service built on top of [Druid](http://druid.io/). It leverages [EGADS (Extensible Generic Anomaly Detection System)](https://github.com/yahoo/egads) to detect anomalies in time-series data. Users can schedule jobs on an hourly, daily, weekly, or monthly basis, view anomaly reports from Sherlock's interface, or receive them via email.

## Components

1. [Timeseries Generation](#timeseries-generation)
2. [EGADS Anomaly Detection](#egads-anomaly-detection)
3. [Redis database](#redis-database)
4. [UI in Spark Java](#ui-in-spark-java)

## Detailed Description

### Timeseries Generation
    
Timeseries generation is the first phase of Sherlock's anomaly detection. The user inputs a full Druid JSON query with a metric name and group-by dimensions. Sherlock validates the query, adjusts the time interaval and granularity based on the EGADS config, and makes a call to Druid. Druid responds with an array of time-series, which are parsed into EGADS time-series.
    
#### Sample Druid Query:
```json
{
  "metric": "metric(metric1/metric2)", 
  "aggregations": [
    {
      "filter": {
        "fields": [
          {
            "type": "selector", 
            "dimension": "dim1", 
            "value": "value1"
          }
        ], 
        "type": "or"
      }, 
      "aggregator": {
        "fieldName": "metric2", 
        "type": "longSum", 
        "name": "metric2"
      }, 
      "type": "filtered"
    }
  ], 
  "dimension": "groupByDimension", 
  "intervals": "2017-09-10T00:00:01+00:00/2017-10-12T00:00:01+00:00", 
  "dataSource": "source1", 
  "granularity": {
    "timeZone": "UTC", 
    "type": "period", 
    "period": "P1D"
  }, 
  "threshold": 50, 
  "postAggregations": [
    {
      "fields": [
        {
          "fieldName": "metric1", 
          "type": "fieldAccess", 
          "name": "metric1"
        }
      ], 
      "type": "arithmetic", 
      "name": "metric(metric1/metric2)", 
      "fn": "/"
    }
  ], 
  "queryType": "topN"
}
```
#### Sample Druid Response:
```json
[ {
  "timestamp" : "2017-10-11T00:00:00.000Z",
  "result" : [ {
    "groupByDimension" : "dim1",
    "metric(metric1/metric2)" : 8,
    "metric1" : 128,
    "metric2" : 16
  }, {
    "groupByDimension" : "dim2",
    "metric(metric1/metric2)" : 4.5,
    "metric1" : 42,
    "metric2" : 9.33
  } ]
}, {
  "timestamp" : "2017-10-12T00:00:00.000Z",
  "result" : [ {
    "groupByDimension" : "dim1",
    "metric(metric1/metric2)" : 9,
    "metric1" : 180,
    "metric2" : 20
  }, {
    "groupByDimension" : "dim2",
    "metric(metric1/metric2)" : 5.5,
    "metric1" : 95,
    "metric2" : 17.27
  } ]
} ]
```
### EGADS Anomaly Detection

Sherlock calls the user-configured EGADS API for each generated time-series, generates anomaly reports from the response, and stores these reports in a database. Users may also elect to receive anomaly reports by email.
    
### Redis Database

Sherlock uses a Redis backend [Redis](https://redis.io/) to store job metadata, generated anomaly reports, among other information, and as a persistent job queue. Keys related to Reports have retention policy. Hourly job reports have retention of 14 days and daily/weekly/monthly job reports have 1 year of retention.   
    
### Sherlock UI

Sherlock's user interface is built with [Spark](http://sparkjava.com/). The UI enables users to submit instant anomaly analyses, create and launch detection jobs, view anomalies on a heatmap, and on a graph.

## Building Sherlock

A `Makefile` is provided with all build targets.

### Building the JAR

```bash
make jar
```

This creates `sherlock.jar` in the `target/` directory.

### How to run
Sherlock is run through the commandline with config arguments.

```bash
java -Dlog4j.configuration=file:${path_to_log4j}/log4j.properties \
      -jar ${path_to_jar}/sherlock.jar \
      --version $(VERSION) \
      --project-name $(PROJECT_NAME) \
      --port $(PORT) \
      --enable-email \
      --failure-email $(FAILURE_EMAIL) \
      --from-mail $(FROM_MAIL) \
      --reply-to $(REPLY_TO) \
      --smtp-host $(SMTP_HOST) \
      --interval-minutes $(INTERVAL_MINUTES) \
      --interval-hours $(INTERVAL_HOURS) \
      --interval-days $(INTERVAL_DAYS) \
      --interval-weeks $(INTERVAL_WEEKS) \
      --interval-months $(INTERVAL_MONTHS) \
      --egads-config-filename $(EGADS_CONFIG_FILENAME) \
      --redis-host $(REDIS_HOSTNAME) \
      --redis-port $(REDIS_PORT) \
      --execution-delay $(EXECUTION_DELAY) \
      --timeseries-completeness $(TIMESERIES_COMPLETENESS)
```

### CLI args usage

| args                                  | required            | default                                                                                                             | description                                         |
|---------------------------------------|---------------------|---------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|
| --help                                |    -                | `false`                                                                                                             | [help](#help)                                       |
| --config                              |    -                | `null`                                                                                                              | [config](#config)                                   |
| --version                             |    -                | `v0.0.0`                                                                                                            | [version](#version)                                 |
| --egads-config-filename               |    -                | `provided`                                                                                                          | [egads-config-filename](#egads-config-filename)     |
| --port                                |    -                | `4080`                                                                                                              | [port](#port)                                       |
| --interval-minutes                    |    -                | `180`                                                                                                               | [interval-minutes](#interval-minutes)               |
| --interval-hours                      |    -                | `672`                                                                                                               | [interval-hours](#interval-hours)                   |
| --interval-days                       |    -                | `28`                                                                                                                | [interval-days](#interval-days)                     |
| --interval-weeks                      |    -                | `12`                                                                                                                | [interval-weeks](#interval-weeks)                   |
| --interval-months                     |    -                | `6`                                                                                                                 | [interval-months](#interval-months)                 |
| --enable-email                        |    -                | `false`                                                                                                             | [enable-email](#enable-email)                       |
| --from-mail                           | if email `enabled`  |                                                                                                                     | [from-mail](#from-mail)                             |
| --reply-to                            | if email `enabled`  |                                                                                                                     | [reply-to](#reply-to)                               |
| --smtp-host                           | if email `enabled`  |                                                                                                                     | [smtp-host](#smtp-host)                             |
| --smtp-port                           |    -                | `25`                                                                                                                | [smtp-port](#smtp-port)                             |
| --smtp-user                           |    -                |                                                                                                                     | [smtp-user](#smtp-user)                             |
| --smtp-password                       |    -                |                                                                                                                     | [smtp-password](#smtp-password)                     |
| --failure-email                       | if email `enabled`  |                                                                                                                     | [failure-email](#failure-email)                     |
| --execution-delay                     |    -                | `30`                                                                                                                | [execution-delay](#execution-delay)                 |
| --valid-domains                       |    -                | `null`                                                                                                              | [valid-domains](#valid-domains)                     |
| --redis-host                          |    -                | `127.0.0.1`                                                                                                         | [redis-host](#redis-host)                           |
| --redis-port                          |    -                | `6379`                                                                                                              | [redis-port](#redis-port)                           |
| --redis-ssl                           |    -                | `false`                                                                                                             | [redis-ssl](#redis-ssl)                             |
| --redis-timeout                       |    -                | `5000`                                                                                                              | [redis-timeout](#redis-timeout)                     |
| --redis-password                      |    -                | -                                                                                                                   | [redis-password](#redis-password)                   |
| --redis-clustered                     |    -                | `false`                                                                                                             | [redis-clustered](#redis-clustered)                 |
| --project-name                        |    -                | -                                                                                                                   | [project-name](#project-name)                       |
| --external-file-path                  |    -                | -                                                                                                                   | [external-file-path](#external-file-path)           |
| --debug-mode                          |    -                | `false`                                                                                                             | [debug-mode](#debug-mode)                           |
| --timeseries-completeness             |    -                | `60`                                                                                                                | [timeseries-completeness](#timeseries-completeness) |
| --http-client-timeout                 |    -                | `20000`                                                                                                             | [http-client-timeout](#http-client-timeout)         |
| --backup-redis-db-path                |    -                | `null`                                                                                                              | [backup-redis-db-path](#backup-redis-db-path)       |
| --druid-brokers-list-file             |    -                | `null`                                                                                                              | [druid-brokers-list-file](#druid-brokers-list-file) |
| --truststore-path                     |    -                | `null`                                                                                                              | [truststore-path](#truststore-path)                 |
| --truststore-type                     |    -                | `jks`                                                                                                               | [truststore-type](#truststore-type)                 |
| --truststore-password                 |    -                | `null`                                                                                                              | [truststore-password](#truststore-password)         |
| --keystore-path                       |    -                | `null`                                                                                                              | [keystore-path](#keystore-path)                     |
| --keystore-type                       |    -                | `jks`                                                                                                               | [keystore-type](#keystore-type)                     |
| --keystore-password                   |    -                | `null`                                                                                                              | [keystore-password](#keystore-password)             |
| --key-dir                             |    -                | `null`                                                                                                              | [key-dir](#key-dir)                                 |
| --cert-dir                            |    -                | `null`                                                                                                              | [cert-dir](#cert-dir)                               |
| --https-hostname-verification         |    -                | `true`                                                                                                              | [https-hostname-verification](#https-hostname-verification)             |
| --custom-ssl-context-provider-class   |    -                | [`DefaultSslContextProvider`](https://github.com/yahoo/sherlock/tree/master/src/main/java/com/yahoo/sherlock/utils) | [custom-ssl-context-provider-class](#custom-ssl-context-provider-class) |
| --custom-secret-provider-class        |    -                | [`DefaultSecretProvider`](https://github.com/yahoo/sherlock/tree/master/src/main/java/com/yahoo/sherlock/utils)     | [custom-secret-provider-class](#custom-secret-provider-class)           |
| --prophet-url                       |    -                | `127.0.0.1:4080`                                                                                                    | [prophet-url](#prophet-url)                                             |
| --prophet-timeout                   |    -                | `120000`                                                                                                            | [prophet-timeout](#prophet-timeout)                                     |
| --prophet-principal                 |    -                | `prophet-principal`                                                                                                 | [prophet-principal](#prophet-principal)                                 |

#### help
Prints commandline argument help message.
#### config
Path to a Sherlock configuration file, where the above configuration may be specified. Config arguments in the file override commandline arguments.
#### version
Version of `sherlock.jar` to display on the UI 
#### egads-config-filename
Path to a custom EGADS configuration file. If none is specified, the default configuration is used.
#### port
Port on which to host the Spark application.
#### interval-minutes
Number of historic data points to use for detection on time-series every minute.
#### interval-hours
Number of historic data points to use for detection on hourly time-series.
#### interval-days
Number of historic data points to use for detection on daily time-series.
#### interval-weeks
Number of historic data points to use for detection on weekly time-series.
#### interval-months
Number of historic data points to use for detection on monthly time-series.
#### enable-email
Enable the email service. This enables users to receive email anomaly report notifications.
#### from-mail
The handle's `FROM` email displayed to email recipients.
#### reply-to
The handle's `REPLY TO` email where replies will be sent.
#### smtp-host
The email service's `SMTP HOST`.
#### smtp-port
The email service's `SMTP PORT`. The default value is `25`.
#### smtp-user
The email service's `SMTP USER`.
#### smtp-password
The email service's `SMTP PASSWORD`.
#### failure-email
A dedicated email which may be set to receive job failure notifications.
#### execution-delay
Sherlock periodically pings Redis to check scheduled jobs. This sets the ping delay in seconds. Jobs are scheduled with a precision of one minute.
#### valid-domains
A comma-separated list of valid domains to receive emails, e.g. 'yahoo,gmail,hotmail'. If specified, Sherlock will restrict who may receive emails.
#### redis-host
The Redis backend hostname.
#### redis-port
The Redis backend port.
#### redis-ssl
Whether Sherlock should connect to Redis via SSL.
#### redis-timeout
The Redis connection timeout.
#### redis-password
The password to use when authenticating to Redis.
#### redis-clustered
Whether the Redis backend is a cluster.
#### project-name
Name of the project to display on UI.
#### external-file-path
Specify the path to external files for Spark framework via this argument.
#### debug-mode
Debug mode enables debug routes. Ex. '/DatabaseJson' (shows redis data as json dump). Look at `com.yahoo.sherlock.App` for more details. 
#### timeseries-completeness
This defines minimum fraction of datapoints needed in the timeseries to consider it as a valid timeseries o/w sherlock ignores such timeseries. (default value 60 i.e. 0.6 in fraction)
#### http-client-timeout
HttpClient timeout can be configured using this(in millis). (default value 20000)
#### backup-redis-db-path
Backup redis DB at given file path as json dump of indices and objects. Backup is done per day at midnight. Default this parameter is null i.e. no buckup. However, BGSAVE command is run at midnight to save redis local dump.
#### druid-brokers-list-file
Specify the path to an access control list file of permitted druid broker hosts for querying. Format: `<host1>:<port>,<host2>:<port>...` (default null i.e any host is allowed)
#### truststore-path
Path to specify truststore location for mTLS connections. (default `null`)
#### truststore-type                  
Param to specify truststore type for mTLS connections. (default `jks`)
#### truststore-password              
Param to specify truststore password for mTLS connections. (default `null`)
#### keystore-path                    
Path to specify keystore location for mTLS connections. (default `null`)
#### keystore-type                    
Param to specify keystore type for mTLS connections. (default `jks`)
#### keystore-password                
Param to specify keystore password for mTLS connections. (default `null`)
#### key-dir                          
Param to specify key directory containing multiple keys(for different clusters) for mTLS connections (default `null`).
This is used when `Principal Name` is given in druid cluster form.
It looks for filename containing `Principal Name` under this dir.
If `--key-dir` and `--cert-dir` values are same then the filename should also contain the identifier `key` for private key file and `cert` for public key file.
#### cert-dir                         
Param to specify cert directory containing multiple certs(for different clusters) for mTLS connections (default `null`)."
This is used when `Principal Name` is given in druid cluster form.
It looks for file name containing `Principal Name` under this dir.
If `--key-dir` and `--cert-dir` values are same then the filename should also contain the identifier `key` for private key file and `cert` for public key file.
#### https-hostname-verification      
Param to enable/disable https hostname verification for mTLS connections. (default `true` i.e. hostname verification enabled)
#### custom-ssl-context-provider-class
Param to specify custom ssl context provider class for mTLS connections. (default `com.yahoo.sherlock.utils.DefaultSslContextProvider` which returns SSLContext with validation)
#### custom-secret-provider-class     
Param to specify custom secret provider class for passwords. (default `com.yahoo.sherlock.utils.DefaultSecretProvider` which returns secrets specified from CLISettings)
#### prophet-url
API endpoint of a running Prophet Service. (default `127.0.0.1:4080` which include both url and port)
#### prophet-timeout
Timeout for querying the Prophet Service. (default `120000` milliseconds)
#### prophet-principal
The Kubernetes principal that the Prophet Service is located. (default `prophet-principal`)

## Getting started
It is suggested to use Java8 and Maven 3.3 to develop Sherlock.

## Further Development
### Adding a new anomaly detector to Sherlock
Currently, Sherlock supports two detector pipelines (Egads/Prophet). Both pipelines use Egads' anomaly detection module for anomaly detection.
The Egads pipeline conducts both time series forecasting and anomaly detection via Egads anomaly detection library.
On the other hand, the Prophet pipeline allows Sherlock to query forecasted time series from a Prophet web service.
After that, the Prophet pipeline performs anomaly detection via Egads' anomaly detection module.
If the developer wants to add a new anomaly detector to Sherlock, the developer should look at the abstract class `service/DetectorAPIService.java`, and implement a new detector class that extends `DetectorAPIService`.
More specifically, developers should implement abstract methods `detectAnomaliesAndForecast` and `detectAnomalies`. The two abstract methods are elaborated in sections below.

### Developing the instant detection feature
Sherlock allows the user to perform an instant anomaly detection, which is accessible via the /Flash-Query endpoint. The endpoint is linked to method `processInstantAnomalyJob` under `Routes.java`,
which calls method `detectWithResults` under `DetectorService.java`.
Method `detectWithResults` checks which detector the user wants to use, assign the corresponding `DetectorAPIService` instance, and calls the instance's `detectAnomaliesAndForecast` method.
Method `detectAnomaliesAndForecast` does anomaly detection and returns the original time series, expected time series, and the anomaly points. The combined results are displayed via the /Flash-Query/ProcessAnomalyReport endpoint.

### Developing the Job Scheduling feature
Sherlock allows the user to schedule anomaly detection jobs that run routinely.
Regarding the job scheduling, Sherlock uses `JobScheduler.java` to maintain a Priority Queue stored in Redis. Every time the user adds a job,
Sherlock puts the job into via method `scheduleJob` with the job's next run time as the priority.
Sherlock keeps checking the current system time, and pops the Priority Queue as required via method `consumeAndExecuteTasks`.
For the actual detection, method `consumeAndExecuteTasks` executes a job that is due, which eventually goes to method `runDetection` under `DetectorService.java`.
Method `runDetection` checks which detector the user wants to use, assign the corresponding DetectorAPIService instance, and calls the instance's `detectAnomalies` method.
Method `detectAnomalies` does anomaly detection and returns anomaly points because job reports display only detected anomaly points.

### Understanding TimeSeries/Anomaly format used in Sherlock
All current pipelines use `TimeSeries` and `Anomaly` classes defined in Egads heavily. To gain a better understanding of those formats, developers should read `TimeSeries.java/Anomaly.java` defined in the [Egads repository](https://github.com/yahoo/egads).

## Committers

Jigar Patel, [jigsdevbox@gmail.com](mailto:jigsdevbox@gmail.com)

Jeff Niu, [jeffniu22@gmail.com](mailto:jeffniu22@gmail.com)

## Contributors

Josh Walters, [josh@joshwalters.com](mailto:josh@joshwalters.com)

Stephan Stiefel, [Stephan3555](https://github.com/Stephan3555)

Han Xu, [hanxu12](https://github.com/hanxu12)

## License

Code licensed under the [GPL v3 License](https://www.gnu.org/licenses/quick-guide-gplv3.en.html). See LICENSE file for terms.

[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/com/yahoo/sherlock/sherlock/ "Sonatype Releases"
[Link-SonatypeSnapshots]: https://oss.sonatype.org/content/repositories/snapshots/com/yahoo/sherlock/sherlock/ "Sonatype Snapshots"
[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/oss.sonatype.org/com.yahoo.sherlock/sherlock.svg "Sonatype Releases"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/oss.sonatype.org/com.yahoo.sherlock/sherlock.svg "Sonatype Snapshots"
