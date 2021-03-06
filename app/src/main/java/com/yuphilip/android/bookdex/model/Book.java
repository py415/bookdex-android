package com.yuphilip.android.bookdex.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Book {

    //region Properties

    String openLibraryId;
    String author;
    String publisher;
    String publishedDate;
    String title;

    //endregion

    public Book(String openLibraryId, String author, String title, String publisher, String publishedDate) {

        this.openLibraryId = openLibraryId;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.publishedDate = publishedDate;

    }

    private Book() {

    }

    // Returns a Book given the expected JSON
    private static Book fromJson(JSONObject jsonObject) {

        Book book = new Book();

        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("cover_edition_key")) {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if (jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }

            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            book.author = getAuthor(jsonObject);
            book.publisher = getPublisher(jsonObject);
            book.publishedDate = getPublishedYear(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        // Return new object
        return book;

    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject jsonObject) {

        try {
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];

            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }

            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }

    }

    // Return comma separated publisher list when there is more than one author
    private static String getPublisher(final JSONObject jsonObject) {

        try {
            final JSONArray publishers = jsonObject.getJSONArray("publisher");
            int numPublishers = publishers.length();
            final String[] publisherStrings = new String[numPublishers];
            for (int i = 0; i < numPublishers; ++i) {
                publisherStrings[i] = publishers.getString(i);
            }

            return TextUtils.join(", ", publisherStrings);
        } catch (JSONException e) {
            return "";
        }

    }

    // Return comma separated publisher list when there is more than one author
    private static String getPublishedYear(final JSONObject jsonObject) {

        try {
            final JSONArray publishedYear = jsonObject.getJSONArray("publish_year");
            int numPublishYears = publishedYear.length();
            final String[] publishedYearString = new String[numPublishYears];

            for (int i = 0; i < numPublishYears; ++i) {
                publishedYearString[i] = publishedYear.getString(i);
            }

            return TextUtils.join(", ", publishedYearString);
        } catch (JSONException e) {
            return "";
        }
    }

    // Decodes array of book json results into business model objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray) {

        ArrayList<Book> books = new ArrayList<>(jsonArray.length());

        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson;

            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Book book = Book.fromJson(bookJson);

            if (book != null) {
                books.add(book);
            }

        }

        return books;

    }

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    // Get book cover from covers API
    public String getCoverUrl() {

        return "https://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";

    }

}

