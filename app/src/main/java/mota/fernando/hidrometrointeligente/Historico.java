package mota.fernando.hidrometrointeligente;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Historico extends Fragment {

    SimpleDateFormat dttm = new SimpleDateFormat("hh:mm:ss");
    SimpleDateFormat formatt = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a");
    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GraphView graph;
    BarGraphSeries series;
    Calendar date;
    Button brnDatepicker;
    int index = 0;
    long unixtime;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_historico, container, false);
      /*  TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER))); */

        brnDatepicker = (Button)rootView.findViewById(R.id.btnDatepicker);
        brnDatepicker.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                showDateTimePicker();
              //  date[index] = Calendar.getInstance();
               // if (index < 1) {
                 //   index = 1;

                    //formatt.format(date[index].getTime())


              //  }else {
                //    index = 0;

                   // long unixtime =  date[index].getTimeInMillis();
                   // Date date2 = formatt.parse(date[index]);


                }
                //Intent in = new Intent(getActivity(), Grafico.class);
                //in.putExtra("some", "some data");
                //startActivity(in);
           // }
            });

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

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isvaluex){
                dttm.setTimeZone(TimeZone.getTimeZone("GMT"));
                if (isvaluex)
                    return dttm.format(new Date((long) value));
                 else
                    return super.formatLabel(value,isvaluex);


                }
            });



        return rootView;


    }
    public void drawGraph(){
        databaseReference.orderByChild("xvalue").startAt(unixtime).addValueEventListener(new ValueEventListener() {
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
    }

    @Override

    public void onStart() {
        super.onStart();
        drawGraph();
    }
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                        jdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String date1 = (String)jdf.format(date.getTime());
                        Date date2 = null;
                        try {
                            date2 = jdf.parse(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        unixtime = date2.getTime();
                        drawGraph();
                        //formatt.format(date[index].getTime())
                        brnDatepicker.setText("Período inicial" + unixtime  +"date2 "+ date2);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
             //   brnDatepicker.setText(date[index].get(Calendar.DAY_OF_MONTH) + " " + date[index].get(Calendar.MONTH) + " " + date[index].get(Calendar.YEAR));

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

}
