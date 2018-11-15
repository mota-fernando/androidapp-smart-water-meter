package mota.fernando.hidrometrointeligente;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Preferencias extends Fragment {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference preferencias = databaseReference.child("Preferencias");
    Config config;
    Switch switchfa, switchia;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_preferencias, container, false);

        switchfa = (Switch) rootView.findViewById(R.id.switchfa);
        switchia = (Switch) rootView.findViewById(R.id.switchia);

        config = new Config();


        Button btnConfirmaSwitch = (Button)rootView.findViewById(R.id.btnConfirmaSwitch);
        btnConfirmaSwitch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                if(switchfa.isChecked())
                    config.setFluxoagua(1);
                else
                    config.setFluxoagua(0);
                if(switchia.isChecked())
                    config.setInteligenciaartificial(1);
                else
                    config.setInteligenciaartificial(0);
                preferencias.setValue(config);

            }
        });


       preferencias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                config = dataSnapshot.getValue(Config.class);
                if (config.getFluxoagua()==1)
                    switchfa.setChecked(true);
                else
                    switchfa.setChecked(false);

                if (config.getInteligenciaartificial()==1)
                    switchia.setChecked(true);
                else
                    switchia.setChecked(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

}
