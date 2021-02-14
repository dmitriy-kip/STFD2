package com.example.stfd.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stfd.R;
import com.example.stfd.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FirstScreenFragment extends Fragment {
    OnSelectedFirstScreenListener listener;
    List<String> modules = new ArrayList<>();

    public interface OnSelectedFirstScreenListener extends HistoryFragment.OnSelectedButtonListenerHistory{
        void goToSender(List<String> modules);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_first_screen, container, false);

        listener = (OnSelectedFirstScreenListener) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.welcome), 1);
        }

        Button start = rootView.findViewById(R.id.start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneEdit = rootView.findViewById(R.id.phone_number);
                String phone = "+7" + phoneEdit.getText().toString();

                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                RequestParams params = new RequestParams();
                params.put("phoneNumber", phone);
                client.post("http://prog-matik.ru:8086/api/auth?phone=9139000000",params,new TextHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONArray arr = new JSONArray(responseString);
                            JSONObject obj = arr.getJSONObject(0);
                            JSONArray arr2 = obj.getJSONArray("records");
                            JSONObject obj2 = arr2.getJSONObject(0);
                            JSONArray projects = obj2.getJSONArray("projects");
                            if (projects.length() != 0) {
                                for (int i = 0; i < projects.length(); i++) {
                                    JSONObject module = projects.getJSONObject(i);
                                    modules.add(module.getString("name"));
                                }
                                listener.goToSender(modules);
                            } else {
                                Toast.makeText(getActivity(), "Вы не зарегестрированны. Обратитесь к админестратору", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("ответ","все ок " + responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity(), "Не удалось подключится к серверу",Toast.LENGTH_LONG).show();
                        Log.e("ответ", "не ок " + responseString);
                    }

                });
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnSelectedFirstScreenListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
