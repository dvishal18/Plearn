package dvishal.com.eatlab.finalplearn;


import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.support.v7.appcompat.R.id.chronometer;
import static android.support.v7.appcompat.R.id.search_close_btn;
import static android.support.v7.appcompat.R.id.top;

public class WelcomeActivity extends AppCompatActivity {

    List<HashMap<String, String>> ch1List;
   SimpleAdapter simpleAdapter;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    private static final int REQUEST_WRITE_PERMISSION = 100;
    private static  final  int REQUEST_WRITE_STORAGE = 200;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    RelativeLayout relativeLayout;
    ImageView Ch_Board;
    TextView ch_Score,topicname;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler bluetoothIn,handler;
    int Seconds, Minutes, MilliSeconds;
    private String chapter1Address ="";
    boolean isPlaying = false;
    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    Button score,letsGo;
    RelativeLayout rl_Chapters;
    private ConnectedThread mConnectedThread;
    String info;
    String addressDevice;
    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    ImageView next,prev,btn_start,btn_pause;
    EditText inputName,in;
    int Totalch1=0,Totalch2=0,Totalch3=0,Totalch4=0,Totalch5=0;
    int[] maxScore = {25,50,23,23,14};
    ListView androidListView;
    String writeMessage="";
    Chronometer timer;
    long stopTime = 0;

    StringBuffer ardunioMsg = new StringBuffer(writeMessage) ;

      // Array of strings for ListView Title
    String[] listviewTitle = new String[]{
            "Yellow", "Green", "Pink", "Blue"};


    int[] listviewImage = new int[]{
            R.drawable.box_yellow, R.drawable.box_green, R.drawable.box_pink, R.drawable.box_blue,};
    
    String[] listViewValues = new String[]{"0","0","0","0"};



