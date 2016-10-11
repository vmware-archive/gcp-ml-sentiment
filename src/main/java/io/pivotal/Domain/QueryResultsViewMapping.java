package io.pivotal.Domain;

import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mross on 10/7/16.
 */
public class QueryResultsViewMapping {

    String bookName;
    String authorName;
    String bookLocation;


    private static final String DEFAULT_VALUE = "(Not available)";

    public QueryResultsViewMapping() {

        this.bookName = "N/A";
        this.authorName = "N/A";
        this.bookLocation = "N/A";
    }

    public QueryResultsViewMapping(TableRow rows) {
        int count =0;
        System.out.println("Entered the mapping");

        System.out.println(rows.toString());
        for (TableCell cell: rows.getF()) {
            // Why are we seeing "author = java.lang.Object@3d347088"?
            Object cellObj = cell.getV();
            String stringVal = DEFAULT_VALUE;
            if (cellObj != null) {
                stringVal = cell.getV().toString();
                if (stringVal.startsWith("java.lang.Object@")) {
                    stringVal = DEFAULT_VALUE;
                }
            }
            if (count == 0 ) {
                bookName = stringVal;
            } else if (count == 1) {
                authorName = stringVal;
            } else {
                bookLocation = stringVal;
            }
            count++;
            System.out.printf("%-50s", cell.getV());
        }

        }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }

    @Override
    public String toString() {
        return "QueryResultsViewMapping{" +
                "bookName='" + bookName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", bookLocation='" + bookLocation + '\'' +
                '}';
    }
}
