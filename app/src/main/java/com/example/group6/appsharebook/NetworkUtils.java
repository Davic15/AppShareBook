package com.example.group6.appsharebook;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Utility class for using the Google Book Search API to download book information.
 */
class NetworkUtils {

    private static final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q"; // Parameter for the search string.

    /**
     * Method for downloading book information from the Books API based on a search term.
     * This method makes a network call so it can not be called on the main thread.
     * @param queryString The search term for the Books API query
     * @return The raw response from the API as a JSON String
     */
    static String getBookInfo(String queryString){

        // Set up variables for the try block that need to be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        // Attempt to query the Books API.
        try {

            URL requestURL = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:"+queryString);

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Read the response string into a StringBuilder.
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                builder.append(line + "\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                // return null;
                return null;
            }
            bookJSONString = builder.toString();

            // Catch errors.
        } catch (IOException e) {
            e.printStackTrace();

            // Close the connections.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return the raw response.
        return bookJSONString;
    }
}

