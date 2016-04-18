package cl.softmedia.movillitar.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cl.softmedia.movillitar.R;
import cl.softmedia.movillitar.activity.MainActivity;
import cl.softmedia.movillitar.dao.GenericDao;
import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.domain.Comuna;
import cl.softmedia.movillitar.domain.Cultivo;
import cl.softmedia.movillitar.domain.EstadoVisita;
import cl.softmedia.movillitar.domain.TipoVisita;
import cl.softmedia.movillitar.domain.Usuario;
import cl.softmedia.movillitar.utils.GPSHelper;
import cl.softmedia.movillitar.utils.LocationHelper;
import cl.softmedia.movillitar.utils.SharedPreferencesManager;
import cl.softmedia.movillitar.utils.SnackbarManager;
import cl.softmedia.movillitar.utils.SpinnerObject;

/**
 * Created by iroman on 18/03/2016.
 */
public class NuevaVisitaFragment extends Fragment {

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.sp_cultivo)
    Spinner sp_cultivo;

    @InjectView(R.id.sp_fecha_cultivo_inicio)
    Spinner sp_fecha_cultivo_inicio;

    @InjectView(R.id.sp_fecha_cultivo_fin)
    Spinner sp_fecha_cultivo_fin;

    @InjectView(R.id.sp_comuna)
    Spinner sp_comuna;

    @InjectView(R.id.et_rut_cliente)
    EditText et_rut_cliente;

    @InjectView(R.id.et_nombres)
    EditText et_nombres;

    @InjectView(R.id.et_apellido_paterno)
    EditText et_apellido_paterno;

    @InjectView(R.id.et_apellido_materno)
    EditText et_apellido_materno;

    @InjectView(R.id.et_correo)
    EditText et_correo;

    @InjectView(R.id.et_telefono)
    EditText et_telefono;

    @InjectView(R.id.et_saldo)
    EditText et_saldo;

    @InjectView(R.id.et_proxima_visita_a_cliente)
    EditText et_proxima_visita_a_cliente;

    @InjectView(R.id.et_direccion)
    EditText et_direccion;

    @InjectView(R.id.et_superficie)
    EditText et_superficie;

    @InjectView(R.id.rdo_propia)
    RadioButton rdo_propia;

    @InjectView(R.id.rdo_arrendada)
    RadioButton rdo_arrendada;

    @InjectView(R.id.et_tema_tratado)
    EditText et_tema_tratado;

    private GenericDao<Cultivo> oCultivoDao;
    private GenericDao<Comuna> oComunaDao;
    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;
    private LocationHelper oLocationHelper;
    private GenericDao<Usuario> oUsuarioDao;
    private SharedPreferencesManager oSharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_negociacion, null);

        try {

            ButterKnife.inject(this, root);

            if (toolbar != null) {

                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                toolbar.setTitle("Nueva Visita - Negociación");
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }

            this.oCultivoDao = new GenericDao<>(getActivity(), Cultivo.class);
            this.oComunaDao = new GenericDao<>(getActivity(), Comuna.class);
            this.oAsignacionVisitaDao = new GenericDao<>(getActivity(), AsignacionVisita.class);
            this.oUsuarioDao = new GenericDao<>(getActivity(), Usuario.class);
            this.oLocationHelper = new LocationHelper(getActivity());
            this.oSharedPreference = new SharedPreferencesManager(getActivity());

            et_superficie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            String number = et_superficie.getText().toString().replace(",", ".");
                            DecimalFormat df = new DecimalFormat();
                            df.setMaximumFractionDigits(2);
                            et_superficie.setText(df.format(Float.parseFloat(number)).replace(",", "."));

                        } catch (Exception e) {
                            et_superficie.setText("");
                        }
                    }
                }
            });

            fillSpinnerCultivo();
            fillSpinnerMouth();
            fillSpinnerComuna();

            et_saldo.setEnabled(false);
            et_rut_cliente.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    editable.toString().replace(".", "");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    boolean bCancel = false;

    @OnClick(R.id.et_proxima_visita_a_cliente)
    public void showDialogDatePicker(View v) {

        hideKeyboard(v);

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        bCancel = false;

        final DatePickerDialog dtd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int mounth, int day) {

                if (!bCancel)
                    et_proxima_visita_a_cliente.setText(String.format("%1$02d-%2$02d-%3$04d", day, mounth + 1, year));
            }
        }, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dtd.setTitle("Fecha próxima visita");
        dtd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bCancel = true;
                dtd.dismiss();
            }
        });
        dtd.show();
    }

    private void fillSpinnerMouth() {
        List<SpinnerObject> aSpinner = new ArrayList<>();
        aSpinner.add(new SpinnerObject(-1, "Seleccione"));
        aSpinner.add(new SpinnerObject(1, "Enero"));
        aSpinner.add(new SpinnerObject(2, "Febrero"));
        aSpinner.add(new SpinnerObject(3, "Marzo"));
        aSpinner.add(new SpinnerObject(4, "Abril"));
        aSpinner.add(new SpinnerObject(5, "Mayo"));
        aSpinner.add(new SpinnerObject(6, "Junio"));
        aSpinner.add(new SpinnerObject(7, "Julio"));
        aSpinner.add(new SpinnerObject(8, "Agosto"));
        aSpinner.add(new SpinnerObject(9, "Septiembre"));
        aSpinner.add(new SpinnerObject(10, "Octubre"));
        aSpinner.add(new SpinnerObject(11, "Noviembre"));
        aSpinner.add(new SpinnerObject(12, "Dicimenbre"));
        ArrayAdapter<SpinnerObject> adapterMes = new ArrayAdapter<SpinnerObject>(getActivity(), android.R.layout.simple_list_item_1, aSpinner);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_fecha_cultivo_inicio.setAdapter(adapterMes);
        sp_fecha_cultivo_fin.setAdapter(adapterMes);
    }


    private void fillSpinnerCultivo() throws Exception {
        List<Cultivo> aCultivo = oCultivoDao.queryForAll();
        List<SpinnerObject> aSpinner = new ArrayList<>();
        aSpinner.add(new SpinnerObject(-1, "Seleccione"));
        for (Cultivo oCultivo : aCultivo) {
            aSpinner.add(new SpinnerObject(oCultivo.idCultivo, oCultivo.cultivo));
        }
        ArrayAdapter<SpinnerObject> adapterUsuario = new ArrayAdapter<SpinnerObject>(getActivity(), android.R.layout.simple_list_item_1, aSpinner);
        adapterUsuario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cultivo.setAdapter(adapterUsuario);
    }

    private void fillSpinnerComuna() throws Exception {
        List<Comuna> aComuna = oComunaDao.queryForAll();
        List<SpinnerObject> aSpinner = new ArrayList<>();
        aSpinner.add(new SpinnerObject(-1, "Seleccione"));
        for (Comuna oComuna : aComuna) {
            aSpinner.add(new SpinnerObject(oComuna.idComuna, oComuna.comuna));
        }
        ArrayAdapter<SpinnerObject> adapterComuna = new ArrayAdapter<SpinnerObject>(getActivity(), android.R.layout.simple_list_item_1, aSpinner);
        adapterComuna.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_comuna.setAdapter(adapterComuna);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getActivity().onBackPressed();
        return false;
    }

    private boolean validateRut(String rut) {
        boolean validacion = false;
        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (Exception e) {
        }
        return validacion;
    }

    @OnClick(R.id.btn_guardar_negociacion)
    public void save(final View view) {

        hideKeyboard(view);

        if (!GPSHelper.IsActive(getActivity())) {
            SnackbarManager.show(view, "Para continuar, active GPS.", SnackbarManager.WARNING);
            GPSHelper.turnOn(getActivity());
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Atención!");
        builder.setMessage("¿Desea guardar los cambios realizados?");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    if (!TextUtils.isEmpty(et_rut_cliente.getText())) {

                        et_rut_cliente.setText(et_rut_cliente.getText().toString().replace(".", ""));
                        if (!validateRut(et_rut_cliente.getText().toString())) {
                            et_rut_cliente.requestFocus();
                            SnackbarManager.show(view, "Rut Inválido.", SnackbarManager.ERROR);
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(et_nombres.getText())) {
                        et_nombres.requestFocus();
                        SnackbarManager.show(view, "Ingrese Nombres.", SnackbarManager.ERROR);
                        return;
                    }
                    if (TextUtils.isEmpty(et_apellido_paterno.getText())) {
                        et_apellido_paterno.requestFocus();
                        SnackbarManager.show(view, "Ingrese Apellido Paterno.", SnackbarManager.ERROR);
                        return;
                    }
//                    if (TextUtils.isEmpty(et_apellido_materno.getText())) {
//                        et_apellido_materno.requestFocus();
//                        SnackbarManager.show(view, "Ingrese Apellido Materno.", SnackbarManager.ERROR);
//                        return;
//                    }
                    if (!TextUtils.isEmpty(et_correo.getText()) && !validateEmail(et_correo.getText().toString())) {
                        et_correo.requestFocus();
                        SnackbarManager.show(view, "Formato de correo inválido.", SnackbarManager.ERROR);
                        return;
                    }
                    if (!TextUtils.isEmpty(et_telefono.getText()) && et_telefono.getText().toString().length() != 9) {
                        et_telefono.requestFocus();
                        SnackbarManager.show(view, "Teléfono debe tener 9 números.", SnackbarManager.ERROR);
                        return;
                    }
//                    if (TextUtils.isEmpty(et_saldo.getText())) {
//                        et_saldo.requestFocus();
//                        SnackbarManager.show(view, "Ingrese saldo.", SnackbarManager.ERROR);
//                        return;
//                    }
                    if (TextUtils.isEmpty(et_direccion.getText())) {
                        et_direccion.requestFocus();
                        SnackbarManager.show(view, "Indique dirección.", SnackbarManager.ERROR);
                        return;
                    }
                    if (((SpinnerObject) sp_comuna.getSelectedItem()).getDataBaseId() == -1) {
                        sp_comuna.requestFocus();
                        SnackbarManager.show(view, "Seleccione Comuna.", SnackbarManager.ERROR);
                        return;
                    }
                    if (((SpinnerObject) sp_cultivo.getSelectedItem()).getDataBaseId() == -1) {
                        sp_cultivo.requestFocus();
                        SnackbarManager.show(view, "Seleccione Cultivo.", SnackbarManager.ERROR);
                        return;
                    }
                    if (((SpinnerObject) sp_fecha_cultivo_inicio.getSelectedItem()).getDataBaseId() == -1) {
                        sp_fecha_cultivo_inicio.requestFocus();
                        SnackbarManager.show(view, "Seleccione Inicio cultivo.", SnackbarManager.ERROR);
                        return;
                    }
                    if (((SpinnerObject) sp_fecha_cultivo_fin.getSelectedItem()).getDataBaseId() == -1) {
                        sp_fecha_cultivo_fin.requestFocus();
                        SnackbarManager.show(view, "Seleccione Fin cultivo.", SnackbarManager.ERROR);
                        return;
                    }
                    if (TextUtils.isEmpty(et_superficie.getText())) {
                        et_superficie.requestFocus();
                        SnackbarManager.show(view, "Ingrese superficie.", SnackbarManager.ERROR);
                        return;
                    }
                    if (TextUtils.isEmpty(et_tema_tratado.getText())) {
                        et_tema_tratado.requestFocus();
                        SnackbarManager.show(view, "Ingrese el tema tratado.", SnackbarManager.ERROR);
                        return;
                    }
                    if (!rdo_arrendada.isChecked() && !rdo_propia.isChecked()) {
                        SnackbarManager.show(view, "Indique si terrero es propio o arrendado.", SnackbarManager.ERROR);
                        return;
                    }

                    AsignacionVisita oAsignacionVisita = new AsignacionVisita();
                    oAsignacionVisita.rutCliente = et_rut_cliente.getText().toString();
                    oAsignacionVisita.cliente = String.format("%s %s %s", et_nombres.getText().toString(), et_apellido_paterno.getText().toString(), et_apellido_materno.getText().toString());
                    oAsignacionVisita.emailCliente = et_correo.getText().toString();
                    oAsignacionVisita.telefonoCliente = Integer.parseInt(et_telefono.getText().toString());
                    oAsignacionVisita.saldo = 0;
                    oAsignacionVisita.direccion = et_direccion.getText().toString();
                    oAsignacionVisita.proximaVisita = et_proxima_visita_a_cliente.getText().toString();
                    oAsignacionVisita.idComuna = ((SpinnerObject) sp_comuna.getSelectedItem()).getDataBaseId();
                    oAsignacionVisita.comuna = ((SpinnerObject) sp_comuna.getSelectedItem()).getDataBaseValue();
                    oAsignacionVisita.idCultivo = ((SpinnerObject) sp_cultivo.getSelectedItem()).getDataBaseId();
                    oAsignacionVisita.cultivo = ((SpinnerObject) sp_cultivo.getSelectedItem()).getDataBaseValue();
                    oAsignacionVisita.fechaInicioCultivo = ((SpinnerObject) sp_fecha_cultivo_inicio.getSelectedItem()).getDataBaseId();
                    oAsignacionVisita.fechaFinCultivo = ((SpinnerObject) sp_fecha_cultivo_fin.getSelectedItem()).getDataBaseId();
                    oAsignacionVisita.temaTratado = et_tema_tratado.getText().toString();
                    oAsignacionVisita.idVendedor = oUsuarioDao.queryForAll().get(0).idUsuario;
                    oAsignacionVisita.tipoCliente = "4";

                    String hoy = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    oAsignacionVisita.fechaVisita = hoy;
                    oAsignacionVisita.fechaRegistro = hoy;
                    oAsignacionVisita.horaRegistro = new SimpleDateFormat("HH:mm:ss").format(new Date());

                    if (rdo_arrendada.isChecked()) {
                        oAsignacionVisita.hectariasPropio = "0";
                        oAsignacionVisita.hectariasArrendado = et_superficie.getText().toString();
                        oAsignacionVisita.terrenoArrendado = "Si";
                        oAsignacionVisita.terrenoPropio = "No";
                    } else if (rdo_propia.isChecked()) {
                        oAsignacionVisita.hectariasPropio = et_superficie.getText().toString();
                        oAsignacionVisita.hectariasArrendado = "0";
                        oAsignacionVisita.terrenoArrendado = "No";
                        oAsignacionVisita.terrenoPropio = "Si";
                    }
                    oAsignacionVisita.idEstadoVisita = EstadoVisita.GESTIONADA;
                    oAsignacionVisita.idTipoVisita = TipoVisita.NEGOCIACION;
                    oAsignacionVisita.idVisita = 0;
                    oAsignacionVisita.ordenVisita = (oSharedPreference.getInt(SharedPreferencesManager.KEY_VISITA_ORDER) + 1);

                    double latitud = 0.0;
                    double longitud = 0.0;
                    Location mGPSLocation = oLocationHelper.getLocation(LocationManager.GPS_PROVIDER);
                    if (mGPSLocation != null) {
                        latitud = mGPSLocation.getLatitude();
                        longitud = mGPSLocation.getLongitude();

                    } else {
                        Location mNETLocation = oLocationHelper.getLocation(LocationManager.NETWORK_PROVIDER);
                        if (mNETLocation != null) {
                            latitud = mNETLocation.getLatitude();
                            longitud = mNETLocation.getLongitude();
                        }
                    }

                    oAsignacionVisita.latitudVisita = String.format("%s", latitud);
                    oAsignacionVisita.longitudVisita = String.format("%s", longitud);
                    oAsignacionVisita.cumpleGPS = "Si";

                    oAsignacionVisitaDao.create(oAsignacionVisita);

                    SnackbarManager.show(view, "Datos guardados exitosamente.", SnackbarManager.SUCCESS);

                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean validateEmail(CharSequence target) {
        if (target == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}