package it.chefacile.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.ListCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private MaterialListView mListView;

    EditText editText;
    TextView responseView;
    ProgressBar progressBar;
    Button TutorialButton;
    Button AddButton;
    String ingredients = ",";
    ArrayAdapter<String> adapter;
    String urlSpo = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?ingredients=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        TutorialButton = (Button) findViewById(R.id.button);
        AddButton = (Button) findViewById(R.id.button2);
        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        mListView = (MaterialListView) findViewById(R.id.material_listview);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        TutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IntroScreenActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(editText.getText().toString().equals("")){
                    ingredients += editText.getText().toString().trim() + "";
                    editText.getText().clear();

                }
                else {
                    ingredients += editText.getText().toString().trim() + ",";
                    String singleIngredient = editText.getText().toString().trim();
                    adapter.add(singleIngredient.substring(0,1).toUpperCase() + singleIngredient.substring(1));
                    editText.getText().clear();
                    Log.d("INGREDIENTS ,", ingredients);
                }
                //responseView.setText(ingredients);
                //ingredients += editText.getText().toString().trim() + ",";

            }
        });

        try {
            fillArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        responseView = (TextView) findViewById(R.id.responseView);
        editText = (EditText) findViewById(R.id.ingredientText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final FloatingActionButton actionABC = (FloatingActionButton) findViewById(R.id.action_abc);
        actionABC.bringToFront();
        actionABC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new RetrieveFeedTask().execute();


            }

            // Snackbar.make(view, "Non disponibile, mangia l'aria", Snackbar.LENGTH_LONG)
            //         .setAction("Action", null).show();

        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d("Tect",responseView.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            // responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String ingredient = responseView.getText().toString();

            // Do some validation here about String ingredient

            try {

                URL urlSpoo = new URL(urlSpo + ingredients + "&number=20&ranking=1");
                HttpURLConnection urlConnection = (HttpURLConnection) urlSpoo.openConnection();
                //TODO: Changing key values
                urlConnection.setRequestProperty("KEY","KEY");


                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {

            if(response == null) {
                response = "THERE WAS AN ERROR";
                Snackbar.make(responseView, "Network connectivity unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            }
            else if(response.toString().trim().equals("[]") || response.toString().trim().equals("")){
                Snackbar.make(responseView, "No recipes for these ingredients", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
            }
            else {
                progressBar.setVisibility(View.GONE);
                // responseView.setText(response);
                Intent myIntent1 = new Intent(MainActivity.this, ResultsActivity.class);
                myIntent1.putExtra("mytext", response);
                startActivity(myIntent1);
                responseView.setText(null);
                ingredients = "";
            }
            //  check this.exception
            //  do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
    private void fillArray() throws JSONException {
        List<Card> cards = new ArrayList<>();
        cards.add(generateNewCard());
        mListView.getAdapter().addAll(cards);
    }

    private Card generateNewCard() {
        return new Card.Builder(this)
                .setTag("LIST_CARD")
                .setDismissible()
                .withProvider(new ListCardProvider())
                .setLayout(R.layout.material_list_card_layout)
                .setTitle("Ingredients")
                .setDescription("Take a list")
                .setAdapter(adapter)
                .endConfig()
                .build();
    }

}