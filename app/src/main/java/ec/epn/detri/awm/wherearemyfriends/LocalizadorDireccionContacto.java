package ec.epn.detri.awm.wherearemyfriends;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Permite la recuperación de un contacto del libro de contactos del terminal y
 * luego lanza una actividad para geolocalizar la dirección de dicho contacto sobre
 * una aplicación de mapas
 */
public class LocalizadorDireccionContacto {
    /**
     * Código de solicitud de permiso READ_CONTACTS.
     */
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    /**
     * Etiqueta de depuración para Android Logger
     */
    private String TAG = getClass().getSimpleName();
    /**
     * Referencia a la Actividad
     */
    private Activity mActivity;

    /**
     * Constructor de inicialización
     */
    public LocalizadorDireccionContacto(Activity activity) {
        mActivity = activity;

        // Verifica si el usuario ya ha concedido el permiso de acceso a los
        // contactos. Si no,lo vuelve a solicitar.
        if (mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    /**
     * Incia la ACTIVIDAD ContactsContentProvider y obtener el URI del contacto seleccionado.
     */
    public void seleccionarDireccionContacto(int pickContactRequest) {
        // Crea un intent implícito que coincida con una actividad de Proveedor de Contenido de Contactos
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        // Pasa un bundle para lograr la transiciones de pantallas usadas cuando una actividad cambia.
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle();

        // Inicia la Actividad para acceder al ContactsContentProvider con el método
        // de enganche onActivityResult()
        // Permite al usuario seleccionar un contacto y retornar el URI
        // para el contacto
        mActivity.startActivityForResult(intent, pickContactRequest, bundle);
    }

    /**
     * Extrae la dirección desde el URI de un contacto obtenido desde el Proveedor de contenidos
     * de contactos.
     */
    public String obtenerDireccionDeContacto(Uri ContactoURI) {
        // Obtiene una referencia a nuestro Content Resolver.
        ContentResolver cr = mActivity.getContentResolver();

        // Obtiene un puntero al contacto designado por su URI
        String id;
        String where;
        String[] whereParameters;
        try (Cursor cursor = cr.query(ContactoURI,
                                      null, null, null, null)) {
            // Pone el puntero al inicio Start the cursor at the beginning.
            assert cursor != null;
            cursor.moveToFirst();

            // Obtiene el ID del contacto
            id = cursor.getString
                    (cursor.getColumnIndex(ContactsContract.Contacts._ID));
        }

        // Crea una consulta SQLque buscará los campos dirección (calle) del contacto identificado
        // por su URI
        where = ContactsContract.Data.CONTACT_ID
                + " = ? AND "
                + ContactsContract.Data.MIMETYPE
                + " = ?";
        whereParameters = new String[]{
                id,
                ContactsContract.CommonDataKinds.StructuredPostal
                        .CONTENT_ITEM_TYPE
        };

        // Crea un cursor que contiene los resultados de la consulta
        try (Cursor cursorDireccion = cr.query(ContactsContract.Data.CONTENT_URI,
                                          null,
                                          where,
                                          whereParameters,
                                          null)) {
            // Incia el cursor al inicio
            assert cursorDireccion != null;
            cursorDireccion.moveToFirst();

            // Extrae y retorna la dirección postal del contacto
            return cursorDireccion
                    .getString(cursorDireccion.getColumnIndexOrThrow
                            (ContactsContract.CommonDataKinds
                                     .StructuredPostal.FORMATTED_ADDRESS));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Inicia una actvidad capaz de geolocalizar una dirección postal.
     */
    public void startMapperActivity(String address) {

        // Crea un intent que será enviado a la aplicación de mapas
        final Intent geoIntent =
                generarIntentMapas(address);

        // Verificar si el terminal móvil tiene una aplicación capaz de manejar el intent "geo".
        if (geoIntent.resolveActivity
                (mActivity.getPackageManager()) != null) {
            mActivity.startActivity(geoIntent);
        } else
        // Caso contrario inciar un navegador.
        {
            mActivity.startActivity(generarIntentNavegador(address));
        }
    }

    /**
     * Método que construye y retorna un Intent para llamar a una aplicación de mapas.
     */
    private Intent generarIntentMapas(String address) {
        return new Intent(Intent.ACTION_VIEW,
                          Uri.parse("geo:0,0?q=" + Uri.encode(address)));
    }

    /**
     * Método que construye y retorna un Intent para llamar a una aplicación de navegación.
     */
    private Intent generarIntentNavegador(String address) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                          Uri.parse("https://maps.google.com/?q="
                                            + Uri.encode(address)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

}
