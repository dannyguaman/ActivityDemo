package ec.epn.detri.awm.wherearemyfriends;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;   //Android Logger

/**
 * Clase abstracta que sobreescribe los callbacks del ciclo de vida de la
 * Actividad (que lo instacia) para registrar los eventos necesarios.
 */
public abstract class Logging_CicloVida_Actividad
        extends Activity {
    /**
     * Etiqueta de depuración usado por Android Logger
     */
    private final String TAG =
            getClass().getSimpleName();

    /**
     * Sobreescritura del método OnCreate() que se invoca cuando se crea una instancia de la
     * Actividad. Aquí se debería agregar el código que necesita
     * ser inicializado, p.ej. los elementos UI y las variables
     * que requiren un alcance a nivel de clase.
     * Si se invoca el método finish() ya no es posible invocar a
     * ningún callback, excepto el callback onDestroy().
     *
     * @param savedInstanceState contiene información de estado de
     *                           una actividad que ha sido guardada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // La actividad será recreada usando la información de
            // estado que se envía en Bundle de entrada.
            Log.d(TAG, "onCreate(): la actividad ha sido recreada");

        } else {
            // Se crea una nueva actividad.
            Log.d(TAG, "onCreate(): La actividad ha sido creada");
        }

    }

    /**
     * Sobreescritura del método onStart() que se invoca DESPUÉS de ejecutar onCreate()
     * o DESPUÉS de ejecutar onRestart(). El código de este callback será ejecutado
     * cuando la Actividad está apunto de hacerse visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() - La actividad está apunto de hacerse visible");
    }

    /**
     * Sobreescritura del método que invoca DESPUÉS de ejecutar
     * onRestoreStateInstance(Bundle). Es decir, solo si la Actividad ha sido
     * pausada con OnPause() y se ha guardado la información de estado de la
     * Actividad en en un objeto Bundle.
     * onResume() se ejecuta inmediatamente después de onStart().
     * En este punto el usuario ya puede empezar a interactuar con la Actividad.
     * Colocar aqui el código para adquirir recursos exclusivos como la cámara.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,
              "onResume() - la actividad ya está visible");
    }

    /**
     * Sobreescritura el método onPause() que se ejecuta cuando la Actividad pierde
     * el foco, pero aún está visible en segundo plano. A este callback le puede seguir
     * onStop() para que la Atividad ya no sea visible ni siquiera en segundo plano,
     * u onResume() para volver a hacer visible la Actividad.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,
              "onPause() - otra actividad está tomando el foco y esta actividad está" +
                      "a punto de ser pausada");
    }

    /**
     * Sobreescritura del método onStop() que se invoca cuando la Actividad
     * ya no está visible. Invocar este método para liberar recursos de CPU y memoria, o recursos exclusivos
     * como la cámara. Se debe guardar el estado persistente con onSaveInstanceState() en caso
     * de que la actividad sea destruida.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,
              "onStop() - la actividad ya no es visible y se ha detenido");
    }

    /**
     * Sobreescritura del método onRestart() que se ejecuta cuando la Actividad (detenida con onStop()) se reinicia.
     * Este método es seguido de onStart() y luego onResume().
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() - la actividad está apunto de ser reiniciado");
    }

    /**
     * Sobreescritura del método onDestroy() que librea los recursos y detiene los hilos
     * generados por la Actividad.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() - la actividad está apunto de ser destruida");
    }
}
