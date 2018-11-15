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

    SimpleDateFormat dttm = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    SimpleDateFormat btnDt = new SimpleDateFormat("dd-MM-yyyy' HORA: 'HH:mm:ss");
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GraphView graph;
    BarGraphSeries series;
    Calendar date;
    Button brnDatepickerIni, brnDatepickerFim;
    int index = 0;
    long[] unixtime = new long[2];

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_historico, container, false);

        brnDatepickerIni = (Button)rootView.findViewById(R.id.btnDatepickerIni);
        brnDatepickerIni.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                showDateTimePicker(0);

                }

            });

        brnDatepickerFim = (Button)rootView.findViewById(R.id.btnDatepickerFim);
        brnDatepickerFim.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                showDateTimePicker(1);
            }
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
        databaseReference.orderByChild("xvalue").startAt(unixtime[0]).endAt(unixtime[1]).addValueEventListener(new ValueEventListener() {
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
    public void showDateTimePicker(final int index) {
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

                        unixtime[index] = date2.getTime();
                        drawGraph();
                        if (index == 0)
                             brnDatepickerIni.setText("INÍCIO: " + btnDt.format(date.getTime()));
                        else
                             brnDatepickerFim.setText("FIM: " + btnDt.format(date.getTime()));

                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();


            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

}
