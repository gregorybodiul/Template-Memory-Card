package com.bgexample.memorycards;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_splash, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_gameFragment);
                    }
                },5000);
            }
        }).start();

        //new Thread(new Loading(R.id.action_splashFragment_to_gameFragment,5000)).start();
    }
    class Loading implements Runnable {
        private int actionId;
        private int sleep;

        public Loading(int actionId, int sleep){
            this.actionId = actionId;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            try { Thread.sleep(sleep); } catch (InterruptedException e) { e.printStackTrace();}
            new Handler(Looper.getMainLooper()).post(() -> Navigation.findNavController(view).navigate(actionId));
        }
    }
}