package Scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstone.Forms.Inventory;
import com.example.capstone.Forms.Sales_Order;
import com.example.capstone.Forms.SurveyForm;
import com.example.capstone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class BarCodeScanner extends AppCompatActivity {
    CameraView cameraView;
    Button btn;
    AlertDialog waitingDialog;
    GraphicOverlay graphicOverlay;
    DatabaseReference barb;
    int key=0;



    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        barb = FirebaseDatabase.getInstance().getReference().child("Barcodes");

        cameraView=findViewById(R.id.camera);
        btn=findViewById(R.id.scan);
        waitingDialog=new SpotsDialog.Builder().setContext(this).setMessage("Please Wait...").setCancelable(false).build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                //waitingDialog.show();
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
                runDetector(bitmap);
                //waitingDialog.dismiss();

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void runDetector(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                        .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);
        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

                processResult(firebaseVisionBarcodes);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BarCodeScanner.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
            key=0;
            Rect rect = item.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            graphicOverlay.add(rectOverlay);
            int valueType = item.getValueType();

            if (valueType == FirebaseVisionBarcode.TYPE_PRODUCT) {

                Bundle bundle = getIntent().getExtras();
                if(bundle!=null)
                {
                    key = bundle.getInt("key");
                }

                barb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean flag=false;

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //Toast.makeText(BarCodeScanner.this, ""+key, Toast.LENGTH_SHORT).show();
                            if (item.getRawValue().equals(ds.getKey())) {
                                flag=true;
                                if(key==0)
                                {

                                    showDialog("Product: "+ds.child("Product").getValue(String.class)+"\nManufacturer: "+ds.child("Manufacturer").getValue(String.class)+"\nPrice: Rs."+ds.child("Price").getValue(String.class));
                                }
                                else if (key == 1) {

                                    Intent intent = new Intent(BarCodeScanner.this, Sales_Order.class);
                                    String prod = ds.child("Product").getValue(String.class);
                                    String desc = ds.child("Description").getValue(String.class);
                                    String price = ds.child("Price").getValue(String.class);
                                    Bundle b = new Bundle();
                                    b.putString("product", prod);
                                    b.putString("desc", desc);
                                    b.putString("price", price);
                                    intent.putExtras(b);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else if (key == 2) {
                                    Intent intent1 = new Intent(BarCodeScanner.this, Inventory.class);
                                    String product = ds.child("Product").getValue(String.class);
                                    Integer bottles = ds.child("Bottles").getValue(Integer.class);
                                    Bundle b1 = new Bundle();
                                    b1.putString("product", product);
                                    b1.putInt("bottles", bottles);
                                    intent1.putExtras(b1);
                                    setResult(RESULT_OK, intent1);
                                    finish();

                                } else if (key == 3) {
                                    Intent intent2 = new Intent(BarCodeScanner.this, SurveyForm.class);
                                    String product = ds.child("Product").getValue(String.class);
                                    String manufacturer = ds.child("Manufacturer").getValue(String.class);
                                    Bundle b2 = new Bundle();
                                    b2.putString("product", product);
                                    b2.putString("manufacturer", manufacturer);
                                    intent2.putExtras(b2);
                                    setResult(RESULT_OK, intent2);
                                    finish();

                                }

                            }

                        }
                        if(!flag)
                        {
                            showDialog(item.getRawValue()+" "+"Not found in database!!");
                        }
                    }



                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }




            }

        }


    public void showDialog(String message)
    {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        dialog.show();
    }
}