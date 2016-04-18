package cl.softmedia.movillitar.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.j256.ormlite.stmt.Where;

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
import cl.softmedia.movillitar.domain.TipoCliente;
import cl.softmedia.movillitar.utils.LocationHelper;
import cl.softmedia.movillitar.utils.SnackbarManager;
import cl.softmedia.movillitar.utils.SpinnerObject;

/**
 * Created by iroman on 18/03/2016.
 */
public class NegociacionFragment extends Fragment {

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
    private static AsignacionVisita oVisitaAsignada;
    private LocationHelper oLocationHelper;


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

                toolbar.setTitle("Negociación");
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }

            this.oCultivoDao = new GenericDao<>(getActivity(), Cultivo.class);
            this.oComunaDao = new GenericDao<>(getActivity(), Comuna.class);
            this.oAsignacionVisitaDao = new GenericDao<>(getActivity(), AsignacionVisita.class);
            this.oLocationHelper = new LocationHelper(getActivity());

            et_superficie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            String number = et_superficie.getText().toString().replace(",", ".");
                            DecimalFormat df = new DecimalFormat("0.00");
                            df.setMaximumFractionDigits(2);
                            et_superficie.setText(String.format("%s", Double.parseDouble(number)));

                        } catch (Exception e) {
                            et_superficie.setText("");
                        }
                    }
                }
            });

            fillSpinnerCultivo();
            fillSpinnerMouth();
            fillSpinnerComuna();

            et_rut_cliente.setEnabled(false);

            Bundle bundle = this.getArguments();

            if (bundle != null) {

                int idAsignacionVisita = bundle.getInt("idAsignacionVisita");
                Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID, idAsignacionVisita);
                oVisitaAsignada = (AsignacionVisita) oAsignacionVisitaDao.queryForAll(where).get(0);
                if (!TextUtils.isEmpty(oVisitaAsignada.rutCliente)) {
                    et_rut_cliente.setText(oVisitaAsignada.rutCliente.replace(".", ""));
                    et_rut_cliente.setEnabled(false);
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.cliente)) {
                    String[] nombres = oVisitaAsignada.cliente.split(" ");
                    et_nombres.setText(nombres[0]);
                    et_apellido_paterno.setText(nombres[1]);
                    et_apellido_materno.setText(nombres.length > 2 ? nombres[2] : "");
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.emailCliente)) {
                    et_correo.setText(oVisitaAsignada.emailCliente);
                }
                if (oVisitaAsignada.telefonoCliente != 0) {
                    et_telefono.setText(oVisitaAsignada.telefonoCliente + "");
                }
                if (oVisitaAsignada.saldo != 0) {
                    et_saldo.setText(oVisitaAsignada.saldo + "");
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.proximaVisita)) {
                    et_proxima_visita_a_cliente.setText(oVisitaAsignada.proximaVisita);
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.direccion)) {
                    et_direccion.setText(oVisitaAsignada.direccion);
                }
                for (int i = 0; i < sp_comuna.getCount(); i++) {
                    if (((SpinnerObject) sp_comuna.getItemAtPosition(i)).getDataBaseId() == oVisitaAsignada.idComuna) {
                        sp_comuna.setSelection(i);
                        break;
                    }
                    sp_comuna.setSelection(0, false);
                }
                for (int i = 0; i < sp_cultivo.getCount(); i++) {
                    if (((SpinnerObject) sp_cultivo.getItemAtPosition(i)).getDataBaseId() == oVisitaAsignada.idCultivo) {
                        sp_cultivo.setSelection(i);
                        break;
                    }
                    sp_cultivo.setSelection(0, false);
                }
                for (int i = 0; i < sp_fecha_cultivo_inicio.getCount(); i++) {
                    if (((SpinnerObject) sp_fecha_cultivo_inicio.getItemAtPosition(i)).getDataBaseId() == oVisitaAsignada.fechaInicioCultivo) {
                        sp_fecha_cultivo_inicio.setSelection(i);
                        break;
                    }
                    sp_fecha_cultivo_inicio.setSelection(0, false);
                }
                for (int i = 0; i < sp_fecha_cultivo_fin.getCount(); i++) {
                    if (((SpinnerObject) sp_fecha_cultivo_fin.getItemAtPosition(i)).getDataBaseId() == oVisitaAsignada.fechaFinCultivo) {
                        sp_fecha_cultivo_fin.setSelection(i);
                        break;
                    }
                    sp_fecha_cultivo_fin.setSelection(0, false);
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.terrenoPropio) && oVisitaAsignada.terrenoPropio.toLowerCase().trim().equals("si")) {
                    rdo_propia.setChecked(true);
                    et_superficie.setText(oVisitaAsignada.hectariasPropio + "");
                } else if (!TextUtils.isEmpty(oVisitaAsignada.terrenoArrendado) && oVisitaAsignada.terrenoArrendado.toLowerCase().trim().equals("si")) {
                    rdo_arrendada.setChecked(true);
                    et_superficie.setText(oVisitaAsignada.hectariasArrendado + "");
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.temaTratado)) {
                    et_tema_tratado.setText(oVisitaAsignada.temaTratado);
                }

                sp_fecha_cultivo_fin.setOnItemSelectedListener(onItemSelectedListener);
                sp_fecha_cultivo_inicio.setOnItemSelectedListener(onItemSelectedListener);
                sp_comuna.setOnItemSelectedListener(onItemSelectedListener);
                sp_cultivo.setOnItemSelectedListener(onItemSelectedListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard(view);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


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

    double latitud = 0.0;
    double longitud = 0.0;
    boolean bDiferenciaMayorA800 = false;

    @OnClick(R.id.btn_guardar_negociacion)
    public void save(final View view) {

        hideKeyboard(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Atención!");
        builder.setMessage("¿Desea guardar los cambios realizados?");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    if (!TextUtils.isEmpty(et_rut_cliente.getText()) && Integer.parseInt(oVisitaAsignada.tipoCliente) == TipoCliente.ACTIVO) {

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

                    terminar(view);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void terminar(View view) throws Exception {

        Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID, oVisitaAsignada.idAsignacionVisita);
        oAsignacionVisitaDao.update(where, AsignacionVisita.RUT_CLIENTE, et_rut_cliente.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.CLIENTE, String.format("%s %s %s", et_nombres.getText().toString(), et_apellido_paterno.getText().toString(), et_apellido_materno.getText().toString()));
        oAsignacionVisitaDao.update(where, AsignacionVisita.EMAIL_CLIENTE, et_correo.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.TELEFONO_CLIENTE, et_telefono.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.SALDO, et_saldo.getText().toString().equals("") ? 0 : Integer.parseInt(et_saldo.getText().toString()));
        oAsignacionVisitaDao.update(where, AsignacionVisita.DIRECCION, et_direccion.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_PROXIMA_VISITA, et_proxima_visita_a_cliente.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.ID_COMUNA, ((SpinnerObject) sp_comuna.getSelectedItem()).getDataBaseId());
        oAsignacionVisitaDao.update(where, AsignacionVisita.COMUNA, ((SpinnerObject) sp_comuna.getSelectedItem()).getDataBaseValue());
        oAsignacionVisitaDao.update(where, AsignacionVisita.ID_CULTIVO, ((SpinnerObject) sp_cultivo.getSelectedItem()).getDataBaseId());
        oAsignacionVisitaDao.update(where, AsignacionVisita.CULTIVO, ((SpinnerObject) sp_cultivo.getSelectedItem()).getDataBaseValue());
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_INICIO_CULTIVO, ((SpinnerObject) sp_fecha_cultivo_inicio.getSelectedItem()).getDataBaseId());
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_FIN_CULTIVO, ((SpinnerObject) sp_fecha_cultivo_fin.getSelectedItem()).getDataBaseId());
        oAsignacionVisitaDao.update(where, AsignacionVisita.TEMA_TRATADO, et_tema_tratado.getText().toString());
        if (rdo_arrendada.isChecked()) {
            oAsignacionVisitaDao.update(where, AsignacionVisita.HECTARIAS_PROPIO, "0.0");
            oAsignacionVisitaDao.update(where, AsignacionVisita.HECTARIAS_ARRENADO, et_superficie.getText().toString());
            oAsignacionVisitaDao.update(where, AsignacionVisita.TERRENO_ARRENDADO, "Si");
            oAsignacionVisitaDao.update(where, AsignacionVisita.TERRENO_PROPIO, "No");
        } else if (rdo_propia.isChecked()) {
            oAsignacionVisitaDao.update(where, AsignacionVisita.HECTARIAS_PROPIO, et_superficie.getText().toString());
            oAsignacionVisitaDao.update(where, AsignacionVisita.HECTARIAS_ARRENADO, "");
            oAsignacionVisitaDao.update(where, AsignacionVisita.TERRENO_ARRENDADO, "No");
            oAsignacionVisitaDao.update(where, AsignacionVisita.TERRENO_PROPIO, "Si");
        }

        //Actualiza estado de la Visita y fecha / hora de registros.l
        oAsignacionVisitaDao.update(where, AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.GESTIONADA);
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_REGISTRO, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        oAsignacionVisitaDao.update(where, AsignacionVisita.HORA_REGISTRO, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        SnackbarManager.show(view, "Datos guardados exitosamente.", SnackbarManager.SUCCESS);

        getActivity().finish();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean validateEmail(CharSequence target) {
        if (target == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}