    Dialog dialog;
    TextView showBtn,cancelBtn;
    ImageView imageView;
    Button imageViewReset;
    StringBuffer stringBuffer;
    Toast boxEvent;
    String boxMessage="";
    String str_SaveData;
    private int STORAGE_PERMISSION_CODE = 23;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);

        btnNext = (Button) findViewById(R.id.btn_next);

        topicname = (TextView)findViewById(R.id.board_txt_topic);
        btn_start = (ImageView)findViewById(R.id.board_btn_play);
        ch_Score = (TextView)findViewById(R.id.txt_score);
        btn_pause = (ImageView)findViewById(R.id.board_btn_pause);
        btn_pause.setVisibility(View.INVISIBLE);
        timer = (Chronometer)findViewById(R.id.board_txt_clock);
        Ch_Board = (ImageView)findViewById(R.id.ch_board);
        relativeLayout = (RelativeLayout)findViewById(R.id.board_content_control);
        androidListView = (ListView) findViewById(R.id.list_view);
        score = (Button)findViewById(R.id.board_btn_score);
        letsGo = (Button)findViewById(R.id.btLestGo);
        imageViewReset = (Button)findViewById(R.id.board_btn_reset);
        rl_Chapters = (RelativeLayout)findViewById(R.id.rel_Chapters);
        rl_Chapters.setVisibility(View.GONE);
        stringBuffer = new StringBuffer(boxMessage);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letsGo.setVisibility(View.GONE);
                rl_Chapters.setVisibility(View.VISIBLE);
             //   onPause();
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
            }
        });


        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }




        //CREATE DIALOG
        createDialog();

        //SHOW BTN CLIKCED
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(WelcomeActivity.this,"Well Done!",Toast.LENGTH_SHORT).show();


                String getAllEvent = stringBuffer.toString();
                ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                String timeInfo = "Time Taken : "+timer.getText().toString();
                String lessonInfo = "Lesson Number : "+ topicname.getText().toString();
                String nameInfo = "Group/Student Name : "+inputName.getText().toString();
                String newLine = "\n\n\t\t";
                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Plearn");
                folder.mkdirs();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                String timeStamp = dateFormat.format(new Date());
                StringBuffer newStingBuffer = new StringBuffer(timeStamp);
                newStingBuffer.append("_");
                newStingBuffer.append(topicname.getText().toString());
                newStingBuffer.append("_");
                newStingBuffer.append(inputName.getText().toString());
                String newFileName = newStingBuffer.toString();
                //String Filename = timeStamp+nameInfo;
                File myFile = new File(folder, newFileName+".txt");     // Filename
                writeData(myFile, timeInfo,lessonInfo,nameInfo,getAllEvent,newLine);

                newStingBuffer.delete(0,newStingBuffer.length());

                resetAll();
             //   onPause();
                stringBuffer.delete(0,stringBuffer.length());
                dialog.dismiss();
            }
        });
        //CANCEL
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
               // onPause();
                dialog.dismiss();
            }
        });





        imageViewReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuffer.delete(0,stringBuffer.length());
                    resetScore();



            }
        });

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(topicname.getText().equals("LESSON-1"))
                {
                    Toast.makeText(getApplicationContext(),"Current Score is : "+Totalch1,Toast.LENGTH_SHORT).show();
                }
                if(topicname.getText().equals("LESSON-2"))
                {
                    Toast.makeText(getApplicationContext(),"Current Score is : "+Totalch2,Toast.LENGTH_SHORT).show();
                }
                if(topicname.getText().equals("LESSON-3"))
                {
                    Toast.makeText(getApplicationContext(),"Current Score is : "+Totalch3,Toast.LENGTH_SHORT).show();
                }
                if(topicname.getText().equals("Lesson-4"))
                {
                    Toast.makeText(getApplicationContext(),"Current Score is : "+Totalch4,Toast.LENGTH_SHORT).show();
                }
                if(topicname.getText().equals("LESSON-5"))
                {
                    Toast.makeText(getApplicationContext(),"Current Score is : "+Totalch5,Toast.LENGTH_SHORT).show();
                }


            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want

                    writeMessage = (String) msg.obj;
                  //  ardunioMsg = msg.obj;
                    Toast.makeText(getApplicationContext()," MSG : "+writeMessage,Toast.LENGTH_SHORT).show();


                    if (topicname.getText().toString().equals("LESSON-1")) {
                        chapter1Logic();
                        ch1Method();

                    }
                    if (topicname.getText().toString().equals("LESSON-2")) {
                        chapter2Logic();
                        ch2Method();
                    }
                    if (topicname.getText().toString().equals("LESSON-3")) {
                        chapter3Logic();
                        ch3Method();

                    }
                    if (topicname.getText().toString().equals("LESSON-4")) {
                        chapter4Logic();
                        ch4Method();
                    }
                    if (topicname.getText().toString().equals("LESSON-5")) {
                        chapter5Logic();
                        ch5Method();
                    }




                }

            }
        };
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                timer.setBase(SystemClock.elapsedRealtime() + stopTime);
                timer.start();
                stringBuffer.delete(0, stringBuffer.length());
                imageViewReset.setEnabled(false);
                btn_pause.setVisibility(View.VISIBLE);
                btn_start.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
                btnSkip.setVisibility(View.INVISIBLE);
                viewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;

                    }
                });

                onResume();
                mConnectedThread.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Connection Turn On", Toast.LENGTH_SHORT).show();


            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Connection Turn Off", Toast.LENGTH_SHORT).show();
             //   onPause();

                stopTime = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();
                imageViewReset.setEnabled(true);
                btn_start.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.GONE);
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);

            }
        });



        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();





        handler = new Handler();



        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.startgamelayout,
                R.layout.activity_chapter1,
                R.layout.activity_chapter2,
                R.layout.activity_chapter3,
                R.layout.activity_chapter4,
                R.layout.activity_chapter5};




        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.post(new Runnable() {
        @Override
        public void run() {
        viewPagerPageChangeListener.onPageSelected(viewPager.getCurrentItem());
    }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }

            }
        });



    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;


    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        //declare key
        Boolean first = true;
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if(position == layouts.length-5)
            {
                ch1Method();
                topicname.setText(R.string.lesson1);
                btnSkip.setVisibility(View.GONE);
                timer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                timer.stop();
            }else
            {
                btnSkip.setVisibility(View.VISIBLE);
            }
            if(position == layouts.length-4)
            {
                topicname.setText(R.string.lesson2);
                ch2Method();
                btnSkip.setVisibility(View.VISIBLE);
                timer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                timer.stop();
            }
            if(position == layouts.length-3)
            {
                topicname.setText(R.string.lesson3);
                ch3Method();
                btnSkip.setVisibility(View.VISIBLE);
                timer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                timer.stop();

            }
            if(position == layouts.length-2)
            {
                topicname.setText(R.string.lesson4);
                ch4Method();
                btnSkip.setVisibility(View.VISIBLE);

                timer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                timer.stop();
            }
            if(position == layouts.length-1)
            {
                topicname.setText(R.string.lesson5);
                ch5Method();
                btnNext.setVisibility(View.GONE);
                timer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                timer.stop();
            }else
            {
                btnNext.setVisibility(View.VISIBLE);
            }

            if(position == layouts.length-6)
            {
                rl_Chapters.setVisibility(View.GONE);
                letsGo.setVisibility(View.VISIBLE);
            }else
            {
                rl_Chapters.setVisibility(View.VISIBLE);
                letsGo.setVisibility(View.GONE);
            }

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (first && positionOffset == 0 && positionOffsetPixels == 0){
                onPageSelected(0);
                first = false;
            }

        }

        @Override
        public void onPageScrollStateChanged(int position) {




        }
    };



    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }


        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceList.EXTRA_DEVICE_ADDRESS);
        chapter1Address = address;
        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
       // bluetoothIn = new Handler();
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this

        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {


        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {


            // Keep looping to listen for received messages
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    int bytes = mmInStream.read(buffer);

                    //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                //    bluetoothIn.removeMessages(handlerState,readMessage);
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);//write bytes over BT connection via outstream

            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }


    public void ch1Method()
    {

        ListViewDetail();
      //  timer.setText("00:00");

        if (Totalch1==maxScore[0]) {
            dialog.show();
            ch_Score.setText(Totalch1+"/"+maxScore[0]);
            Ch_Board.setBackgroundResource(R.drawable.ch1_colour);
            stopTime = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            imageView.setBackgroundResource(R.drawable.ch1_colour);
            aftrScoreLevel();

        }else
        {

            ch_Score.setText("-/"+maxScore[0]);
            Ch_Board.setBackgroundResource(R.drawable.ch1_bw);
        }
    }

    public void ch2Method()
    {
        ListViewDetail();
       // timer.setText("00:00");

        if (Totalch2==maxScore[1]) {
            stopTime = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            aftrScoreLevel();
            dialog.show();
            ch_Score.setText(Totalch2+"/"+maxScore[1]);
            Ch_Board.setBackgroundResource(R.drawable.ch2_colour);
            imageView.setBackgroundResource(R.drawable.ch2_colour);

        }else
        {

            ch_Score.setText("-/"+maxScore[1]);
            Ch_Board.setBackgroundResource(R.drawable.ch2_bw);
        }
    }

    public void ch3Method()
    {
        ListViewDetail();
       // timer.setText("00:00");

        if (Totalch3==maxScore[2]) {

            dialog.show();
            stopTime = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            ch_Score.setText(Totalch3+"/"+maxScore[2]);
            Ch_Board.setBackgroundResource(R.drawable.ch3_colour);
            imageView.setBackgroundResource(R.drawable.ch3_colour);
            aftrScoreLevel();
        }else
        {
            ch_Score.setText("-/"+maxScore[2]);
            Ch_Board.setBackgroundResource(R.drawable.ch3_bw);
        }
    }
    public void ch4Method()
    {

       // timer.setText("00:00");
        ListViewDetail();

        if (Totalch4==maxScore[3]) {

            dialog.show();
            stopTime = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            ch_Score.setText(Totalch4+"/"+maxScore[3]);
            Ch_Board.setBackgroundResource(R.drawable.ch4_colour);
            imageView.setBackgroundResource(R.drawable.ch4_colour);
            aftrScoreLevel();

        }else
        {
            ch_Score.setText("-/"+maxScore[3]);
            Ch_Board.setBackgroundResource(R.drawable.ch4_bw);
        }
    }
    public void ch5Method()
    {

        ListViewDetail();
       // timer.setText("00:00");
        if (Totalch5==maxScore[4]) {
            dialog.show();
            aftrScoreLevel();
            stopTime = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            ch_Score.setText(Totalch5+"/"+maxScore[4]);
            Ch_Board.setBackgroundResource(R.drawable.ch5_colour);
            imageView.setBackgroundResource(R.drawable.ch5_colour);

        }else
        {
            ch_Score.setText("-/"+maxScore[4]);
            Ch_Board.setBackgroundResource(R.drawable.ch5_bw);
        }
    }


    public void chapter1Logic()
    {
              // Toast.makeText(getApplicationContext(),"Lesson 2 Selected",Toast.LENGTH_SHORT).show();
              int pink = 3, yellow = 11, green = 1, blue = 5;

              if (writeMessage.startsWith("*1") || writeMessage.startsWith("**1")) {
                  if (writeMessage.endsWith("1*")) {

                      Toast.makeText(getBaseContext(), "Yellow Placed", Toast.LENGTH_SHORT).show();

                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Yellow Plcaed \n");
                      int val = Integer.parseInt(listViewValues[0]);
                      val = val + 1;
                      listViewValues[0] = String.valueOf(val);
                      Totalch1 = Totalch1 + yellow;
                      ch1Method();
                  }
                  if (writeMessage.endsWith("2*")) {
                      Toast.makeText(getBaseContext(), "Green Placed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Green Plcaed \n");
                      int val = Integer.parseInt(listViewValues[1]);
                      val = val + 1;
                      listViewValues[1] = String.valueOf(val);
                      Totalch1 = Totalch1 + green;
                      ch1Method();
                  }

                  if (writeMessage.endsWith("3*")) {
                      Toast.makeText(getBaseContext(), "Pink Placed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Pink Plcaed \n");
                      int val = Integer.parseInt(listViewValues[2]);
                      val = val + 1;
                      listViewValues[2] = String.valueOf(val);
                      Totalch1 = Totalch1 + pink;
                      ch1Method();
                  }
                  if (writeMessage.endsWith("4*")) {
                      Toast.makeText(getBaseContext(), "Blue Placed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());

                      stringBuffer.append("\n\t\t" + timeStamp + " : Blue Plcaed \n");
                      int val = Integer.parseInt(listViewValues[3]);
                      val = val + 1;
                      listViewValues[3] = String.valueOf(val);
                      Totalch1 = Totalch1 + blue;
                      ch1Method();
                  }


//                ch_Score.setText(Totalch1 + "/"+maxScore[0]);


              } else if (writeMessage.startsWith("*0") || writeMessage.startsWith("**0")) {
                  if (writeMessage.endsWith("1*")) {
                      Toast.makeText(getBaseContext(), "Yellow Removed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Yellow Removed \n");
                      int val = Integer.parseInt(listViewValues[0]);
                      val = val - 1;
                      listViewValues[0] = String.valueOf(val);
                      Totalch1 = Totalch1 - yellow;
                      ch1Method();
                  }
                  if (writeMessage.endsWith("2*")) {
                      Toast.makeText(getBaseContext(), "Green Removed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Green Removed \n");
                      int val = Integer.parseInt(listViewValues[1]);
                      val = val - 1;
                      listViewValues[1] = String.valueOf(val);
                      Totalch1 = Totalch1 - green;
                      ch1Method();
                  }
                  if (writeMessage.endsWith("3*")) {
                      Toast.makeText(getBaseContext(), "Pink Removed", Toast.LENGTH_SHORT).show();
                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Pink Removed \n");

                      int val = Integer.parseInt(listViewValues[2]);
                      val = val - 1;
                      listViewValues[2] = String.valueOf(val);
                      Totalch1 = Totalch1 - pink;
                      ch1Method();
                  }
                  if (writeMessage.endsWith("4*")) {
                      Toast.makeText(getBaseContext(), "Blue Removed", Toast.LENGTH_SHORT).show();

                      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                      String timeStamp = dateFormat.format(new Date());
                      stringBuffer.append("\n\t\t" + timeStamp + " : Blue Removed \n");
                      Totalch1 = Totalch1 - blue;
                      int val = Integer.parseInt(listViewValues[3]);
                      val = val - 1;
                      listViewValues[3] = String.valueOf(val);
                      ch1Method();
                  }


                  //        ch_Score.setText(Totalch1 + "/"+maxScore[0]);

              }


    }

    public void chapter2Logic()
    {
            int pink=7,yellow=5,Green=17,blue=11;

            if (writeMessage.startsWith("*1") || writeMessage.startsWith("**1")) {
                if (writeMessage.endsWith("1*") ) {

                    Toast.makeText(getApplicationContext(), "Yellow Placed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Plcaed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val+1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch2 = Totalch2 + yellow;
                    ch2Method();
                }
                if (writeMessage.endsWith("2*")) {
                    Toast.makeText(getBaseContext(), "Green Placed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Plcaed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val+1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch2 = Totalch2 + Green;
                    ch2Method();
                }

                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Plcaed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val+1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch2 = Totalch2 + pink;
                    ch2Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Placed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Plcaed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val+1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch2 = Totalch2 + blue;
                    ch2Method();
                }


//               ch_Score.setText(Totalch2 + "/"+maxScore[1]);



            } else if (writeMessage.startsWith("*0") || writeMessage.startsWith("**0")) {
                if (writeMessage.endsWith("1*") ) {
                    Toast.makeText(getBaseContext(), "Yellow Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val-1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch2 = Totalch2 - yellow;
                    ch2Method();
                }
                if (writeMessage.endsWith("2*") ) {
                    Toast.makeText(getBaseContext(), "Green Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Removed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val-1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch2 = Totalch2 - Green;
                    ch2Method();
                }
                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Removed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val-1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch2 = Totalch2 - pink;
                    ch2Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Removed \n");
                    Totalch2 = Totalch2 - blue;
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val-1;
                    listViewValues[3]=String.valueOf(val);
                    ch2Method();
                }


                ch_Score.setText(Totalch2 + "/"+maxScore[1]);

            }


    }

    public void chapter3Logic()
    {

            int pink=2,yellow=1,Green=3,blue=11;

            if (writeMessage.startsWith("*1") || writeMessage.startsWith("**1")) {
                if (writeMessage.endsWith("1*") ) {

                    Toast.makeText(getBaseContext(), "Yellow Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Placed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val+1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch3 = Totalch3 + yellow;
                    ch3Method();
                }
                if (writeMessage.endsWith("2*")) {
                    Toast.makeText(getBaseContext(), "Green Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Placed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val+1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch3 = Totalch3 + Green;
                    ch3Method();
                }

                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Placed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val+1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch3 = Totalch3 + pink;
                    ch3Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Placed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val+1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch3 = Totalch3 + blue;
                    ch3Method();
                }


//                ch_Score.setText(Totalch3 + "/"+maxScore[2]);



            } else if (writeMessage.startsWith("*0")|| writeMessage.startsWith("**0")) {
                if (writeMessage.endsWith("1*") ) {
                    Toast.makeText(getBaseContext(), "Yellow Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val-1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch3 = Totalch3 - yellow;
                    ch3Method();
                }
                if (writeMessage.endsWith("2*") ) {
                    Toast.makeText(getBaseContext(), "Green Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Removed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val-1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch3 = Totalch3 - Green;
                    ch3Method();
                }
                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Removed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val-1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch3 = Totalch3 - pink;
                    ch3Method();                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Removed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val-1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch3 = Totalch3 - blue;
                    ch3Method();
                }


       //         ch_Score.setText(Totalch3 + "/"+maxScore[2]);

            }


    }

    public void chapter4Logic()
    {

            int pink=11,yellow=2,Green=3,blue=5;

            if (writeMessage.startsWith("*1")|| writeMessage.startsWith("**1")) {
                if (writeMessage.endsWith("1*") ) {

                    Toast.makeText(getBaseContext(), "Yellow Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Placed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val+1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch4 = Totalch4 + yellow;
                    ch4Method();
                }
                if (writeMessage.endsWith("2*")) {
                    Toast.makeText(getBaseContext(), "Green Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Placed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val+1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch4 = Totalch4 + Green;
                    ch4Method();
                }

                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Placed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val+1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch4 = Totalch4 + pink;
                    ch4Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Placed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val+1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch4 = Totalch4 + blue;
                    ch4Method();
                }


       //         ch_Score.setText(Totalch4 + "/"+maxScore[3]);



            } else if (writeMessage.startsWith("*0")|| writeMessage.startsWith("**0")) {
                if (writeMessage.endsWith("1*") ) {
                    Toast.makeText(getBaseContext(), "Yellow Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val-1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch4 = Totalch4 - yellow;
                    ch4Method();                }
                if (writeMessage.endsWith("2*") ) {
                    Toast.makeText(getBaseContext(), "Green Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Removed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val-1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch4 = Totalch4 - Green;
                    ch4Method();
                }
                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Removed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val-1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch4 = Totalch4 - pink;
                    ch4Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Removed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val-1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch4 = Totalch4 - blue;
                    ch4Method();
                }


    //            ch_Score.setText(Totalch4 + "/"+maxScore[3]);

            }



    }

    public void chapter5Logic()
    {

            int pink=5,yellow=2,Green=7,blue=3;

            if (writeMessage.startsWith("*1") || writeMessage.startsWith("**1")) {
                if (writeMessage.endsWith("1*") ) {

                    Toast.makeText(getBaseContext(), "Yellow Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Placed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val+1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch5 = Totalch5 + yellow;
                    ch5Method();
                }
                if (writeMessage.endsWith("2*")) {
                    Toast.makeText(getBaseContext(), "Green Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Placed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val+1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch5 = Totalch5 + Green;
                    ch5Method();
                }

                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Placed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val+1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch5 = Totalch5 + pink;
                    ch5Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Placed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Placed \n");
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val+1;
                    listViewValues[3]=String.valueOf(val);
                    Totalch5 = Totalch5 + blue;
                    ch5Method();
                }


     //           ch_Score.setText(Totalch5 + "/"+maxScore[4]);



            } else if (writeMessage.startsWith("*0")|| writeMessage.startsWith("**0")) {
                if (writeMessage.endsWith("1*") ) {
                    Toast.makeText(getBaseContext(), "Yellow Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
                    int val=Integer.parseInt(listViewValues[0]);
                    val=val-1;
                    listViewValues[0]=String.valueOf(val);
                    Totalch5 = Totalch5 - yellow;
                    ch5Method();                }
                if (writeMessage.endsWith("2*") ) {
                    Toast.makeText(getBaseContext(), "Green Removed", Toast.LENGTH_SHORT).show();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Green Removed \n");
                    int val=Integer.parseInt(listViewValues[1]);
                    val=val-1;
                    listViewValues[1]=String.valueOf(val);
                    Totalch5 = Totalch5 - Green;
                    ch5Method();                }
                if (writeMessage.endsWith("3*")) {
                    Toast.makeText(getBaseContext(), "Pink Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Pink Removed \n");
                    int val=Integer.parseInt(listViewValues[2]);
                    val=val-1;
                    listViewValues[2]=String.valueOf(val);
                    Totalch5 = Totalch5 - pink;
                    ch5Method();
                }
                if (writeMessage.endsWith("4*")) {
                    Toast.makeText(getBaseContext(), "Blue Removed", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    stringBuffer.append("\n\t\t"+timeStamp+" : Blue Removed \n");
                    Totalch5 = Totalch5 - blue;
                    int val=Integer.parseInt(listViewValues[3]);
                    val=val-1;
                    listViewValues[3]=String.valueOf(val);
                    ch5Method();
                }


//                ch_Score.setText(Totalch5 + "/"+maxScore[4]);

            }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // The result of the popup opened with the requestPermissions() method
        // is in that method, you need to check that your application comes here
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // write
            }
        }
    }


public  void resetAll()
{


    listViewValues[0]="0";
    listViewValues[1]="0";
    listViewValues[2]="0";
    listViewValues[3]="0";
    simpleAdapter.notifyDataSetChanged();
    Totalch4=0;
    Totalch5=0;
    Totalch3=0;
    Totalch2=0;
    Totalch1=0;
    if(topicname.getText().equals("LESSON-1")) {
        ch1Method();
    }
    if(topicname.getText().equals("LESSON-2")) {
        ch2Method();
    }
    if(topicname.getText().equals("LESSON-3")) {
        ch3Method();
    }
    if(topicname.getText().equals("LESSON-4")) {
        ch4Method();
    }
    if(topicname.getText().equals("LESSON-5")) {
        ch5Method();
    }

    timer.setBase(SystemClock.elapsedRealtime());
    stopTime = 0;
    timer.stop();
    btn_pause.setVisibility(View.GONE);
    btn_start.setVisibility(View.VISIBLE);
    btnNext.setVisibility(View.VISIBLE);
    btnSkip.setVisibility(View.VISIBLE);
    mConnectedThread.write("0");    // Send "0" via Bluetooth
   // onPause();
}

    public void resetScore()
    {


            AlertDialog.Builder builder = new AlertDialog.Builder(
                    WelcomeActivity.this);
            builder.setTitle("Reset Alert");
            builder.setMessage("Do you want to Reset All the things?");
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            return ;
                        }
                    });
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {


                            resetAll();
                            ListViewDetail();
                            mConnectedThread.write("0");    // Send "0" via Bluetooth
                         //   onPause();



                        }

                    });
            builder.show();
        }





    private void createDialog()
    {
        dialog=new Dialog(this);
        //SET TITLE
        dialog.setTitle("Player");
        //set content
        dialog.setContentView(R.layout.custom_dialog);

        showBtn= (TextView) dialog.findViewById(R.id.showTxt);
        cancelBtn= (TextView) dialog.findViewById(R.id.cancelTxt);
        imageView = (ImageView)dialog.findViewById(R.id.imageView);
        inputName = (EditText)dialog.findViewById(R.id.groupName);


    }


    public void ListViewDetail()
    {
        ch1List = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 4; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listViewValues[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            ch1List.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        simpleAdapter = new SimpleAdapter(getBaseContext(), ch1List, R.layout.listview_activity, from, to);

        androidListView.setAdapter(simpleAdapter);

        simpleAdapter.notifyDataSetChanged();




    }

    private void writeData(File myFile, String dataLesson, String dataTime,String dataName,String getAllEvent,String newLine) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myFile);
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.write(dataTime.getBytes());
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.write(dataName.getBytes());
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.write(dataLesson.getBytes());
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.write(getAllEvent.getBytes());
            Toast.makeText(this, "Done" + myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void aftrScoreLevel()
    {
        imageViewReset.setEnabled(true);
        btn_pause.setVisibility(View.GONE);
        btn_start.setVisibility(View.VISIBLE);
    }

    /*public void boxYellowRemoveTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
    }
    public void boxYellowPlaceTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Placed \n");
    }

    public void boxGreenRemoveTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Green Removed \n");
    }
    public void boxGreenPlaceTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Green Placed \n");
    }
    public void boxPinkRemoveTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Pink Removed \n");
    }
    public void boxPinkPlaceTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Pink Placed \n");
    }
    public void boxBlueRemoveTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Yellow Removed \n");
    }
    public void boxBluePlaceTimestamp()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        stringBuffer.append("\n\t\t"+timeStamp+" : Blue Placed \n");
    }*/


}
