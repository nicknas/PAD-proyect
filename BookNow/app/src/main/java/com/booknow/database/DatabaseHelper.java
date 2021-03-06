package com.booknow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.booknow.database.model.Booking;
import com.booknow.database.model.BookingContract;
import com.booknow.database.model.HoursRestaurant;
import com.booknow.database.model.User;
import com.booknow.database.model.HoursRestaurantContract;
import com.booknow.database.model.Restaurant;
import com.booknow.database.model.RestaurantContract;
import com.booknow.database.model.UserContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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
                + RestaurantContract.RestaurantEntry.HORARIO_CIERRE + " TEXT NOT NULL,"
                + RestaurantContract.RestaurantEntry.COMENSALES_HORA + " INTEGER NOT NULL"
                + " )"
        );

        values.put(RestaurantContract.RestaurantEntry.NAME, "El Bulli");
        values.put(RestaurantContract.RestaurantEntry.INAUGURACION, "01/01/1962");
        values.put(RestaurantContract.RestaurantEntry.DIRECCION, "Ctra. de la Roca, s/n, 17480 Roses, Girona");
        values.put(RestaurantContract.RestaurantEntry.CHEF, "Ferran Adrià");
        values.put(RestaurantContract.RestaurantEntry.HORARIO_APERTURA, "13:00");
        values.put(RestaurantContract.RestaurantEntry.HORARIO_CIERRE, "23:45");
        values.put(RestaurantContract.RestaurantEntry.COMENSALES_HORA, 20);
        db.insert(RestaurantContract.RestaurantEntry.TABLE_NAME, null, values);
        values.clear();

        db.execSQL("CREATE TABLE " + HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME + " ("
                + HoursRestaurantContract.HoursRestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HoursRestaurantContract.HoursRestaurantEntry.DIA + " TEXT NOT NULL,"
                + HoursRestaurantContract.HoursRestaurantEntry.HORA + " TEXT NOT NULL,"
                + HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + " INTEGER NOT NULL,"
                + HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES + " INTEGER NOT NULL,"
                + " FOREIGN KEY (" + HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + ") REFERENCES " + RestaurantContract.RestaurantEntry.TABLE_NAME + "(" + RestaurantContract.RestaurantEntry._ID + ")" + " ON UPDATE CASCADE"
                + " )"
        );

        db.execSQL("CREATE TABLE " + BookingContract.BookingEntry.TABLE_NAME + " ("
                + BookingContract.BookingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookingContract.BookingEntry.DIA + " TEXT NOT NULL,"
                + BookingContract.BookingEntry.HORA + " TEXT NOT NULL,"
                + BookingContract.BookingEntry.NOMBRE_RESERVA + " TEXT NOT NULL,"
                + BookingContract.BookingEntry.NUM_COMENSALES + " INTEGER NOT NULL,"
                + BookingContract.BookingEntry.ID_USUARIO + " INTEGER NOT NULL,"
                + BookingContract.BookingEntry.IS_ACTIVE + " INTEGER NOT NULL,"
                + BookingContract.BookingEntry.ID_RESTAURANTE + " INTEGER NOT NULL,"
                + " FOREIGN KEY (" + BookingContract.BookingEntry.ID_USUARIO + ") REFERENCES " + UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + ")" + " ON UPDATE CASCADE,"
                + " FOREIGN KEY (" + BookingContract.BookingEntry.ID_RESTAURANTE + ") REFERENCES " + RestaurantContract.RestaurantEntry.TABLE_NAME + "(" + RestaurantContract.RestaurantEntry._ID + ")" + " ON UPDATE CASCADE"
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
        c.close();
        return false;
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
            SimpleDateFormat formatInauguracion = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatHorario = new SimpleDateFormat("HH:mm");
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
            int comensalesHora = c.getInt(c.getColumnIndex(RestaurantContract.RestaurantEntry.COMENSALES_HORA));
            Restaurant r = new Restaurant(name, inauguracion, c.getInt(c.getColumnIndex(RestaurantContract.RestaurantEntry._ID)), direccion, chef, horarioApertura, horarioCierre, comensalesHora);
            c.close();
            return r;
        }
        else {
            c.close();
            return null;
        }
    }

    public Restaurant getRestaurantByName(String restaurantName){
        Cursor c = getWritableDatabase().query(RestaurantContract.RestaurantEntry.TABLE_NAME, null, RestaurantContract.RestaurantEntry.NAME + " LIKE ?", new String[]{restaurantName}, null, null, null);
        if (c.getCount() == 1){
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(RestaurantContract.RestaurantEntry.NAME));
            SimpleDateFormat formatInauguracion = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatHorario = new SimpleDateFormat("HH:mm");
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
            int comensalesHora = c.getInt(c.getColumnIndex(RestaurantContract.RestaurantEntry.COMENSALES_HORA));
            Restaurant r = new Restaurant(name, inauguracion, c.getInt(c.getColumnIndex(RestaurantContract.RestaurantEntry._ID)), direccion, chef, horarioApertura, horarioCierre, comensalesHora);
            c.close();
            return r;
        }
        else {
            c.close();
            return null;
        }
    }

    public boolean isRestaurantAvailable(String restaurantName){
        Cursor c = getReadableDatabase().query(RestaurantContract.RestaurantEntry.TABLE_NAME,
                new String[]{RestaurantContract.RestaurantEntry.NAME},
                RestaurantContract.RestaurantEntry.NAME + " LIKE ?", new String[]{restaurantName},
                null, null, null);
        if (c.getCount() == 0 || c == null){
            c.close();
            return false;
        }
        else{
            c.close();
            return true;
        }
    }

    public boolean setHourRestaurant(int id_restaurante, Date dia, Date hora, int comensales){
        boolean isDinersAvailable = true;
        String horaString = hora.getHours() + ":00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String diaString = dateFormat.format(dia);
        Cursor c = getWritableDatabase().query(HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME, null,
                HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + "= ? AND " + HoursRestaurantContract.HoursRestaurantEntry.DIA + " LIKE ? AND "
                + HoursRestaurantContract.HoursRestaurantEntry.HORA + " LIKE ?", new String[]{Integer.toString(id_restaurante), diaString, horaString},
                null, null, null);
        ContentValues values = new ContentValues();
        if (c.getCount() == 0 || c == null){
            Cursor cursor = getWritableDatabase().query(RestaurantContract.RestaurantEntry.TABLE_NAME, new String[]{RestaurantContract.RestaurantEntry.COMENSALES_HORA},
                    RestaurantContract.RestaurantEntry._ID + " = ?", new String[]{Integer.toString(id_restaurante)},
                    null, null, null);
            cursor.moveToFirst();
            values.put(HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE, id_restaurante);
            values.put(HoursRestaurantContract.HoursRestaurantEntry.DIA, diaString);
            values.put(HoursRestaurantContract.HoursRestaurantEntry.HORA, horaString);
            values.put(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES,
                    cursor.getInt(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COMENSALES_HORA)) - comensales);
            getWritableDatabase().insert(HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME, null, values);
        }
        else{
            c.moveToFirst();
            if (c.getCount() == 1){
                if (c.getInt(c.getColumnIndex(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES)) - comensales < 0){
                    isDinersAvailable = false;
                }
                else {
                    values.put(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES, c.getInt(c.getColumnIndex(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES)) - comensales);
                    int rows = getWritableDatabase().update(HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME, values,
                            HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + "= ? AND "
                                    + HoursRestaurantContract.HoursRestaurantEntry.DIA + " LIKE ? AND "
                                    + HoursRestaurantContract.HoursRestaurantEntry.HORA + " LIKE ?", new String[]{Integer.toString(id_restaurante), diaString, horaString});
                }
            }
        }
        c.close();
        return isDinersAvailable;
    }

    public boolean isHourBookingAvailable(Date hour, int idRestaurante){
        boolean available = true;
        Restaurant r = getRestaurantById(idRestaurante);
        if (hour.after(r.getHorarioCierre()) || hour.before(r.getHorarioApertura())){
            available = false;
        }
        return available;
    }

    public Booking getBookingById(int idBooking){
        Booking b = null;
        Cursor c = getReadableDatabase().query(BookingContract.BookingEntry.TABLE_NAME, null,
                BookingContract.BookingEntry._ID + " = ?", new String[]{Integer.toString(idBooking)},
                null, null, null);
        if (c.getCount() == 1){
            c.moveToFirst();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
            Date dia = new Date();
            Date hora = new Date();
            try {
                dia = dateFormat.parse(c.getString(c.getColumnIndex(BookingContract.BookingEntry.DIA)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                hora = hourFormat.parse(c.getString(c.getColumnIndex(BookingContract.BookingEntry.HORA)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String nombreReserva = c.getString(c.getColumnIndex(BookingContract.BookingEntry.NOMBRE_RESERVA));
            int numComensales = c.getInt(c.getColumnIndex(BookingContract.BookingEntry.NUM_COMENSALES));
            int id = c.getInt(c.getColumnIndex(BookingContract.BookingEntry._ID));
            int idUsuario = c.getInt(c.getColumnIndex(BookingContract.BookingEntry.ID_USUARIO));
            boolean isActive = c.getInt(c.getColumnIndex(BookingContract.BookingEntry.IS_ACTIVE)) == 1 ? true : false;
            int idRestaurante = c.getInt(c.getColumnIndex(BookingContract.BookingEntry.ID_RESTAURANTE));

            b = new Booking(dia, hora, nombreReserva, numComensales, id, idUsuario, isActive,idRestaurante);
        }
        c.close();
        return b;
    }

    public void deleteBookingById(int id, int numDiners, String date, Date hour, int idRestaurante){
        String hourString = hour.getHours() + ":00";
        Cursor c = this.getWritableDatabase().query(HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME, null,
                HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + " = ? AND " +
                         HoursRestaurantContract.HoursRestaurantEntry.DIA + " LIKE ? AND " +
                         HoursRestaurantContract.HoursRestaurantEntry.HORA + " LIKE ?", new String[]{Integer.toString(idRestaurante), date, hourString},
                         null, null, null);
        c.moveToNext();
        ContentValues values = new ContentValues();
        values.put(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES, c.getInt(c.getColumnIndex(HoursRestaurantContract.HoursRestaurantEntry.COMENSALES_DISPONIBLES)) + numDiners);
        c.close();
        int ok = this.getWritableDatabase().update(HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME, values,
                                       HoursRestaurantContract.HoursRestaurantEntry.ID_RESTAURANTE + " = ? AND " +
                                                   HoursRestaurantContract.HoursRestaurantEntry.DIA + " LIKE ? AND " +
                                                   HoursRestaurantContract.HoursRestaurantEntry.HORA + " LIKE ?", new String[]{Integer.toString(idRestaurante), date, hourString});
        this.getWritableDatabase().delete(BookingContract.BookingEntry.TABLE_NAME,
                              BookingContract.BookingEntry._ID + " = ?", new String[]{Integer.toString(id)});

    }

    public Cursor getAllBookings(int idUsuario){
        Cursor c = getWritableDatabase().query(BookingContract.BookingEntry.TABLE_NAME, null,
                BookingContract.BookingEntry.ID_USUARIO + " = ?", new String[]{Integer.toString(idUsuario)},
                null, null, BookingContract.BookingEntry.DIA + " DESC");
        return c;
    }

    public Cursor getActiveBookings(int idUsuario){
        Cursor c = getWritableDatabase().query(BookingContract.BookingEntry.TABLE_NAME, null,
                BookingContract.BookingEntry.ID_USUARIO + " = ? AND "
                        + BookingContract.BookingEntry.IS_ACTIVE + " = 1", new String[]{Integer.toString(idUsuario)},
                null, null, BookingContract.BookingEntry.DIA + " DESC");
        return c;
    }

    public Cursor getClosedBookings(int idUsuario){
        Cursor c = getWritableDatabase().query(BookingContract.BookingEntry.TABLE_NAME, null,
                BookingContract.BookingEntry.ID_USUARIO + " = ? AND "
                        + BookingContract.BookingEntry.IS_ACTIVE + " = 0", new String[]{Integer.toString(idUsuario)},
                null, null, BookingContract.BookingEntry.DIA + " DESC");
        return c;
    }

    public void createNewBooking(Date dia, Date hora, String nombreReserva, int numComensales,
                                 int idUsuario, int idRestaurante) throws Exception{
        boolean isHourAvailable = isHourBookingAvailable(hora, idRestaurante);
        if (isHourAvailable){
            boolean isDinersAvailable = setHourRestaurant(idRestaurante, dia, hora, numComensales);
            if (isDinersAvailable){
                ContentValues values = new ContentValues();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                values.put(BookingContract.BookingEntry.DIA, dateFormat.format(dia));
                values.put(BookingContract.BookingEntry.HORA, hourFormat.format(hora));
                values.put(BookingContract.BookingEntry.NOMBRE_RESERVA, nombreReserva);
                values.put(BookingContract.BookingEntry.NUM_COMENSALES, numComensales);
                values.put(BookingContract.BookingEntry.ID_USUARIO, idUsuario);
                values.put(BookingContract.BookingEntry.IS_ACTIVE, 1);
                values.put(BookingContract.BookingEntry.ID_RESTAURANTE, idRestaurante);
                getWritableDatabase().insert(BookingContract.BookingEntry.TABLE_NAME, null, values);
            }
            else {
                throw new Exception("There is no diners available in this hour");
            }

        }

        else {
            throw new Exception("The restaurant is not open in this hour");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantContract.RestaurantEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HoursRestaurantContract.HoursRestaurantEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookingContract.BookingEntry.TABLE_NAME);
        onCreate(db);
    }
}
