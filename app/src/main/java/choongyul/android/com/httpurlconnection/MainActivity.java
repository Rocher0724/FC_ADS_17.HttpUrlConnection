package choongyul.android.com.httpurlconnection;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText editText;
    TextView textView;
    TextView textTitle;
    RelativeLayout progressLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textResult);
        textTitle = (TextView) findViewById(R.id.textTitle);
        progressLO = (RelativeLayout) findViewById(R.id.progressLO);

        button.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button:
                    String urlString = editText.getText().toString();
                    getUrl(urlString);

                    break;
            }
        }
    };

    public void getUrl(String urlString) {

        if(!urlString.startsWith("http")) {
            urlString = "http://" + urlString;
        }

        new AsyncTask<String,Void,String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressLO.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];

                try {
                    // 1. String 을 url 객체로 변환
                    URL url = new URL(urlString);
                    // 2. url으로 네트워크 연결 시작
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 3. url 연결에 대한 옵션 설정
                    connection.setRequestMethod("GET"); // 가. GET 데이터 요청시 사용하는 방식
                    // 나. POST 데이터 입력시 사용하는 방식
                    // 4. 서버로 부터 응답코드 회신
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 4.1 서버 연결로부터 스트림을 얻고 버퍼래퍼로 감싼다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // 4.2 반복문은 돌면서 버퍼의 데이터를 읽어온다.
                        StringBuilder result = new StringBuilder();
                        String lineOfData = "";
                        while ((lineOfData = br.readLine()) != null) {
                            result.append(lineOfData);
                        }
                        connection.disconnect();
                        return result.toString();

                    } else {
                        Log.e("HTTOConnection", "Error Code = " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;

            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                textView.setText(result);

                String title = setTitle(result);

                textTitle.setText(title);


            }

        }.execute(urlString);


    }

    public String setTitle(String result) {
        int startIndex = 0;
        int endIndex = 0;
        String title = "";
        if(result != null) {
            startIndex = result.indexOf("<title>");
            endIndex = result.indexOf("</title>");
            //String substrnig(int start, int end) : 현재 문자열 객체에서 start 부터 end 직전까지 문자열 발췌

            title = result.substring(startIndex + 7, endIndex);
            progressLO.setVisibility(View.GONE);
        }
        return title;
    }

}
