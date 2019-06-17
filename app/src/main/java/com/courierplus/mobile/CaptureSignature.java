package com.courierplus.mobile;

/**
 * Created by iabdullahi on 6/25/2016.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import db.DataDB;

public class CaptureSignature extends Activity {

    LinearLayout mContent;
    signature mSignature;
    ImageButton mClear, mGetSign;
   // private ImageView imageView,imageView2;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    // radio buttons
    private RadioGroup ratingRadio;

    DataDB db;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signature);

        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mClear = (ImageButton)findViewById(R.id.clear);
        mGetSign = (ImageButton)findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mView = mContent;
        db=new DataDB();
//        imageView = (ImageView) findViewById(R.id.imageView);
//        imageView2 = (ImageView) findViewById(R.id.imageView2);

        // for rating
        ratingRadio =  (RadioGroup) findViewById(R.id.ratingRadioGroup);

        ratingRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                if(checkedId == R.id.radio_good) {
                    Global.globalPodRemarks = "Good";
                } else if(checkedId == R.id.radio_fair) {
                    Global.globalPodRemarks = "Fair";
                } else if(checkedId == R.id.radio_bad){
                    Global.globalPodRemarks = "Bad";
                }
            }

        });


        mClear.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
               // Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(CaptureSignature.this);
                saveDialog.setTitle("Save signature");
                saveDialog.setMessage("Save signature?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //save drawing
                        //prepare directory
                        if(Global.globalPodRemarks.toString().equals("") || Global.globalPodRemarks.toString().equals(" ")) {
                            Global.AssetDialog("Please, kindly rate our service!!!", CaptureSignature.this).create().show();
                        }else{

                            tempDir = Environment.getExternalStorageDirectory() + "/app_signatures/";
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("app_signatures", Context.MODE_PRIVATE);

                        prepareDirectory();
                        current = Global.globalPodAwbno.toString() + ".png";

                        mypath= new File(directory,current);
                        //Log.v("log_tag", "Panel Saved");

                            mView.setDrawingCacheEnabled(true);

                            mSignature.save(mView);
                            mView.destroyDrawingCache();
                            Global.globalPodRemarks = "";
                        }

//                        Bitmap bm =db.getSignature(Global.globalPodAwbno.toString());
//                        imageView.setImageBitmap(bm);
                            //finish();
                       // }

                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();


            }
        });



    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private boolean prepareDirectory()
    {
        try
        {
            if (makedirs())
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean makedirs()
    {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory())
        {
            File[] files = tempdir.listFiles();
            for (File file : files)
            {
                if (!file.delete())
                {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }



    public class signature extends View
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v)
        {
//            Log.v("log_tag", "Width: " + v.getWidth());
//            Log.v("log_tag", "Height: " + v.getHeight());
            if(mBitmap == null)
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);

            }
            Canvas canvas = new Canvas(mBitmap);
            try
            {
               FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();


                    if (Global.globalMultiplePOD.toString().equals("No")) {
                        if (db.insertSignature(CaptureSignature.this, mBitmap)) {
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Signature saved!", Toast.LENGTH_LONG);
                            savedToast.show();

                            finish();
                        } else {
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Signature could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }

                    } else {
                        Global.globalMultipleSignature = mBitmap;
                        finish();
                    }


            }
            catch(Exception e)
            {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear()
        {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string){
        }

        private void expandDirtyRect(float historicalX, float historicalY)
        {
            if (historicalX < dirtyRect.left)
            {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right)
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top)
            {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom)
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}