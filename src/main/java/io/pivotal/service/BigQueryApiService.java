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

    String query;

    public BigQueryApiService(String landmarkName) {
        this.query=  String.format(
                "SELECT BookMeta_Title, BookMeta_Creator, BookMeta_Subjects FROM"
                        + " (TABLE_QUERY([gdelt-bq:internetarchivebooks], 'REGEXP_EXTRACT(table_id, r\"(\\d{4})\")"
                        + " BETWEEN \"1819\" AND \"2014\"')) WHERE LOWER(BookMeta_Subjects) CONTAINS LOWER(\"%s\")"
                , landmarkName);
    }

    public List<TableRow> executeQuery ()  {

        List<TableRow> rows = new ArrayList<TableRow>();

        try {
            CredentialManager credentialManager = new CredentialManager();

            Bigquery bigquery = credentialManager.getBiqQueryClient();
            String projectId = "cbriant-dev";

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


    private static List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId) {
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
            return queryResult.getRows();

        } catch (Exception e) {
            System.out.println(e);
            return rows;
        }
    }


}
