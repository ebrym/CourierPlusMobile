package db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.dataList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataDB {
	Connection con;
	String name;
	private ArrayList<String> results = new ArrayList<String>();

	public boolean getUser(Context context,String userName, String password){
		con = new Connection(context);
		
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username='" + userName + "' and password='" + password + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{

				cursor.close();
				return false;
			} else {

				cursor.close();
				return true;
			}

		}else{
			return false;
		}
	}
	public String getUserDeviceType(Context context,String userName){
		con = new Connection(context);
		String DeviceType = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username='" + userName + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{
				cursor.close();
			} else {
				if (cursor.moveToFirst()) {
					DeviceType = cursor.getString(cursor.getColumnIndex("DeviceType"));

					cursor.close();
				}
				cursor.close();
			}

		}else{

		}
		return DeviceType;
	}
	public String getDeviceID(Context context){
		con = new Connection(context);
		String DeviceID = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username<>'admin'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{
				cursor.close();
			} else {
				if (cursor.moveToFirst()) {
					DeviceID = cursor.getString(cursor.getColumnIndex("DeviceID"));

					cursor.close();
				}
				cursor.close();
			}

		}else{

		}
		return DeviceID;
	}
	public String getUserID(Context context){
		con = new Connection(context);
		String UserID = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username<>'admin'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{
				cursor.close();
			} else {
				if (cursor.moveToFirst()) {
					UserID = cursor.getString(cursor.getColumnIndex("username"));

					cursor.close();
				}
				cursor.close();
			}

		}else{

		}
		return UserID;
	}

	public Cursor getUserList(Context context){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
			//	cursor.close();
		}
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor dcursor = db.rawQuery("SELECT * FROM users WHERE username <>'admin'", null);
		return dcursor;

	}

	public List<String> getStation(Context context) {
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		SQLiteDatabase db = con.getWritableDatabase();
		try {
			db = con.getWritableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM station order by station_code", null);

			if (c != null ) {
				if (c.moveToFirst()) {
					labels.add(" ");
					do {
						String stationCode = c.getString(c.getColumnIndex("station_code"));
						labels.add(stationCode);
					}while (c.moveToNext());
				}
			}
			return labels;
		} catch (SQLiteException se ) {
			Log.e(getClass().getSimpleName(), "Could not create or Open the database");
		} finally {

			db.close();
		}
		return labels;
	}
	public List<String> getScanOperations(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM scanoperations order by opname";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("opname"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getContent(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM ContentType";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("description"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getPodStatus(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM dexcodes";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("Description"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getDeliveryType(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM deliverytype";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("deliverytype"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getPackagingType(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM packagingtype";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("packagingtype"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getCratingType(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM createpackage";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("description"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getRoutes(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM routes order by routecode";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("routecode"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getRoutesByDestination(Context context, String Destination){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM routes where station_code='" + Destination.toString() + "' order by routecode";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("routecode"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getExpressCenter(Context context){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM expresscenters order by expresscentercode";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("expresscentercode"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public List<String> getOnForwarding(Context context, String destination){
		List<String> labels = new ArrayList<String>();
		con = new Connection(context);
		// Select All Query
		String selectQuery = "SELECT  * FROM deliverytown where station_code='" + destination + "' order by deliverytowncode";

		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			labels.add(" ");
			do {
				String scanOperation = cursor.getString(cursor.getColumnIndex("deliverytowncode"));

				labels.add(scanOperation);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}
	public boolean dynamicInsert(Context context,String strSQL){
		con = new Connection(context);

		try {
			con.createDataBase();
		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			db.execSQL(strSQL);
			return true;
		}else{
			return false;
		}
		//return false;
		} catch (IOException e) {
			return false;
	}

	}
	public boolean insertSignature(Context context, Bitmap bm)  {
		con = new Connection(context);
		// Convert the image into byte array
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

		byte[] buffer= out.toByteArray();


		// Open the database for writing
		SQLiteDatabase db = con.getWritableDatabase();
		// Start the transaction.
		db.beginTransaction();
		ContentValues values;
		String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		try
		{

			values = new ContentValues();
			values.put("Signature", buffer);
			values.put("AWBNO", Global.globalPodAwbno);
			values.put("DateCreated", currentDateandTime);
			values.put("Transferred", "N");
			// Insert Row
			db.insert("Signatures", "srn", values);
			//long i = db.insert("Signatures", null, values);
			//Log.i("Insert", i + "");
			// Insert into database successfully.
			db.setTransactionSuccessful();
			return true;
		}
		catch (SQLiteException e)
		{
			Log.v("log_tag","error : " + e.getMessage());
			e.printStackTrace();
			return false;

		}
		finally
		{
			db.endTransaction();
			// End the transaction.
			db.close();
			// Close database
		}
	}
	public Bitmap getSignature(Context context,String id){
		con = new Connection(context);
		Bitmap bitmap = null;
		// Open the database for reading
		SQLiteDatabase db = con.getReadableDatabase();
		// Start the transaction.
		db.beginTransaction();

		try
		{
			String selectQuery = "SELECT * FROM Signatures WHERE AWBNO = '" + id + "'";
			Cursor cursor = db.rawQuery(selectQuery, null);
			if(cursor.getCount() >0)
			{
				while (cursor.moveToNext()) {
					// Convert blob data to byte array
					byte[] blob = cursor.getBlob(cursor.getColumnIndex("Signature"));
					// Convert the byte array to Bitmap
					bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);

				}

			}
			db.setTransactionSuccessful();

		}
		catch (SQLiteException e)
		{
			e.printStackTrace();

		}
		finally
		{
			db.endTransaction();
			// End the transaction.
			db.close();
			// Close database
		}
		return bitmap;

	}
	public boolean checkAWBNOSCans(Context context,String AWBNO,String ScanStatus){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM SCANS WHERE AWBNO='" + AWBNO + "' and SCAN_STATUS='" + ScanStatus + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{

				cursor.close();
				return false;
			} else {

				cursor.close();
				return true;
			}

		}else{
			return false;
		}
	}
	public boolean checkAWBNOSignature(Context context,String AWBNO){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM signatures WHERE AWBNO='" + AWBNO + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{

				cursor.close();
				return false;
			} else {

				cursor.close();
				return true;
			}

		}else{
			return false;
		}
	}
	public boolean checkAWBNOPickUp(Context context,String AWBNO){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM PICKUP_BILLING WHERE AwbNo='" + AWBNO + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{

				cursor.close();
				return false;
			} else {

				cursor.close();
				return true;
			}

		}else{
			return false;
		}
	}
	public boolean checkAWBNOPOD(Context context,String AWBNO){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM history_pod WHERE waybillnumber='" + AWBNO + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{

				cursor.close();
				return false;
			} else {

				cursor.close();
				return true;
			}

		}else{
			return false;
		}
	}
	public String getDEXCode(Context context,String Dex){
		con = new Connection(context);
		String dexcodeid = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM DEXCodes WHERE Description='" + Dex + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{
				cursor.close();
				return "";
			} else {
				if (cursor.moveToFirst()) {
						 dexcodeid = cursor.getString(cursor.getColumnIndex("DEXCodeID"));

					cursor.close();
					return dexcodeid;
				}
			}
		}else{
			return "";
		}
		return  dexcodeid;
	}
	public String getDeliveryTownID(Context context,String deliveryTown,String Station){
		con = new Connection(context);
		String deliveryTownID = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM deliverytown WHERE deliverytowncode='" + deliveryTown + "' and Station_code='" + Station + "'", null);

			if (cursor.getCount() < 1) // UserName Not Exist
			{
				cursor.close();
				return "";
			} else {
				if (cursor.moveToFirst()) {
					deliveryTownID = cursor.getString(cursor.getColumnIndex("deliverytownid"));

					cursor.close();
					return deliveryTownID;
				}
			}
		}else{
			return "";
		}
		return  deliveryTownID;
	}
	public Cursor getPODNotUploaded(Context context){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
		//	cursor.close();
		}
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor dcursor = db.rawQuery("SELECT * FROM history_pod WHERE outstation_Transfer='N'", null);
		return dcursor;

	}
	public Cursor getSCANSNotUploaded(Context context){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
			//	cursor.close();
		}
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM SCANS WHERE TransferStatus='N'", null);
		return cursor;

	}
	public Cursor getPickUPNotUploaded(Context context){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
			//	cursor.close();
		}
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM PICKUP_BILLING WHERE CUSTOM_FIELD2='N'", null);
		return cursor;

	}
	public Cursor getSignatureNotUploaded(Context context){
		con = new Connection(context);

		try {
			con.createDataBase();
		} catch (IOException e) {
			//	cursor.close();
		}
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM signatures WHERE Transferred= 'N'", null);
		return cursor;

	}
	public String getAvailableScanCount(Context context){
		con = new Connection(context);
		String scanCount = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT count(*) as ScanCount FROM scans WHERE TransferStatus='N'", null);

			if (cursor.getCount() < 1) // No scans to upload
			{
				scanCount = "0";
				cursor.close();

			} else {
				if (cursor.moveToFirst()) {
					scanCount = cursor.getString(cursor.getColumnIndex("ScanCount"));

					cursor.close();
				}
			}
		}else{

		}
		return scanCount;
	}
	public String getAvailableSignatureCount(Context context){
		con = new Connection(context);
		String signatureCount = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT count(*) as signatureCount FROM signatures WHERE Transferred='N'", null);

			if (cursor.getCount() < 1) // No scans to upload
			{
				signatureCount = "0";
				cursor.close();

			} else {
				if (cursor.moveToFirst()) {
					signatureCount = cursor.getString(cursor.getColumnIndex("signatureCount"));

					cursor.close();
				}
			}
		}else{

		}
		return signatureCount;
	}

	public String getAvailablePODCount(Context context){
		con = new Connection(context);
		String PODCount = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT count(*) as PODCount FROM history_pod WHERE outstation_Transfer='N'", null);

			if (cursor.getCount() < 1) // No scans to upload
			{
				PODCount = "0";
				cursor.close();

			} else {
				if (cursor.moveToFirst()) {
					PODCount = cursor.getString(cursor.getColumnIndex("PODCount"));

					cursor.close();
				}
			}
		}else{

		}
		return PODCount;
	}
	public String getAvailablePickUpCount(Context context){
		con = new Connection(context);
		String PickUpCount = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT count(*) as PickUpCount FROM PICKUP_BILLING WHERE CUSTOM_FIELD2='N'", null);

			if (cursor.getCount() < 1) // No scans to upload
			{
				PickUpCount = "0";
				cursor.close();

			} else {
				if (cursor.moveToFirst()) {
					PickUpCount = cursor.getString(cursor.getColumnIndex("PickUpCount"));

					cursor.close();
				}
			}
		}else{

		}
		return PickUpCount;
	}
	public String getUserCount(Context context){
		con = new Connection(context);
		String userCount = "";
		try {
			con.createDataBase();
		} catch (IOException e) {
		}

		if(con.checkDataBase()){
			con.openDataBase();
			SQLiteDatabase db = con.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT count(*) as userCount FROM users", null);

			if (cursor.getCount() < 1) // No scans to upload
			{
				userCount = "0";
				cursor.close();

			} else {
				if (cursor.moveToFirst()) {
					userCount = cursor.getString(cursor.getColumnIndex("userCount"));

					cursor.close();
				}
			}
		}else{

		}
		return userCount;
	}



	/* Method for fetching record from Database */
	public ArrayList<dataList> getAllData(Context context, String type) {
		String query = "";
		if (type.equals("POD"))
		{
			 query = "SELECT waybillnumber As Awbno, OriginStation as Origin, OriginStation as Destination, POD as OP, poddate as Date FROM history_pod WHERE outstation_Transfer='N'" ;
		}
		if (type.equals("SCAN"))
		{
			 query = "SELECT AWBNO As Awbno, ORIGIN as Origin, DESTINATION as Destination, SCAN_STATUS as OP, DATE as Date FROM SCANS WHERE TransferStatus='N'" ;
		}
		if (type.equals("PICKUP"))
		{
			 query = "SELECT AWBNO As Awbno, ORIGIN as Origin, DESTINATION as Destination, WEIGHT as OP, PICKUP_DATE as Date FROM PICKUP_BILLING WHERE CUSTOM_FIELD2='N'" ;
		}
		ArrayList<dataList> dlist = new ArrayList<dataList>();
		con = new Connection(context);
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			while (c.moveToNext()) {
				String Awbno = c.getString(c.getColumnIndex("Awbno"));
				String Origin = c.getString(c.getColumnIndex("Origin"));
				String Destination = c.getString(c.getColumnIndex("Destination"));
				String OP = c.getString(c.getColumnIndex("OP"));
				String Date = c.getString(c.getColumnIndex("Date"));

				dataList dl = new dataList();
				dl.setAwbno(Awbno);
				dl.setOrigin(Origin);
				dl.setDestination(Destination);
				dl.setOp(OP);
				dl.setDate(Date);

				/*Log.v("DBHelper: ", "Awbno: " + Awbno);
				Log.v("DBHelper: ", "Origin: " + Origin);
				Log.v("DBHelper: ", "Destination: " + Destination);
				Log.v("DBHelper: ", "OP: " + OP);
				Log.v("DBHelper: ", "Date: " + Date);*/

				dlist.add(dl);
			}
		}

		return dlist;

	}
}
