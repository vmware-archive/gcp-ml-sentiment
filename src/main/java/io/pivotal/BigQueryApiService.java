package io.pivotal;

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

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
/**
 * Created by mross on 10/7/16.
 */
public class BigQueryApiService {

    public BigQueryApiService() {

    }

    public List<TableRow> executeQuery (String query) throws IOException {

        List<TableRow> results = new ArrayList<TableRow>();

        CredentialManager credentialManager = new CredentialManager();

        Bigquery bigquery = credentialManager.getBiqQueryClient();
        String projectId = "cbriant-dev";

        List<TableRow> rows =
                executeQuery(
                        query,
                        bigquery,
                        projectId);

        return rows;
    }


    private static List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId)
            throws IOException {
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
    }
    

}
