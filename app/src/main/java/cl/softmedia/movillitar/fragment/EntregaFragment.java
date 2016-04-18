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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.j256.ormlite.stmt.Where;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cl.softmedia.movillitar.R;
import cl.softmedia.movillitar.activity.MainActivity;
import cl.softmedia.movillitar.dao.GenericDao;
import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.domain.EstadoVisita;
import cl.softmedia.movillitar.domain.TipoCliente;
import cl.softmedia.movillitar.utils.LocationHelper;
import cl.softmedia.movillitar.utils.SharedPreferencesManager;
import cl.softmedia.movillitar.utils.SnackbarManager;

/**
 * Created by iroman on 18/03/2016.
 */
public class EntregaFragment extends Fragment {

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.et_rut_cliente)
    EditText et_rut_cliente;

    @InjectView(R.id.et_razon_social_cliente)
    EditText et_razon_social_cliente;

    @InjectView(R.id.et_proxima_visita_a_cliente)
    EditText et_proxima_visita_a_cliente;

    @InjectView(R.id.et_tema_tratado)
    EditText et_tema_tratado;

    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;
    private static AsignacionVisita oVisitaAsignada;
    private LocationHelper oLocationHelper;
    private SharedPreferencesManager oSharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_entrega, null);

        try {

            ButterKnife.inject(this, root);

            this.oAsignacionVisitaDao = new GenericDao<>(getActivity(), AsignacionVisita.class);
            this.oLocationHelper = new LocationHelper(getActivity());
            this.oSharedPreference = new SharedPreferencesManager(getActivity());

            if (toolbar != null) {

                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                toolbar.setTitle("Entrega");
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
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
                    et_razon_social_cliente.setText(oVisitaAsignada.cliente);
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.proximaVisita)) {
                    et_proxima_visita_a_cliente.setText(oVisitaAsignada.proximaVisita);
                }
                if (!TextUtils.isEmpty(oVisitaAsignada.temaTratado)) {
                    et_tema_tratado.setText(oVisitaAsignada.temaTratado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
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

    @OnClick(R.id.btn_guardar_entrega)
    public void save(final View v) {

        hideKeyboard(v);


//        if (oVisitaAsignada.idEstadoVisita == EstadoVisita.GESTIONADA) {
//            SnackbarManager.show(v, "Visita ya gestionada.", SnackbarManager.INFORMATION);
//            return;
//        }

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
                            SnackbarManager.show(v, "Rut Inválido.", SnackbarManager.ERROR);
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(et_razon_social_cliente.getText())) {
                        et_razon_social_cliente.requestFocus();
                        SnackbarManager.show(v, "Ingrese Nombre del cliente.", SnackbarManager.ERROR);
                        return;
                    }
                    if (TextUtils.isEmpty(et_tema_tratado.getText())) {
                        et_tema_tratado.requestFocus();
                        SnackbarManager.show(v, "Indique el tema tratado.", SnackbarManager.ERROR);
                        return;
                    }

                    terminar(v);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void terminar(View v) throws Exception {

        Where where = oAsignacionVisitaDao.getStatement();
        where.eq(AsignacionVisita.ID, oVisitaAsignada.idAsignacionVisita);
        oAsignacionVisitaDao.update(where, AsignacionVisita.RUT_CLIENTE, et_rut_cliente.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.CLIENTE, et_razon_social_cliente.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_PROXIMA_VISITA, et_proxima_visita_a_cliente.getText().toString());
        oAsignacionVisitaDao.update(where, AsignacionVisita.TEMA_TRATADO, et_tema_tratado.getText().toString());

        int order = oSharedPreference.getInt(SharedPreferencesManager.KEY_VISITA_ORDER) + 1;
        oSharedPreference.setInt(SharedPreferencesManager.KEY_VISITA_ORDER, order);

        oAsignacionVisitaDao.update(where, AsignacionVisita.ORDEN_VISITA, order);

        //Actualiza estado de la Visita y fecha / hora de registros.
        oAsignacionVisitaDao.update(where, AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.GESTIONADA);
        oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_REGISTRO, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        oAsignacionVisitaDao.update(where, AsignacionVisita.HORA_REGISTRO, new SimpleDateFormat("HH:mm:ss").format(new Date()));

        SnackbarManager.show(v, "Datos guardados exitosamente.", SnackbarManager.SUCCESS);

        getActivity().finish();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btn_limpiar_entrega)
    public void clear() {
        et_tema_tratado.setText("");
        et_proxima_visita_a_cliente.setText("");
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
}