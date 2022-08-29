package ec.epn.detri.awm.wherearemyfriends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Actividad que ubica la localización de un contacto almacenado en el libro de
 * contactos
 */
public class LocalizadorContactosActivity
       extends Logging_CicloVida_Actividad {
    /**
     * Etiqueta para depuración
     */
    private String TAG = getClass().getSimpleName();

    /**
     * Código que identifica la solicitud enviada a la actividad del libro de contactos
     */
    private static final int CODIGO_SOLICITUD_CONTACTO = 0;

    /**
     * Mantiene una referencia al botón de acción flotante.
     */
    private ImageButton btnSeleccionarContacto;

    /**
     * Referencia hacia la clase que lanza una actividad de mapas para localizar
     * la dirección del contacto seleccionado.
     */
    private LocalizadorDireccionContacto ldc;

    /**
     * Método de enganche que se ejecuta cuando se crea una nueva
     * instancia de la Actividad
     *
     * @param estadoInstanciaGuardado contiene la información de estado de la Actividad.
     */
    @Override
    protected void onCreate(Bundle estadoInstanciaGuardado) {

        super.onCreate(estadoInstanciaGuardado);

        // Asignar el Layout por defecto
        setContentView(R.layout.localizador_contactos_activity);

        // Crear una referencia al botón para agregar contacto
        btnSeleccionarContacto = (ImageButton) findViewById(R.id.btnAgregar);

        // Crear un LocalizadorDireccionContacto para iniciar una aplicación de mapas
        ldc = new LocalizadorDireccionContacto(this);
    }

    /**
     * Callback llamado por el Framework de Android cuando el usuario
     * selecciona el botón para agregar un contacto (btnAgregar)
     * El callback está definido en el fichero localizador_contactos_activity.xml
     *
     * @param v La vista.
     */
    public void encontrarDireccion(View v) {
        // Cambia el diseño del botón
        cambiarIconoBoton(false);

        // Inicia la actividad ContactsContentProvider para obtener el URI del
        // contacto seleccionado
        ldc.seleccionarDireccionContacto(CODIGO_SOLICITUD_CONTACTO);
    }

    /**
     * Método callback llamado por el framework de Android cuando
     * una actividad termina y retorna su resultado
     *
     * @param codigoSolicitud El entero que se provee al método startActivityForResult() y
     *                        que permite identificar de quién procede el resultado
     * @param codigoResultado el entero que retorna la actividad hija a través del método
     *                        setResult().
     * @param datos        Un intent que puede retornar el resultado hacia la actividad
     *                     llamante (varios datos pueden ser adjuntos al campo extras del
     *                     intent)
     */
    @Override
    protected void onActivityResult(int codigoSolicitud,
                                    int codigoResultado,
                                    Intent datos) {
        // Verificar si la actividad se ha completado satisfactoriamente
        // y el código de solicitud es el esperado
        if (codigoResultado == Activity.RESULT_OK
            && codigoSolicitud == CODIGO_SOLICITUD_CONTACTO)
            // Localizar al contacto en base a su dirección
            mostrarEnMapa(datos);

        // Cambiar el ícono
        cambiarIconoBoton(true);
    }

    /**
     * Cambiar la forma del ícono
     *
     * @param reverse {@code true}
     */
    private void cambiarIconoBoton(boolean reverse) {
        btnSeleccionarContacto.setImageResource(
                reverse ? R.drawable.icon_morph_reverse : R.drawable.icon_morph);
        ((Animatable) btnSeleccionarContacto.getDrawable()).start();
    }

    /**
     * Método que muestra la dirección del contacto luego de
     * obtener el permiso del usuario para acceder a READ_CONTACTS
     *
     * @param datos Intent que mantiene los datos del contacto
     */
    private void mostrarEnMapa(final Intent datos) {
        // Usa un marco de ejecución de tareas asincrónica para obtener el contacto
        // y mostrar la dirección del contacto en una aplicación de mapa.
        new AsyncTask<Intent, Void, String>() {

            /**
             * Ejecutar una tarea de larga duración (getAddressFromContact) en segundo plano
             * tal que no se bloquee el hilo de la interfaz gráfica (UI)
             */
            protected String doInBackground(Intent... data) {
                // Extrae la dirección del registro de contacto, que es identificado
                // mediante el URI contenido en el intent.
                return ldc
                        .obtenerDireccionDeContacto(data[0].getData());
            }

            /**
             * Este método se ejecuta en el hilo de la interfaz gráfica de usuario (UI)
             */
            protected void onPostExecute(String address) {
                // Nos aseguramos que la dirección dada es váida.
                if (!TextUtils.isEmpty(address))
                    // Lanza la actividad de Mapas en el hilo de la UI
                    ldc.startMapperActivity(address);
                else
                    //Mostramos un objeto Toast, que es un mensaje emergente mostrado
                   // en la interfaz del usuario para mostrar un mensaje relacionado hacia
                    // alguna interacción realizada por el usaurio
                    Toast.makeText(LocalizadorContactosActivity.this,
                            "Dirección no encontrada",
                            Toast.LENGTH_SHORT).show();
            }
            // Ejecuta el AsyncTask para obtener la dirección de un contacto y entonces
            // iniciar una aplicación de mapas para mostrar la dirección.
        }.execute(datos);
    }
}
