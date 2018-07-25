package com.booknow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.booknow.database.model.User;
import com.booknow.database.model.HoursRestaurantContract;
import com.booknow.database.model.Restaurant;
import com.booknow.database.model.RestaurantContract;
import com.booknow.database.model.UserContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "BookNow.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " ("
                + UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserContract.UserEntry.LOGIN + " TEXT NOT NULL UNIQUE,"
                + UserContract.UserEntry.TELEFONO + " INTEGER,"
                + UserContract.UserEntry.PASSWORD + " TEXT NOT NULL,"
                + UserContract.UserEntry.EMAIL + " TEXT NOT NULL UNIQUE" + " )"
        );

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.LOGIN, "nick");
        values.put(UserContract.UserEntry.TELEFONO, 648182926);
        values.put(UserContract.UserEntry.PASSWORD, "nick");
        values.put(UserContract.UserEntry.EMAIL, "nalcaine@ucm.es");
        db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        values.clear();
        db.execSQL("CREATE TABLE " + RestaurantContract.RestaurantEntry.TABLE_NAME + " ("
                + RestaurantContract.RestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RestaurantContract.RestaurantEntry.NAME + " TEXT NOT NULL,"
                + RestaurantContract.RestaurantEntry.INAUGURACION + " TEXT,"
                + RestaurantContract.RestaurantEntry.DIRECCION + " TEXT NOT NULL,"
                + RestaurantContract.RestaurantEntry.CHEF + " TEXT,"
                + RestaurantContract.RestaurantEntry.HORARIO_APERTURA + " TEXT NOT NULL,"
                + RestaurantContract.RestaurantEntry.HORARIO_CIERRE + " TEXT NOT NULL"
                + " )"
        );
        values.put(RestaurantContract.RestaurantEntry.NAME, "El Bulli");
        values.put(RestaurantContract.RestaurantEntry.INAUGURACION, "01-01-1962");
        values.put(RestaurantContract.RestaurantEntry.DIRECCION, "Ctra. de la Roca, s/n, 17480 Roses, Girona");
        values.put(RestaurantContract.RestaurantEntry.CHEF, "Ferran Adrià");
        values.put(RestaurantContract.RestaurantEntry.HORARIO_APERTURA, "13:00");
        values.put(RestaurantContract.RestaurantEntry.HORARIO_CIERRE, "23:45");
        db.insert(RestaurantContract.RestaurantEntry.TABLE_NAME, null, values);
        values.clear();
        db.execSQL("CREATE TABLE " + HoursRestaurantContract.HoursDinersEntry.TABLE_NAME + " ("
                + HoursRestaurantContract.HoursDinersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HoursRestaurantContract.HoursDinersEntry.DIA + " TEXT NOT NULL,"
                + HoursRestaurantContract.HoursDinersEntry.HORA + " TEXT NOT NULL,"
                + HoursRestaurantContract.HoursDinersEntry.ID_RESTAURANTE + " INTEGER NOT NULL,"
                + HoursRestaurantContract.HoursDinersEntry.COMENSALES_DISPONIBLES + " INTEGER NOT NULL,"
                + " FOREIGN KEY (" + HoursRestaurantContract.HoursDinersEntry.ID_RESTAURANTE + ") REFERENCES " + RestaurantContract.RestaurantEntry.TABLE_NAME + "(" + RestaurantContract.RestaurantEntry._ID + ")" + " ON UPDATE CASCADE"
                + " )"
        );
    }

    public User getUserByLogin(String login){
       User u = null;
       Cursor c = getWritableDatabase().query(UserContract.UserEntry.TABLE_NAME, null, UserContract.UserEntry.LOGIN + " LIKE ?", new String[]{login}, null, null, null);
       if (c.getCount() > 0){
           c.moveToFirst();
           u = new User(c.getInt(c.getColumnIndex(UserContract.UserEntry._ID)), c.getString(c.getColumnIndex(UserContract.UserEntry.LOGIN)),
                   c.getInt(c.getColumnIndex(UserContract.UserEntry.TELEFONO)), c.getString(c.getColumnIndex(UserContract.UserEntry.PASSWORD)),
                   c.getString(c.getColumnIndex(UserContract.UserEntry.EMAIL)));
       }
       if (c != null && !c.isClosed()){
           c.close();
       }
       return u;
    }

    public boolean isUserLogged(String login, String password){
        Cursor c = getWritableDatabase().query(UserContract.UserEntry.TABLE_NAME, null, UserContract.UserEntry.LOGIN + " LIKE ?" + " AND " + UserContract.UserEntry.PASSWORD + " LIKE ?", new String[]{login, password}, null, null, null);
        if (c.getCount() == 1){
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }

    public Cursor getAllRestaurants(){
        Cursor c = getWritableDatabase().query(RestaurantContract.RestaurantEntry.TABLE_NAME, null, null, null, null, null, null);
        return c;
    }

    public Restaurant getRestaurantById(int id){
        Cursor c = getWritableDatabase().query(RestaurantContract.RestaurantEntry.TABLE_NAME, null, RestaurantContract.RestaurantEntry._ID + " LIKE ?", new String[]{Integer.toString(id)}, null, null, null);
        if (c.getCount() == 1){
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.NAME));
            SimpleDateFormat formatInauguracion = new SimpleDateFormat("DD-MM-YYYY");
            SimpleDateFormat formatHorario = new SimpleDateFormat("HH:MM");
            Date inauguracion = null;
            Date horarioApertura = null;
            Date horarioCierre = null;
            try {
                inauguracion = formatInauguracion.parse(c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.INAUGURACION)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                horarioApertura = formatHorario.parse(c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.HORARIO_APERTURA)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                horarioCierre = formatHorario.parse(c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.HORARIO_CIERRE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String direccion = c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.DIRECCION));
            String chef = c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.CHEF));
            Restaurant r = new Restaurant(name, inauguracion, c.getInt(c.getColumnIndex(RestaurantContract.RestaurantEntry._ID)), direccion, chef, horarioApertura, horarioCierre);
            c.close();
            return r;
        }
        else {
            c.close();
            return null;
        }
    }

    public void setHourRestaurant(int id_restaurante, Date dia, Date hora){
        String horaString = hora.getHours() + ":00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY");
        String diaString = dateFormat.format(dia);
        // Cursor c = getWritableDatabase().query(HoursRestaurantContract.HoursDinersEntry.TABLE_NAME, null, );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantContract.RestaurantEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HoursRestaurantContract.HoursDinersEntry.TABLE_NAME);
        onCreate(db);
    }
}