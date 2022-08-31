/*
 * Copyright 2017, Yahoo Holdings Inc.
 * Copyrights licensed under the GPL License.
 * See the accompanying LICENSE file for terms.
 */

package com.yahoo.sherlock.model;

import com.yahoo.sherlock.settings.Constants;
import com.yahoo.sherlock.utils.Utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import spark.QueryParamsMap;

/**
 * Deserializer class for user query request.
 */
@Data
public class UserQuery {

    /**
     * Build a {@code UserQuery} object from {@code Spark Request}
     * query parameter map.
     *
     * @param params parameter map
     * @return a user query instance
     */
    public static UserQuery fromQueryParams(QueryParamsMap params) {
        return Utils.deserializeQueryParams(new UserQuery(), params);
    }

    /** Input user query. */
    private String query;

    /** Test name entered by user. */
    private String testName;

    /** Description of the test. */
    private String testDescription;

    /** Url of superset associated with the query. */
    private String queryUrl;

    /** Owner of the anomaly test. */
    private String owner;

    /** Email id of the owner. */
    private String ownerEmail;

    /** Email on no data case. */
    private Boolean emailOnNoData;

    /** Query end time for custom time-range. */
    private String queryEndTimeText;

    /** Granularity of data. */
    private String granularity;

    /** Frequency of the job. */
    private String frequency;

    /** Threshold for standard deviation on normal distribution curve. */
    private Double sigmaThreshold;

    /** URL to send instant anomaly request. */
    private String druidUrl;

    /** Id of the associated cluster for this job. */
    private Integer clusterId;

    /** Timeseries range to query in druid. */
    private Integer timeseriesRange;

    /** Detection window for Instant anomaly job.*/
    private Integer detectionWindow;

    /** Granularity range to aggregate on. */
    private Integer granularityRange;

    /** Timeseries Framework (e.g., Egads/Prophet). */
    private String tsFramework;

    /** Timeseries model. */
    private String tsModels;

    /** Anomaly detection model. */
    private String adModels;

    /** Hours of lag associated with the job's cluster. */
    private Integer hoursOfLag;

    /** Meta Prophet's growth model ("linear"/"flat"). */
    private String growthModel;

    /** Meta Prophet's yearly seasonality ("auto"/"true"/"false"). */
    private String yearlySeasonality;

    /** Meta Prophet's weekly seasonality parameter ("auto"/"true"/"false"). */
    private String weeklySeasonality;

    /** Meta Prophet's daily seasonality parameter ("auto"/"true"/"false"). */
    private String dailySeasonality;

    /** RB: Druid Host to make the query. */
    private String brokerHost;

    /** RB: Druid Broker Port to make the query. */
    private Integer brokerPort;

    /**
     * Removing duplicate emails and return set of comma separated emails.
     * @return comma separated set of emails
     */
    public String getOwnerEmail() {
        if (this.ownerEmail != null) {
            String[] emails = this.ownerEmail.replace(" ", "").split(Constants.COMMA_DELIMITER);
            Set<String> setOfEmails = Arrays.stream(emails).collect(Collectors.toSet());
            this.ownerEmail = setOfEmails.stream().collect(Collectors.joining(Constants.COMMA_DELIMITER));
        }
        return this.ownerEmail;
    }
}
