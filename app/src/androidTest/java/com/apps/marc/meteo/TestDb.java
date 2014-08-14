package com.apps.marc.meteo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.apps.marc.meteo.data.WeatherContract.LocationEntry;
import com.apps.marc.meteo.data.WeatherContract.WeatherEntry;
import com.apps.marc.meteo.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        testValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(weatherCursor, weatherValues);

        dbHelper.close();
    }
}

// Unsimplified
// import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.test.AndroidTestCase;
//import android.util.Log;
//
//import com.apps.marc.meteo.data.WeatherContract.LocationEntry;
//import com.apps.marc.meteo.data.WeatherContract.WeatherEntry;
//import com.apps.marc.meteo.data.WeatherDbHelper;
//
//public class TestDb extends AndroidTestCase {
//    //All function that start with text are executed
//
//    public static final String LOG_TAG = TestDb.class.getSimpleName();
//
//    public void testCreateDb() throws Throwable {
//        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
//        SQLiteDatabase db = new WeatherDbHelper(
//                this.mContext).getWritableDatabase();
//        assertEquals(true, db.isOpen());
//        db.close();
//    }
//
//    public void testInserReadDb() {
//
//        //Writting to DB
//        String testName = "North Pole";
//        String testLocationSetting = "99705";
//        double testLatitude = 64.772;
//        double testLongitude = -147.355;
//
//        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(LocationEntry.COLUMN_CITY_NAME,testName);
//        values.put(LocationEntry.COLUMN_LOCATION_SETTING,testLocationSetting);
//        values.put(LocationEntry.COLUMN_COORD_LAT,testLatitude);
//        values.put(LocationEntry.COLUMN_COORD_LONG,testLongitude);
//
//        long locationRowId;
//        locationRowId = db.insert(LocationEntry.TABLE_NAME,null,values);
//
//        assertTrue(locationRowId != -1);
//        Log.d(LOG_TAG, "New row id: " + locationRowId);
//
//        //Reading from DB
//
//        String[] columns = {
//                LocationEntry._ID,
//                LocationEntry.COLUMN_LOCATION_SETTING,
//                LocationEntry.COLUMN_CITY_NAME,
//                LocationEntry.COLUMN_COORD_LONG,
//                LocationEntry.COLUMN_COORD_LAT
//         };
//
//        Cursor cursor = db.query(LocationEntry.TABLE_NAME,columns,null,null,null,null,null);
//
//        if (cursor.moveToFirst()){
//            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
//            String location = cursor.getString(locationIndex);
//
//            int nameIndex = cursor.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
//            String name = cursor.getString(nameIndex);
//
//            int latIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
//            double latitude = cursor.getDouble(latIndex);
//
//            int longIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
//            double longitude = cursor.getDouble(longIndex);
//
//            assertEquals(location,testLocationSetting);
//            assertEquals(name,testName);
//            assertEquals(latitude,testLatitude);
//            assertEquals(longitude,testLongitude);
//
//            // Fantastic.  Now that we have a location, add some weather!
//            ContentValues weatherValues = new ContentValues();
//            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
//            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
//            weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
//            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
//            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
//            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
//            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
//            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
//            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
//            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
//
//            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
//            assertTrue(weatherRowId != -1);
//
//            // A cursor is your primary interface to the query results.
//            Cursor weatherCursor = db.query(
//                    WeatherEntry.TABLE_NAME,  // Table to Query
//                    null, // leaving "columns" null just returns all the columns.
//                    null, // cols for "where" clause
//                    null, // values for "where" clause
//                    null, // columns to group by
//                    null, // columns to filter by row groups
//                    null  // sort order
//            );
//
//            if (!weatherCursor.moveToFirst()) {
//                fail("No weather data returned!");
//            }
//
//            assertEquals(weatherCursor.getInt(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_LOC_KEY)), locationRowId);
//            assertEquals(weatherCursor.getString(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT)), "20141205");
//            assertEquals(weatherCursor.getDouble(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES)), 1.1);
//            assertEquals(weatherCursor.getDouble(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)), 1.2);
//            assertEquals(weatherCursor.getDouble(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)), 1.3);
//            assertEquals(weatherCursor.getInt(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), 75);
//            assertEquals(weatherCursor.getInt(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), 65);
//            assertEquals(weatherCursor.getString(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC)), "Asteroids");
//            assertEquals(weatherCursor.getDouble(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)), 5.5);
//            assertEquals(weatherCursor.getInt(
//                    weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID)), 321);
//
//            weatherCursor.close();
//            dbHelper.close();
//
//
//        } else {
//            fail("No value returned :(");
//        }
//
//
//    }
//}