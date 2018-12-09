package com.example.jaewookjaelee.mp6_emotionrecognition;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.app.Fragment;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Uri image;
    Bitmap bitMap;
    String mCameraFileName;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final String uriBase =
            "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

    private final String subscriptionKey = "5551205fa8ec4b838ff7db6a016ad233";

    private static final String imageWithFaces =
            "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}";

    private static final String faceAttributes =
            "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";

    //variables
    public static String gender;
    public static double age;
    public static double smile;
    public static String glasses;
    public static double happiness;
    public static double neutral;
    public static double sadness;
    public static double surprise;
    public static double anger;
    public static double fear;
    public static boolean makeUp;
    public static double baldness;
    public static double moustache;
    public static double beard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(android.R.color.transparent);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 0);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        bitMap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitMap);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                    URIBuilder builder = new URIBuilder(uriBase);

                    // Request parameters. All of them are optional.
                    builder.setParameter("returnFaceId", "true");
                    builder.setParameter("returnFaceLandmarks", "false");
                    builder.setParameter("returnFaceAttributes", faceAttributes);

                    // Prepare the URI for the REST API call.
                    URI uri = builder.build();
                    HttpPost request = new HttpPost(uri);

                    // Request headers.
                    request.setHeader("Content-Type", "application/octet-stream");
                    request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

                    // Request body
                    File file = new File(getApplicationContext().getCacheDir(), "image");

                    file.createNewFile();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitMap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    FileEntity reqEntity = new FileEntity(file, "application/octet-stream");
                    request.setEntity(reqEntity);

                    // Execute the REST API call and get the response entity.
                    CloseableHttpResponse response = httpClient.execute(request);
                    HttpEntity entity = response.getEntity();

                    System.out.println(entity);

                    if (entity != null)
                    {
                        // Format and display the JSON response.
                        System.out.println("REST Response:\n");

                        String jsonString = EntityUtils.toString(entity).trim();


                        if (jsonString.charAt(0) == '[') {
                            JSONArray jsonArray = new JSONArray(jsonString);
                            System.out.println(jsonArray.toString(2));
                        }
                        else if (jsonString.charAt(0) == '{') {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            System.out.println(jsonObject.toString(2));
                        } else {
                            System.out.println(jsonString);
                        }

                        JSONArray jsonArray = new JSONArray(jsonString);

                        if (jsonArray.length() < 1) {
                            final Context context = getApplicationContext();
                            final CharSequence text = "Did not find a face...";
                            final int duration = Toast.LENGTH_SHORT;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                            Toast.makeText(context, text, duration).show();
                                        }
                                        }, 2000);
                                }
                            });
                        }
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        JSONObject jsonFaceAttributes = jsonObject.getJSONObject("faceAttributes");
                        JSONObject jsonFacialHair = jsonFaceAttributes.getJSONObject("facialHair");
                        JSONObject jsonEmotion = jsonFaceAttributes.getJSONObject("emotion");
                        JSONObject jsonHair = jsonFaceAttributes.getJSONObject("hair");
                        JSONObject jsonMakeUp = jsonFaceAttributes.getJSONObject("makeup");

                        smile = jsonFaceAttributes.getDouble("smile");
                        gender = jsonFaceAttributes.getString("gender");
                        age = jsonFaceAttributes.getDouble("age");
                        moustache = jsonFacialHair.getDouble("moustache");
                        beard = jsonFacialHair.getDouble("beard");
                        glasses = jsonFaceAttributes.getString("glasses");
                        anger = jsonEmotion.getDouble("anger");
                        happiness = jsonEmotion.getDouble("happiness");
                        fear = jsonEmotion.getDouble("fear");
                        neutral = jsonEmotion.getDouble("neutral");
                        sadness = jsonEmotion.getDouble("sadness");
                        surprise = jsonEmotion.getDouble("surprise");
                        baldness = jsonHair.getDouble("bald");
                        makeUp = jsonMakeUp.getBoolean("eyeMakeup") || jsonMakeUp.getBoolean("lipMakeUp");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(MainActivity.this, AnalyzePicture.class);
                                        startActivity(i);
                                        imageView.setImageResource(android.R.color.transparent);
                                    }
                                    }, 2000);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error", "Some Error");
                    System.out.println(e.getMessage());
                }
            }
        });
        thread.start();
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        MainActivity.gender = gender;
    }

    public static double getAge() {
        return age;
    }

    public static void setAge(double age) {
        MainActivity.age = age;
    }

    public static double getSmile() {
        return smile;
    }

    public static void setSmile(double smile) {
        MainActivity.smile = smile;
    }

    public static String getGlasses() {
        return glasses;
    }

    public static void setGlasses(String glasses) {
        MainActivity.glasses = glasses;
    }

    public static double getHappiness() {
        return happiness;
    }

    public static void setHappiness(double happiness) {
        MainActivity.happiness = happiness;
    }

    public static double getNeutral() {
        return neutral;
    }

    public static void setNeutral(double neutral) {
        MainActivity.neutral = neutral;
    }

    public static double getSadness() {
        return sadness;
    }

    public static void setSadness(double sadness) {
        MainActivity.sadness = sadness;
    }

    public static double getSurprise() {
        return surprise;
    }

    public static void setSurprise(double surprise) {
        MainActivity.surprise = surprise;
    }

    public static double getAnger() {
        return anger;
    }

    public static void setAnger(double anger) {
        MainActivity.anger = anger;
    }

    public static double getFear() {
        return fear;
    }

    public static void setFear(double fear) {
        MainActivity.fear = fear;
    }

    public static boolean isMakeUp() {
        return makeUp;
    }

    public static void setMakeUp(boolean makeUp) {
        MainActivity.makeUp = makeUp;
    }

    public static double getBaldness() {
        return baldness;
    }

    public static void setBaldness(double baldness) {
        MainActivity.baldness = baldness;
    }

    public static double getMoustache() {
        return moustache;
    }

    public static void setMoustache(double moustache) {
        MainActivity.moustache = moustache;
    }

    public static double getBeard() {
        return beard;
    }

    public static void setBeard(double beard) {
        MainActivity.beard = beard;
    }

}

