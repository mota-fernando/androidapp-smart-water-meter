package mota.fernando.hidrometrointeligente;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.anastr.speedviewlib.SpeedView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.*;


public class Monitoramento extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Query consumoquery = databaseReference.child("Consumo").orderByChild("xvalue").limitToLast(1);
    Handler mHandler;
    SpeedView speedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_monitoramento, container, false);

        this.mHandler = new Handler();
        m_Runnable.run();

        speedView = (SpeedView) rootView.findViewById(R.id.speedView);
        speedView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speedView.getUnit() == "m³") {
                    speedView.speedTo(speedView.getSpeed());
                    speedView.setUnit("l/m");
                }else{
                    speedView.speedTo(speedView.getSpeed());
                    speedView.setUnit("m³");
                }
            }
        });
        return rootView;
    }

    public void rMonitor() {

        consumoquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren())
                {

                        Consumo consumo = child.getValue(Consumo.class);
                        drawtext(consumo.getyvalue());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }

        });
    }
    public void drawtext (int v) {


        speedView.setMaxSpeed(320);

        speedView.speedTo(v);

    }

    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            rMonitor();
            mHandler.postDelayed(m_Runnable, 5000);
        }
    };
}