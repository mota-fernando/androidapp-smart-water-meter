package mota.fernando.hidrometrointeligente;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

public class Historico extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GraphView graph;
    BarGraphSeries series;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_historico, container, false);
      /*  TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER))); */

        /*Button btnAbrirGrafico = (Button)rootView.findViewById(R.id.btnAbrirGrafico);
        btnAbrirGrafico.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), Grafico.class);
                in.putExtra("some", "some data");
                startActivity(in);
            }
            });*/

        graph = (GraphView) rootView.findViewById(R.id.graph);
        series = new BarGraphSeries();
        graph.addSeries(series);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
         series.setValuesOnTopSize(20);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Consumo");

        databaseReference.orderByChild("xvalue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index = 0;

                for (DataSnapshot m : dataSnapshot.getChildren()) {

                    Consumo valor = m.getValue(Consumo.class);


                    dp[index] = new DataPoint(valor.getxvalue(), valor.getyvalue());
                    index++;
                }
                series.resetData(dp);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;


    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
