package cl.softmedia.movillitar.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.stmt.Where;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cl.softmedia.movillitar.R;
import cl.softmedia.movillitar.bss.CargaDatosBss;
import cl.softmedia.movillitar.bss.ComunaBss;
import cl.softmedia.movillitar.bss.CultivoBss;
import cl.softmedia.movillitar.bss.UsuarioBss;
import cl.softmedia.movillitar.dao.GenericDao;
import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.domain.Comuna;
import cl.softmedia.movillitar.domain.Cultivo;
import cl.softmedia.movillitar.domain.Usuario;
import cl.softmedia.movillitar.utils.SharedPreferencesManager;
import cl.softmedia.movillitar.utils.SnackbarManager;
import cl.softmedia.movillitar.utils.ToastManager;
import cl.softmedia.movillitar.utils.WifiConexionManager;

public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.tool_bar)
    Toolbar tool_bar;

    @InjectView(R.id.et_usuario)
    EditText et_usuario;

    @InjectView(R.id.et_password)
    EditText et_password;

    @InjectView(R.id.btn_ingresar)
    Button btn_ingresar;

    @InjectView(R.id.btn_cancelar)
    Button btn_cancelar;

    @InjectView(R.id.txt_version)
    TextView txt_version;

    private Context mContext;
    private UsuarioBss oUsuarioBss;
    private GenericDao<Usuario> oUsuarioDao;
    private CargaDatosBss oCargaDatosBss;
    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;
    private ComunaBss oComunaBss;
    private GenericDao<Comuna> oComunaDao;
    private CultivoBss oCultivoBss;
    private GenericDao<Cultivo> oCultivoDao;
    private SharedPreferencesManager oSharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mContext = this;

        ButterKnife.inject(this);

        tool_bar.setTitle("Litar Móvil - Ingreso de Usuario");
        txt_version.setText("Versión " + getString(R.string.version));

        et_usuario.addTextChangedListener(onTextWatcherEditTextUsuario);

        try {

            String conexion = getResources().getString(R.string.conexion);

            oUsuarioBss = new UsuarioBss(conexion);
            oUsuarioDao = new GenericDao<>(this, Usuario.class);
            oCargaDatosBss = new CargaDatosBss(conexion);
            oAsignacionVisitaDao = new GenericDao<>(this, AsignacionVisita.class);
            oComunaBss = new ComunaBss(conexion);
            oComunaDao = new GenericDao<>(this, Comuna.class);
            oCultivoBss = new CultivoBss(conexion);
            oCultivoDao = new GenericDao<>(this, Cultivo.class);
            oSharedPreferencesHelper = new SharedPreferencesManager(mContext);

            et_usuario.setText(oSharedPreferencesHelper.getString(SharedPreferencesHelper.KEY_USUARIO));
            et_password.setText(oSharedPreferencesHelper.getString(SharedPreferencesHelper.KEY_PASSWD));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextWatcher onTextWatcherEditTextUsuario = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 1) {
                if (!validateEmail(s.toString())) {
                    et_usuario.setError("Formato incorrecto");
                } else {
                    et_usuario.setError(null);
                }
            }
        }
    };

    @OnClick(R.id.btn_ingresar)
    public void ingresar(View v) {
        try {

            String fechaProcesamiento = oSharedPreferencesHelper.getString(SharedPreferencesManager.KEY_DATE_VISITA_PROCESSED);
            if (!TextUtils.isEmpty(fechaProcesamiento)) {
                Date ultimoProceso = new SimpleDateFormat("dd-MM-yyyy").parse(fechaProcesamiento);
                Date hoy = new Date();
                if (ultimoProceso.getYear() == hoy.getYear()
                        && ultimoProceso.getMonth() == hoy.getMonth()
                        && ultimoProceso.getDay() == hoy.getDay()) {

                    SnackbarManager.show(v, "Imposible continuar, ya procesó las visitas del día.", SnackbarManager.INFORMATION);
                    return;
                }
            }

            if (validate(v)) {

                Usuario oUsuario = new Usuario();
                oUsuario.correo = et_usuario.getText().toString();
                oUsuario.password = et_password.getText().toString();

                Where where = oUsuarioDao.getStatement();
                where.eq(Usuario.CORREO, oUsuario.correo);
                List<Usuario> aUsuarioValido = oUsuarioDao.queryForAll(where);

                if (aUsuarioValido.isEmpty()) {

                    //Verifica la conexión a internet.
                    if (!WifiConexionManager.hasInternet(this)) {
                        SnackbarManager.show(v, "Sin conexión a internet, imposible continuar.", SnackbarManager.ERROR);
                        //ToastManager.show(mContext, "Sin conexión a internet, imposible continuar", ToastManager.INFORMATION);
                        return;
                    }

                    new AsyncTaskValidarUsuario().execute(oUsuario);
                } else {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    //intent.putExtra("despachador", oUsuarioResult);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cancelar)
    public void cancelar() {
        ToastManager.show(LoginActivity.this, "Adios", ToastManager.INFORMATION);
        finish();
        System.exit(0);
    }

    private boolean validateEmail(CharSequence target) {
        if (target == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean validate(View view) {

        et_usuario.setError(null);
        et_password.setError(null);

        if (TextUtils.isEmpty(et_usuario.getText().toString())) {
            et_usuario.setError("Campo requerido");
            et_usuario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(et_password.getText().toString())) {
            et_password.setError("Campo requerido");
            et_password.requestFocus();
            return false;
        }

        if (!validateEmail(et_usuario.getText().toString()) || et_usuario.getText().toString().length() == 1) {
            et_usuario.setError("Formato incorrecto");
            et_usuario.requestFocus();
            return false;
        }

        //Oculta el teclado de la pantalla.
        hideKeyboard(et_password);


        return true;
    }

    private void setVarUserPasswd(String user, String passwd) {
        oSharedPreferencesHelper.setString(SharedPreferencesManager.KEY_USUARIO, user);
        oSharedPreferencesHelper.setString(SharedPreferencesManager.KEY_PASSWD, passwd);
    }

    private class AsyncTaskValidarUsuario extends AsyncTask<Usuario, Void, JSONArray> {

        private ProgressDialog oProgressDialog;
        private boolean bFallo;
        private String sError;

        public AsyncTaskValidarUsuario() {

            this.oProgressDialog = new ProgressDialog(LoginActivity.this);
            this.oProgressDialog.setTitle("Autenticando");
            this.oProgressDialog.setMessage("Por favor, espere...");
            this.oProgressDialog.setCancelable(false);
            this.bFallo = false;
            this.sError = "";
        }

        @Override
        protected void onPreExecute() {
            this.oProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(Usuario... params) {
            try {

                JSONArray jsonArrayResponse = oUsuarioBss.validarUsuario(params[0]);
                setVarUserPasswd(params[0].correo, params[0].password);

                if (jsonArrayResponse == null || jsonArrayResponse.length() == 0) {
                    setVarUserPasswd("", "");
                    this.bFallo = true;
                    this.sError = "Error al iniciar sesión, reintente.";
                    return null;
                }

                this.bFallo = false;
                return jsonArrayResponse;

            } catch (Exception e) {

                setVarUserPasswd("", "");
                bFallo = true;
                this.sError = e.getMessage().toLowerCase().equals("incorrecto") ? "Usuario y/o password incorrectos." : "Error al iniciar sesión, reintente.";
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONArray jsonArrayResponse) {
            try {

                if (bFallo) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(this.sError);
                    builder.setIcon(R.drawable.icon_error);
                    builder.setTitle("Error!");
                    builder.setPositiveButton("Aceptar", null);
                    builder.setCancelable(false);
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    int idUsuario = 0;
                    String nombresUsuario = "";

                    try {

                        for (int i = 0; i < jsonArrayResponse.length(); i++) {

                            JSONObject json = jsonArrayResponse.getJSONObject(i);
                            Usuario oUsuario = new Usuario();
                            oUsuario.idUsuario = json.getInt("id_usuario");
                            idUsuario = oUsuario.idUsuario;
                            oUsuario.nombres = json.getString("nombres");
                            nombresUsuario = oUsuario.nombres;
                            oUsuario.correo = json.getString("correo");
                            oUsuario.latitud = json.getString("latitud");
                            oUsuario.longitud = json.getString("longitud");
                            oUsuarioDao.create(oUsuario);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    this.oProgressDialog.dismiss();
                    SnackbarManager.show(getWindow().getCurrentFocus(), String.format("Bienvenido %s \n", nombresUsuario), SnackbarManager.SUCCESS);
                    new AsyncTaskCargaDatos().execute(idUsuario);

                }
            } finally {
                this.oProgressDialog.dismiss();
            }
            super.onPostExecute(jsonArrayResponse);
        }
    }

    private class AsyncTaskCargaDatos extends AsyncTask<Integer, Void, Boolean> {

        private ProgressDialog oProgressDialog;
        private String sError;

        public AsyncTaskCargaDatos() {

            this.oProgressDialog = new ProgressDialog(LoginActivity.this);
            this.oProgressDialog.setTitle("Obteniendo datos");
            this.oProgressDialog.setMessage("Por favor, espere...");
            this.oProgressDialog.setCancelable(false);
            this.sError = "";
        }

        @Override
        protected void onPreExecute() {
            this.oProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {

                //Búsqueda de visitas asignadas.
                JSONArray jsonArrayAsignaciones = new JSONArray();
                try {
                    jsonArrayAsignaciones = oCargaDatosBss.obtenerAsignacion(params[0], new Date());

                } catch (Exception e) {
                }
                if (jsonArrayAsignaciones != null && jsonArrayAsignaciones.length() > 0) {

                    //Mapeo de JSonArray a Lista de obj.
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(jsonArrayAsignaciones.toString());
                    TypeReference<List<AsignacionVisita>> typeRef = new TypeReference<List<AsignacionVisita>>() {
                    };
                    List<AsignacionVisita> aVisita = mapper.readValue(node.traverse(), typeRef);
                    oAsignacionVisitaDao.createFromList(aVisita);

                }

                //Búsqueda de comunas.
                JSONArray jsonArrayComunas = new JSONArray();
                try {
                    jsonArrayComunas = oComunaBss.obtenerComunas();

                } catch (Exception e) {

                }
                if (jsonArrayComunas == null || jsonArrayComunas.length() == 0) {
                    this.sError = "Sin comunas ingresadas en el sistema.";
                    return true;
                }

                for (int i = 0; i < jsonArrayComunas.length(); i++) {
                    JSONObject json = jsonArrayComunas.getJSONObject(i);
                    Comuna oComuna = new Comuna();
                    oComuna.idComuna = json.getInt("id");
                    oComuna.comuna = json.getString("comuna");
                    oComuna.idRegion = json.getInt("idregion");
                    oComunaDao.create(oComuna);
                }

                //Búsqueda de cultivos.
                JSONArray jsonArrayCultivo = new JSONArray();
                try {

                    jsonArrayCultivo = oCultivoBss.obtenerCultivos();

                    if (jsonArrayCultivo == null || jsonArrayCultivo.length() == 0) {
                        this.sError = "Sin cultivos ingresados en el sistema.";
                        return true;
                    }

                } catch (Exception e) {

                }

                for (int i = 0; i < jsonArrayCultivo.length(); i++) {
                    JSONObject json = jsonArrayCultivo.getJSONObject(i);
                    Cultivo oCultivo = new Cultivo();
                    oCultivo.idCultivo = json.getInt("id_cultivo");
                    oCultivo.cultivo = json.getString("cultivo");
                    oCultivo.estado = Boolean.parseBoolean(json.getString("estado"));
                    oCultivoDao.create(oCultivo);
                }

                return false;

            } catch (Exception e) {
                this.sError = "Error al obtener datos, reintente.";
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean bFallo) {
            try {

                if (bFallo) {

                    try {
                        oUsuarioDao.deleteAll();
                        oComunaDao.deleteAll();
                        oCultivoDao.deleteAll();
                        oAsignacionVisitaDao.deleteAll();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(this.sError);
                    builder.setIcon(R.drawable.icon_error);
                    builder.setTitle("Error!");
                    builder.setPositiveButton("Aceptar", null);
                    builder.setCancelable(false);
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    oSharedPreferencesHelper.setInt(SharedPreferencesManager.KEY_VISITA_ORDER, 0);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    startActivity(intent);
                    finish();
                }
            } finally {
                this.oProgressDialog.dismiss();
            }
            super.onPostExecute(bFallo);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
