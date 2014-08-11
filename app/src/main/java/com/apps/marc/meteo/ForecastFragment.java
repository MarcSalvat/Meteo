package com.apps.marc.meteo;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {
    ArrayAdapter<String> arrayAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getWeatherFromAPI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getWeatherFromAPI();
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemForecastString = arrayAdapter.getItem(i);
                Toast.makeText(getActivity(), itemForecastString, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, itemForecastString);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void getWeatherFromAPI() {
        RequestParams params = new RequestParams();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String location = prefs.getString("location", "08001");
        final int days = 7;

        params.put("q", location);
        params.put("cnt", days);
        params.put("mode", "json");
        params.put("units", "metric");
        WeatherAPI.get("daily", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("response", response.toString());
                try {
                    String[] updatedData = WeatherDataParser.getWeatherDataFromJson(response.toString(), days);
                    List<String> weakForecast = new ArrayList<String>(Arrays.asList(updatedData));
                    arrayAdapter.clear();
                    arrayAdapter.addAll(weakForecast);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("response", errorResponse.toString());
            }
        });
    }
}