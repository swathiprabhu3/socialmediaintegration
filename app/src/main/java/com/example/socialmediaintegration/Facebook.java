package com.example.socialmediaintegration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Facebook extends AppCompatActivity {
    Button fblogout;
    TextView name,email;
    ImageView fpic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        fpic=findViewById(R.id.fbpic);
        fblogout=findViewById(R.id.fblogout);
        checkloginstatus();
        fblogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Facebook.this,MainActivity.class));
                finish();
            }
        });
    }
    AccessTokenTracker tokenTracker= new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null)
            {
                name.setText("");
                email.setText("");
                fpic.setImageResource(0);
                Toast.makeText(Facebook.this, "Logged out", Toast.LENGTH_SHORT).show();
            }
            else
            {
                loaduserprofile(currentAccessToken);
            }
        }
    };
    private void loaduserprofile(AccessToken newAccessToken)
    {
        GraphRequest graphrequest= GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try
                {
                    String first_name= object.getString("first_name");
                    String last_name= object.getString("last_name");
                    String emailid= object.getString("email");
                    String id= object.getString("id");
                    String imageurl= "https://graph.facebook.com/"+id+"/picture?type=normal";

                    name.setText(first_name+" "+last_name);
                    email.setText(emailid);
                    Picasso.get().load(imageurl).into(fpic);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters= new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        graphrequest.setParameters(parameters);
        graphrequest.executeAsync();
    }
    private void checkloginstatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loaduserprofile(AccessToken.getCurrentAccessToken());
        }
    }
}