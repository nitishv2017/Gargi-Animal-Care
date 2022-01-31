package com.example.gargianimalcare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.acl.Permission;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class All_complaintsReport extends AppCompatActivity {
    //0-newest  1-oldest
    int flag_sort=0;
    //0-pending  1-inprocess   2- completed   3-all_complaints
    int flag_filter=3;
    //0- alldates else customdate
    int flag_dates=0;
    AlertDialog dialog;
    String start="", end="";
    long startEpoch=0, endEpoch=-1;
    LinearLayout customDateRangeView;
    int start_edittext=0;
    ArrayList<complaintsHelperClass> tempArray;
    final Calendar myCalendar= Calendar.getInstance();
    EditText startDate, endDate;
    RadioGroup sort_group, dates_group, status_group;
    RadioButton new_btn, old_btn, allDateBtn, customDatebtn, pendingBtn, inProcessBtn, completedBtn, allComBtn;
    TextView emptyText;
    String[] permission= {READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==Activity.RESULT_OK)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager())
                    {
                        Toast.makeText(All_complaintsReport.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(All_complaintsReport.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    });
    String flagUser="";
    androidx.appcompat.widget.SearchView mSearchView;
    RecyclerView recyclerView;
    customerComplainAdapter adapter;
    ArrayList<complaintsHelperClass> array_complains;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, ref1;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    ValueEventListener valueEventListener;

    DrawerLayout drawerLayout;
    TextView actionBar_text;
    FloatingActionButton floatingActionButton;
    Button apply_btn, resetBtn;
    ImageView closeBtn;

    SharedPreferences sharedPref;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_complaints_report);
        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.loginEmailToolbar);
        setSupportActionBar(toolbar);

        emptyText=findViewById(R.id.emptyList_allc);

        if(!checkManageallfilePermission())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(All_complaintsReport.this);

            // Set the message show for the Alert time
            builder.setMessage("Please allow permission to save files on storage to Gargi Animal Care in your settings");

            // Set Alert Title
            builder.setTitle("Request Permission");

            // Set Cancelable false
            // for when the user clicks on the outside
            // the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the positive button with yes name
            // OnClickListener method is use of
            // DialogInterface interface.

            builder
                    .setPositiveButton(
                            "Yes",
                            new DialogInterface
                                    .OnClickListener() {


                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {

                                    requestPermission();

                                }
                            });

            // Set the Negative button with No name
            // OnClickListener method is use
            // of DialogInterface interface.
            builder
                    .setNegativeButton(
                            "No",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {

                                    // If user click no
                                    // then dialog box is canceled.
                                    dialog.cancel();
                                }
                            });

            // Create the Alert dialog
            android.app.AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();

        }

        navigationView=findViewById(R.id.nav_view_allc);
        // get or create SharedPreferences
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

       flagUser = sharedPref.getString("flagUser", "0");
        navigationView.getMenu().clear();
       if(flagUser.equals("0"))
       {
           navigationView.inflateMenu(R.menu.side_navigation_cust);
       }
       else
       {
           navigationView.inflateMenu(R.menu.side_navigation);
       }

        //initialize the objects
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/");

        drawerLayout=findViewById(R.id.drawer_layout_allc);
        mSearchView=findViewById(R.id.searchView_allc);



        actionBar_text=findViewById(R.id.actionBarText);
        actionBar_text.setText("All Complaints Report");

        progressBar=findViewById(R.id.progress_allc);

        recyclerView= findViewById(R.id.allComplaintsView_allc);



        floatingActionButton=findViewById(R.id.filter_allc);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an alert builder
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(All_complaintsReport.this);

                // set the custom layout
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.custom_filter_alert,
                                null);
                builder.setView(customLayout);
                closeBtn=customLayout.findViewById(R.id.closeBtn_filter);
                resetBtn=customLayout.findViewById(R.id.reset_btn_filter);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        recreate();
                    }
                });

                customDateRangeView=customLayout.findViewById(R.id.customDateRangeview);
                apply_btn=customLayout.findViewById(R.id.apply_filter);
                startDate=customLayout.findViewById(R.id.startDate);
                endDate=customLayout.findViewById(R.id.endDate);
                sort_group= customLayout.findViewById(R.id.sort_group);
                dates_group=customLayout.findViewById(R.id.dates_group);
                status_group=customLayout.findViewById(R.id.status_group);
                new_btn=customLayout.findViewById(R.id.newDate_sort);
                old_btn=customLayout.findViewById(R.id.oldDate_sort);
                allDateBtn=customLayout.findViewById(R.id.allDates);
                customDatebtn=customLayout.findViewById(R.id.customDate);
                pendingBtn=customLayout.findViewById(R.id.pending_filter);
                inProcessBtn=customLayout.findViewById(R.id.inprocess_filter);
                completedBtn=customLayout.findViewById(R.id.completed_filter);
                allComBtn=customLayout.findViewById(R.id.all_filter);

                if(flag_sort==1)
                {
                    old_btn.setChecked(true);
                }

                dates_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch(i)
                        {
                            case R.id.allDates:
                                customDateRangeView.setVisibility(View.GONE);
                                break;
                            case R.id.customDate:
                                customDateRangeView.setVisibility(View.VISIBLE);
                                break;

                        }
                    }
                });

                if(flag_dates==1)
                {
                    customDatebtn.setChecked(true);

                    startDate.setText(start);
                    endDate.setText(end);
                    //visibility on krna
                    customDateRangeView.setVisibility(View.VISIBLE);
                }

                switch (flag_filter)
                {
                    case 0: pendingBtn.setChecked(true); break;
                    case 1: inProcessBtn.setChecked(true); break;
                    case 2: completedBtn.setChecked(true); break;
                    case 3: allComBtn.setChecked(true);break;
                }

                datePickerinitialise();
                apply_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(new_btn.isChecked())
                        {
                            flag_sort=0;
                        }
                        else
                        {
                            flag_sort=1;
                        }

                        if(allDateBtn.isChecked())
                        {
                            flag_dates=0;
                        }
                        else
                        {
                            flag_dates=1;
                        }

                        if(pendingBtn.isChecked())
                        {
                            flag_filter=0;
                        }
                        else if(inProcessBtn.isChecked())
                        {
                            flag_filter=1;
                        }
                        else if(completedBtn.isChecked())
                        {
                            flag_filter=2;
                        }
                        else flag_filter=3;

                        switch (flag_sort)
                        {
                            case 0:
                                Collections.sort(array_complains, new Comparator<complaintsHelperClass>() {
                                    @Override
                                    public int compare(complaintsHelperClass t1, complaintsHelperClass t2) {
                                        long c=t2.getTimeOfComplain()-t1.getTimeOfComplain();
                                        if(c>0)
                                        {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                                break;
                            case 1:
                                Collections.sort(array_complains, new Comparator<complaintsHelperClass>() {
                                    @Override
                                    public int compare(complaintsHelperClass t1, complaintsHelperClass t2) {
                                        long c=t2.getTimeOfComplain()-t1.getTimeOfComplain();
                                        if(c>0)
                                        {
                                            return -1;
                                        }
                                        return 1;
                                    }
                                });
                                break;
                        }

                        if(flag_dates==1)
                        {
                             start= startDate.getText().toString();
                             end= endDate.getText().toString();

                            if(TextUtils.isEmpty(start) || TextUtils.isEmpty(end) )
                            {
                                Toast.makeText(All_complaintsReport.this, "Please select dates", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Date d1, d2;

                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                d1= simpleDateFormat.parse(start);
                                startEpoch=d1.getTime()/1000;
                                Log.i(TAG, "onClick: startEpoch: " + startEpoch);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }try {
                                d2= simpleDateFormat.parse(end);
                                endEpoch=(d2.getTime()+ TimeUnit.DAYS.toMillis(1))/1000;
                            Log.i(TAG, "onClick: endEpoch: " + endEpoch);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(endEpoch-startEpoch<0)
                            {
                                Toast.makeText(All_complaintsReport.this, "End date cannot be less than start date", Toast.LENGTH_SHORT).show();
                                return;
                            }




                        }

                        tempArray=new ArrayList<complaintsHelperClass>();
                        for(complaintsHelperClass data: array_complains)
                        {
                            Log.i(TAG, "onClick: timeOFComplain: "+data.getTimeOfComplain());
                            if((flag_filter==0 && data.getStatus().equals("pending") ) && ( flag_dates==0 || (flag_dates ==1 && data.getTimeOfComplain()>=startEpoch && data.getTimeOfComplain()<=endEpoch)))
                            {
                                tempArray.add(data);
                            }
                            else if((flag_filter==1 && data.getStatus().equals("in process")) && ( flag_dates==0 || (flag_dates ==1 && data.getTimeOfComplain()>=startEpoch && data.getTimeOfComplain()<=endEpoch)))
                            {
                                tempArray.add(data);
                            }
                            else if((flag_filter==2 && data.getStatus().equals("completed")) && ( flag_dates==0 || (flag_dates ==1 && data.getTimeOfComplain()>=startEpoch && data.getTimeOfComplain()<=endEpoch)))
                            {
                                tempArray.add(data);
                            }
                            else if((flag_filter==3) && ( flag_dates==0 || (flag_dates ==1 && data.getTimeOfComplain()>=startEpoch && data.getTimeOfComplain()<=endEpoch)))  tempArray.add(data);
                        }

                        dialog.cancel();
                        // Create adapter passing in the sample user data
                        customerComplainAdapter adapter = new customerComplainAdapter(tempArray,All_complaintsReport.this,1);
                        // Attach the adapter to the recyclerview to populate items
                        recyclerView.setAdapter(adapter);
                        if(tempArray.size()==0) {
                            emptyText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        else {
                            emptyText.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                    }
                });
                // create and show
                // the alert dialog
                dialog = builder.create();
                dialog.show();
            }
        });


        array_complains=new ArrayList<complaintsHelperClass>();
        generateList(new CustomerDatafetch() {
            @Override
            public void onAvailableCallback() {


                // Set layout manager to position the items
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                Collections.sort(array_complains, new Comparator<complaintsHelperClass>() {
                    @Override
                    public int compare(complaintsHelperClass t1, complaintsHelperClass t2) {
                        long c=t2.getTimeOfComplain()-t1.getTimeOfComplain();
                        if(c>0)
                        {
                            return 1;
                        }
                        return -1;
                    }
                });
                // Create adapter passing in the sample user data
                adapter = new customerComplainAdapter(array_complains,All_complaintsReport.this, 1);
                // Attach the adapter to the recyclerview to populate items
                tempArray=array_complains;
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if(tempArray.size()==0) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else {
                    emptyText.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        Query query= firebaseDatabase.getReference("complaints");
        query.addListenerForSingleValueEvent(valueEventListener);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                activityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction((Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                activityResultLauncher.launch(intent);
            }
        }
        else {
                ActivityCompat.requestPermissions(All_complaintsReport.this, permission, 30);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case 30:
                if(grantResults.length>0)
                {
                    boolean readper=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeper=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(readper && writeper)
                    {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "You Denied Permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean checkManageallfilePermission() {
        // If you have access to the external storage, do whatever you need
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();

        }
        else
        {
            int readcheck= ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writecheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return  readcheck== PackageManager.PERMISSION_GRANTED && writecheck== PackageManager.PERMISSION_GRANTED;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.allcomplains_menu, menu);

            return true;

    }
    private void killActivity() {
        finish();
    }

    public void  ClickedDrawer(View v)
    {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void  logout_navigate(MenuItem item)
    {
        FirebaseAuth.getInstance().signOut();
        Intent j = new Intent(getApplicationContext(), LoginViaPhone.class);
        startActivity(j);
        killActivity();
    }
    public void  home_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), Home.class);
        startActivity(j);
        killActivity();
    }
    public void  account_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), Account_details.class);
        startActivity(j);
        killActivity();

    }
    public void  allC_navigate(MenuItem item)
    {

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void allC_feedback(MenuItem item)
    {
        Intent j=new Intent(getApplicationContext(),Feedback.class);
        startActivity(j);
        killActivity();
    }

    public void  generatePDF(MenuItem item) throws IOException {
        ArrayList<complaintsHelperClass> data= tempArray;

        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        HSSFSheet hssfSheet=hssfWorkbook.createSheet();


        String[] label={"complainID",
            "Customer Name",
            "Phone Number",
            "Address",
            "Subject",
            "Description",
            "Time of Complain",
            "Status",
            "Employee Name",
            "Time of Completion",
            "Description of Solution",
            "Employee Phone"};

        HSSFRow hssfRow= hssfSheet.createRow(0);
        for(int i=0;i<label.length;i++) {
            HSSFCell hssfCell=hssfRow.createCell(i);
            hssfCell.setCellValue(label[i]);
        }


        for(int i=0;i<data.size();i++) {
             hssfRow= hssfSheet.createRow(i+1);

                 HSSFCell hssfCell=hssfRow.createCell(0);
                 hssfCell.setCellValue(data.get(i).getComplainID());

            hssfCell=hssfRow.createCell(1);
            hssfCell.setCellValue(data.get(i).getCustomerName());

            hssfCell=hssfRow.createCell(2);
            hssfCell.setCellValue(data.get(i).getPhoneNumber());

            hssfCell=hssfRow.createCell(3);
            hssfCell.setCellValue(data.get(i).getAddress());

            hssfCell=hssfRow.createCell(4);
            hssfCell.setCellValue(data.get(i).getSubject());

            hssfCell=hssfRow.createCell(5);
            hssfCell.setCellValue(data.get(i).getDescription());

            Date date = new Date ();
            date.setTime((long)data.get(i).getTimeOfComplain()*1000);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            hssfCell=hssfRow.createCell(6);
            hssfCell.setCellValue(dateFormat.format(date));

            hssfCell=hssfRow.createCell(7);
            hssfCell.setCellValue(data.get(i).getStatus());

            hssfCell=hssfRow.createCell(8);
            hssfCell.setCellValue(data.get(i).getEmployeeName());

            date = new Date ();
            date.setTime((long)data.get(i).getTimeofCompletion()*1000);
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            hssfCell=hssfRow.createCell(9);
            if(data.get(i).getTimeofCompletion()!=0)
            hssfCell.setCellValue(dateFormat.format(date));
            else hssfCell.setCellValue("null");

             hssfCell=hssfRow.createCell(10);
            hssfCell.setCellValue(data.get(i).getDescriptionofSolution());

             hssfCell=hssfRow.createCell(11);
            hssfCell.setCellValue(data.get(i).getEmployeePhone());

        }

        String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gargi Animal Care/Report/";
        File file = new File(directory_path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            }
            catch (Exception e)
            {
                Log.e("creating file error", e.toString());
            }
        }

        String targetPdf = directory_path+"Report.xls";
        File filePath = new File(targetPdf);


            FileOutputStream fileOutputStream=new FileOutputStream(filePath);
            try {
                hssfWorkbook.write(fileOutputStream);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            if(fileOutputStream!=null)
            {
                fileOutputStream.flush();
                fileOutputStream.close();

            }
        Toast.makeText(this, "Report generated", Toast.LENGTH_LONG).show();

//        // create a new document
//        PdfDocument document = new PdfDocument();
//        // crate a page description
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
//        // start a page
//        PdfDocument.Page page = document.startPage(pageInfo);
//        Canvas canvas = page.getCanvas();
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawCircle(50, 50, 30, paint);
//        paint.setColor(Color.BLACK);
//        canvas.drawText("sometext", 80, 50, paint);
//        //canvas.drawt
//        // finish the page
//        document.finishPage(page);
//// draw text on the graphics object of the page
//        // Create Page 2
//        pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 2).create();
//        page = document.startPage(pageInfo);
//        canvas = page.getCanvas();
//        paint = new Paint();
//        paint.setColor(Color.BLUE);
//        canvas.drawCircle(100, 100, 100, paint);
//        document.finishPage(page);
//        // write the document content
//        String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypdf/";
//        File file = new File(directory_path);
//        if (!file.exists()) {
//            try {
//                file.mkdirs();
//            }
//            catch (Exception e)
//            {
//                Log.e("creating file error", e.toString());
//            }
//        }
//
//        String targetPdf = directory_path+"test.pdf";
//        File filePath = new File(targetPdf);
//
//        try {
//            document.writeTo(new FileOutputStream(filePath));

//            Intent intent=new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(filePath);
//            intent.setDataAndType(uri, "application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            try {
//                startActivity(intent);
//            }
//            catch(ActivityNotFoundException e)
//            {
//                Toast.makeText(All_complaintsReport.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
//            }

//        } catch (IOException e) {
//            Log.e("main", "error "+e.toString());
//            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
//        }
//        // close the document
//        document.close();
    }

    private void generateList(CustomerDatafetch callback) {

        reference = firebaseDatabase.getReference("complaints");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for( DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        complaintsHelperClass c=dataSnapshot1.getValue(complaintsHelperClass.class);
                        array_complains.add(c);
                    }

                    callback.onAvailableCallback();
                }
                else
                    emptyText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(All_complaintsReport.this, "Please reopen app, error connecting database", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error getting database"); //Don't ignore potential errors!
            }
        };
//        reference.addListenerForSingleValueEvent(valueEventListener);

    }

    void datePickerinitialise()
    {


        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_edittext=1;
                new DatePickerDialog(All_complaintsReport.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_edittext=0;
                new DatePickerDialog(All_complaintsReport.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        if(start_edittext==1)
        startDate.setText(dateFormat.format(myCalendar.getTime()));
        else endDate.setText(dateFormat.format(myCalendar.getTime()));
    }
    boolean doubleBackToExitPressedOnce=false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}