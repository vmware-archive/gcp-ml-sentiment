package io.pivotal.Domain;

import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

import java.util.List;

/**
 * Created by mross on 10/7/16.
 */
public class QueryResultsViewMapping {

    String bookName;
    String authorName;
    String bookLocation;



    public QueryResultsViewMapping() {
        this.bookName = "N/A";
        this.authorName = "N/A";
        this.bookLocation = "N/A";
    }

    public QueryResultsViewMapping(TableRow rows) {
        int count =0;


        for (TableCell cell: rows.getF()) {

            if (count == 0 ) {
                bookName = cell.getV().toString();
            } else if (count == 1) {
                authorName = cell.getV().toString();
            } else {
                bookLocation = cell.getV().toString();

            }
            System.out.printf("%-50s", cell.getV());
        }

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
