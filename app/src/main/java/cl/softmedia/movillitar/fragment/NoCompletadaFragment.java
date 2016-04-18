package cl.softmedia.movillitar.fragment;

import android.app.AlertDialog;
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
import android.widget.EditText;

import com.j256.ormlite.stmt.Where;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cl.softmedia.movillitar.R;
import cl.softmedia.movillitar.activity.MainActivity;
import cl.softmedia.movillitar.dao.GenericDao;
import cl.softmedia.movillitar.domain.AsignacionVisita;
import cl.softmedia.movillitar.domain.EstadoVisita;
import cl.softmedia.movillitar.utils.SnackbarManager;

/**
 * Created by iroman on 18/03/2016.
 */
public class NoCompletadaFragment extends Fragment {

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.et_motivo_no_completada)
    EditText et_motivo_no_completada;

    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;
    private static AsignacionVisita oVisitaAsignada;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_no_completada, null);

        try {

            ButterKnife.inject(this, root);

            if (toolbar != null) {

                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                toolbar.setTitle("No completada");
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }

            oAsignacionVisitaDao = new GenericDao<>(getActivity(), AsignacionVisita.class);

            Bundle bundle = this.getArguments();

            if (bundle != null) {

                int idAsignacionVisita = bundle.getInt("idAsignacionVisita");
                Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID, idAsignacionVisita);
                oVisitaAsignada = (AsignacionVisita) oAsignacionVisitaDao.queryForAll(where).get(0);
                if (!TextUtils.isEmpty(AsignacionVisita.TEMA_TRATADO)) {
                    et_motivo_no_completada.setText(AsignacionVisita.TEMA_TRATADO);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @OnClick(R.id.btn_guardar_no_completada)
    public void save(final View v) {

        hideKeyboard(v);

        if (oVisitaAsignada.idEstadoVisita == EstadoVisita.GESTIONADA) {
            SnackbarManager.show(v, "Visita ya gestionada.", SnackbarManager.INFORMATION);
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
                    if (TextUtils.isEmpty(et_motivo_no_completada.getText())) {
                        et_motivo_no_completada.requestFocus();
                        SnackbarManager.show(v, "Ingrese motivo no completada.", SnackbarManager.ERROR);
                        return;
                    }

                    Where where = oAsignacionVisitaDao.getStatement();
                    where.eq(AsignacionVisita.ID, oVisitaAsignada.idAsignacionVisita);
                    oAsignacionVisitaDao.update(where, AsignacionVisita.TEMA_TRATADO, et_motivo_no_completada.getText().toString());

                    //Actualiza estado de la Visita.
                    //Actualiza estado de la Visita y fecha / hora de registros.l
                    oAsignacionVisitaDao.update(where, AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.NO_COMPLETADA);
                    oAsignacionVisitaDao.update(where, AsignacionVisita.FECHA_REGISTRO, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    oAsignacionVisitaDao.update(where, AsignacionVisita.HORA_REGISTRO, new SimpleDateFormat("HH:mm:ss").format(new Date()));


                    SnackbarManager.show(v, "Datos guardados exitosamente.", SnackbarManager.SUCCESS);

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