package io.pivotal.service;

import com.google.api.services.bigquery.model.TableRow;

import java.util.ArrayList;
import java.util.List;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import io.pivotal.CredentialManager;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
/**
 * Created by mross on 10/7/16.
 */
public class BigQueryApiService {

    private String query;
    private long totalBytesProcessed;
    private String dataSetName = "gdelt-bq:internetarchivebooks";
    private boolean isCached;

    public long getTotalBytesProcessed() {
        return totalBytesProcessed;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public BigQueryApiService(String landmarkName) {
        this.query=  String.format(
                "SELECT * FROM ("
                + "SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects, LENGTH(BookMeta_Title) title_len"
                        + ", ROW_NUMBER() OVER (PARTITION BY BookMeta_Title, BookMeta_Creator) AS rn"
                        + " FROM (TABLE_QUERY([" + dataSetName + "], "
                        + " 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\") BETWEEN \"1819\" AND \"2014\"'))"
                        + " WHERE (REGEXP_MATCH(LOWER(CONCAT(BookMeta_Title, ' ', BookMeta_Subjects)), r'%s'))"
                        + ") b" +
                        " WHERE b.rn = 1"
                        + " ORDER BY title_len ASC"
                        + " LIMIT 20"
                , landmarkName.toLowerCase().replace("'", "\\'").replaceAll(" +", "\\\\s+"));
        System.out.println("QUERY: \"" + query + "\"");
    }

    public List<TableRow> executeQuery ()  {

        List<TableRow> rows = new ArrayList<TableRow>();

        try {
            CredentialManager credentialManager = new CredentialManager();

            Bigquery bigquery = credentialManager.getBiqQueryClient();
            //String projectId = "cbriant-dev";
            String projectId = credentialManager.getProjectId();

             rows =
                    executeQuery(
                            query,
                            bigquery,
                            projectId);

            System.out.println("Successfully executed a query");
        } catch (Exception e) {

            System.out.println(e);

        }

        return rows;
    }

    public boolean isCached() {
        return isCached;
    }

    private List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId) {
        List<TableRow> rows = new ArrayList<TableRow>();

        try {
            QueryResponse query =
                    bigquery.jobs().query(projectId, new QueryRequest().setQuery(querySql)).execute();

            // Execute it
            GetQueryResultsResponse queryResult =
                    bigquery
                            .jobs()
                            .getQueryResults(
                                    query.getJobReference().getProjectId(), query.getJobReference().getJobId())
                            .execute();
            // TODO: return some metadata about the query (bytes processed, elapsed time, data set)
            totalBytesProcessed = queryResult.getTotalBytesProcessed();
            isCached = queryResult.getCacheHit();
            System.out.println("Total Bytes: " + totalBytesProcessed + (isCached() ? " (cached)" : ""));
            return queryResult.getRows();

        } catch (Exception e) {

            System.out.println(e);
            return rows;
        }
    }


}
