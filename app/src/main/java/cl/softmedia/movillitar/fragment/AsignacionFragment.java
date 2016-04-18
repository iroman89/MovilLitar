package cl.softmedia.movillitar.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.stmt.Where;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cl.softmedia.movillitar.R;
import cl.softmedia.movillitar.activity.LoginActivity;
import cl.softmedia.movillitar.bss.AsignacionVisitaBss;
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
import cl.softmedia.movillitar.utils.WifiConexionManager;

/**
 * Created by iroman on 18/03/2016.
 */
public class AsignacionFragment extends Fragment {

    @InjectView(R.id.lv_visitas_asignadas)
    ListView lv_visitas_asignadas;

    @InjectView(R.id.btn_cerrar_todo)
    Button btn_cerrar_todo;

    @InjectView(R.id.btn_nueva_visita)
    Button btn_nueva_visita;

    @InjectView(R.id.txt_sin_visitas)
    TextView txt_sin_visitas;

    private GenericDao<AsignacionVisita> oAsignacionVisitaDao;
    private GenericDao<Comuna> oComunaDao;
    private GenericDao<Cultivo> oCultivoDao;
    private GenericDao<Usuario> oUsuarioDao;
    private ListAdapter listAdapter;
    private AsignacionVisitaBss oAsignacionVisitaBss;
    private SharedPreferencesManager oSharedPreference;
    private LocationHelper oLocationHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_asignacion, null);

        try {

            ButterKnife.inject(this, root);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Visitas Asignadas");
            this.oAsignacionVisitaDao = new GenericDao<>(getActivity(), AsignacionVisita.class);
            this.oAsignacionVisitaBss = new AsignacionVisitaBss(getActivity());
            this.oComunaDao = new GenericDao<>(getActivity(), Comuna.class);
            this.oCultivoDao = new GenericDao<>(getActivity(), Cultivo.class);
            this.oUsuarioDao = new GenericDao<>(getActivity(), Usuario.class);
            this.oSharedPreference = new SharedPreferencesManager(getActivity());
            this.oLocationHelper = new LocationHelper(getActivity());

            List<AsignacionVisita> aVisita = oAsignacionVisitaDao.queryForAll();
            listAdapter = new ListAdapter(getActivity(), aVisita);
            lv_visitas_asignadas.setAdapter(listAdapter);

            Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ACTIVA_NUEVA_VISITA, "1");
            List<AsignacionVisita> activaCreacionList = oAsignacionVisitaDao.queryForAll(where);
            if (!activaCreacionList.isEmpty()) {
                btn_nueva_visita.setClickable(true);
                txt_sin_visitas.setVisibility(View.GONE);
            } else {
                txt_sin_visitas.setVisibility(View.VISIBLE);
            }

            hideKeyboard(getView());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }


    public void reloadAllData() {

        try {

            List<AsignacionVisita> aVisita = oAsignacionVisitaDao.queryForAll();
            listAdapter = new ListAdapter(getActivity(), aVisita);
            lv_visitas_asignadas.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_nueva_visita)
    public void create(View view) {
        hideKeyboard(view);
        try {

            Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ACTIVA_NUEVA_VISITA, 1);
            List<AsignacionVisita> aAsignacionVisita = oAsignacionVisitaDao.queryForAll(where);

            if (!aAsignacionVisita.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Nueva Visita");
                builder.setIcon(R.drawable.icon_warning);
                builder.setMessage("Se creará una Visita y deberá ingresar todos sus datos, ¿Desea continuar?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Fragment fragment = getFragmentManager().findFragmentByTag("nueva_visita");
                        if (fragment == null) {
                            NuevaVisitaFragment nuevaVisitaFragment = new NuevaVisitaFragment();
                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                    .replace(android.R.id.content, nuevaVisitaFragment, "nueva_visita").addToBackStack(null).commit();
                        }

                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cerrar_todo)
    public void cerrarDia(final View view) {

        hideKeyboard(view);
        try {

            if (!WifiConexionManager.hasInternet(AsignacionFragment.this)) {
                SnackbarManager.show(view, "Imposible continuar, debe tener conexión a internet", SnackbarManager.WARNING);
                return;
            }

            Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.NO_GESTIONADA);
            final List<AsignacionVisita> aAsignacionVisitaNoGestionada = oAsignacionVisitaDao.queryForAll(where);

            if (!aAsignacionVisitaNoGestionada.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Atención!");
                builder.setIcon(R.drawable.icon_warning);
                builder.setMessage("Tiene Visitas sin gestionar, ¿Desea cerrar de todas formas?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new AsyncTaskCerrarVisitas().execute();
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                new AsyncTaskCerrarVisitas().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    double latitud = 0.0;
    double longitud = 0.0;
    boolean bDiferenciaMayorA800 = false;

    @OnItemClick(R.id.lv_visitas_asignadas)
    public void gestionarVisita(final AdapterView<?> adapterView, final View view, final int index, long l) {

        final AsignacionVisita oAsignacionVisitaTemp = ((AsignacionVisita) adapterView.getItemAtPosition(index));
        if (oAsignacionVisitaTemp.idVisita == -1) {
            return;
        }

        Fragment fragment = getFragmentManager().findFragmentByTag("no_completada");
        if (fragment != null) {
            return;
        }
        fragment = getFragmentManager().findFragmentByTag("entregada");
        if (fragment != null) {
            return;
        }
        fragment = getFragmentManager().findFragmentByTag("negociacion");
        if (fragment != null) {
            return;
        }

        if (!GPSHelper.IsActive(getActivity())) {
            SnackbarManager.show(view, "Para continuar, active GPS.", SnackbarManager.WARNING);
            GPSHelper.turnOn(getActivity());
            return;
        }

        CharSequence[] items;

        if (oAsignacionVisitaTemp.idTipoVisita == TipoVisita.NEGOCIACION) {
            items = new CharSequence[]{"Negociación", "Visita No Completada"};
        } else if (oAsignacionVisitaTemp.idTipoVisita == TipoVisita.ENTREGA) {
            items = new CharSequence[]{"Entrega", "Visita No Completada"};
        } else {
            items = new CharSequence[]{"Entrega", "Negociación", "Visita No Completada"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Gestión de Visitas");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int indice) {

                verVisita(oAsignacionVisitaTemp, indice);

            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void negociacionOEntrega(final AsignacionVisita oAsignacionVisita, final int tipoVisita) {

        try {

            latitud = 0.0;
            longitud = 0.0;
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
            double diferenciaKm = 0.0;
            bDiferenciaMayorA800 = false;
            if (!oAsignacionVisita.latitudCliente.equals("0.0") || !oAsignacionVisita.longitudCliente.equals("0.0")) {

                Location locationA = new Location("");
                Location locationB = new Location("");
                locationA.setLatitude(Double.parseDouble(oAsignacionVisita.latitudCliente));
                locationA.setLongitude(Double.parseDouble(oAsignacionVisita.longitudCliente));

                if (latitud != 0.0 && longitud != 0.0) {

                    locationB.setLatitude(latitud);
                    locationB.setLongitude(longitud);
                    diferenciaKm = locationA.distanceTo(locationB);
                    if (diferenciaKm > 800) {
                        bDiferenciaMayorA800 = true;
                    }
                }
            }

            Where where = oAsignacionVisitaDao.getStatement();
            where.eq(AsignacionVisita.ID, oAsignacionVisita.idAsignacionVisita);
            oAsignacionVisitaDao.update(where, AsignacionVisita.CUMPLE_GPS, bDiferenciaMayorA800 == true ? "No" : "Si");
            oAsignacionVisitaDao.update(where, AsignacionVisita.LATITUD_VISITA, String.format("%s", latitud));
            oAsignacionVisitaDao.update(where, AsignacionVisita.LONGITUD_VISITA, String.format("%s", longitud));

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            String diferenciaFormateada = df.format(diferenciaKm);

            String lCliente = String.format("%s,%s", oAsignacionVisita.latitudCliente, oAsignacionVisita.longitudCliente);
            String lVisita = String.format("%s, %s", latitud, longitud);

            if (bDiferenciaMayorA800) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.drawable.icon_warning);
                builder.setTitle("Atención!");
                builder.setMessage(String.format("USTED NO ESTA CUMPLIENDO GPS. \nExiste una diferencia de más de %s mts. desde el punto registrado al que ud. esta intentando registrar. \n\n(Registrado:\n\t%s).\n(Actual:\n\t%s). \n\n¿Desea continuar sin cumplir GPS?",
                        diferenciaFormateada, lCliente, lVisita));
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (tipoVisita == TipoVisita.NEGOCIACION) {
                            Fragment fragment2 = getFragmentManager().findFragmentByTag("negociacion");
                            if (fragment2 == null) {
                                NegociacionFragment negociacionFragment = new NegociacionFragment();
                                Bundle b = new Bundle();
                                b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                                negociacionFragment.setArguments(b);
                                getFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                        .replace(android.R.id.content, negociacionFragment, "negociacion").addToBackStack(null).commit();
                            }

                        } else if (tipoVisita == TipoVisita.ENTREGA) {
                            Fragment fragment1 = getFragmentManager().findFragmentByTag("entrega");
                            if (fragment1 == null) {
                                EntregaFragment entregaFragment = new EntregaFragment();
                                Bundle b = new Bundle();
                                b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                                entregaFragment.setArguments(b);
                                getFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                        .replace(android.R.id.content, entregaFragment, "entrega").addToBackStack(null).commit();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Rechazar", null);
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                if (tipoVisita == TipoVisita.NEGOCIACION) {
                    Fragment fragment2 = getFragmentManager().findFragmentByTag("negociacion");
                    if (fragment2 == null) {
                        NegociacionFragment negociacionFragment = new NegociacionFragment();
                        Bundle b = new Bundle();
                        b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                        negociacionFragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                .replace(android.R.id.content, negociacionFragment, "negociacion").addToBackStack(null).commit();
                    }

                } else if (tipoVisita == TipoVisita.ENTREGA) {
                    Fragment fragment1 = getFragmentManager().findFragmentByTag("entrega");
                    if (fragment1 == null) {
                        EntregaFragment entregaFragment = new EntregaFragment();
                        Bundle b = new Bundle();
                        b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                        entregaFragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                .replace(android.R.id.content, entregaFragment, "entrega").addToBackStack(null).commit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verVisita(final AsignacionVisita oAsignacionVisita, int indice) {


        if (oAsignacionVisita.idTipoVisita == TipoVisita.NEGOCIACION) {
            switch (indice) {
                case 0:
                    negociacionOEntrega(oAsignacionVisita, TipoVisita.NEGOCIACION);
                    break;
                case 1:

                    Fragment fragment3 = getFragmentManager().findFragmentByTag("no_completada");
                    if (fragment3 == null) {
                        NoCompletadaFragment noCompletadaFragment = new NoCompletadaFragment();
                        Bundle b = new Bundle();
                        b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                        noCompletadaFragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                .replace(android.R.id.content, noCompletadaFragment, "no_completada").addToBackStack(null).commit();
                    }
                    break;
            }
        } else if (oAsignacionVisita.idTipoVisita == TipoVisita.ENTREGA) {
            switch (indice) {
                case 0:

                    negociacionOEntrega(oAsignacionVisita, TipoVisita.ENTREGA);

                    break;
                case 1:

                    Fragment fragment3 = getFragmentManager().findFragmentByTag("no_completada");
                    if (fragment3 == null) {
                        NoCompletadaFragment noCompletadaFragment = new NoCompletadaFragment();
                        Bundle b = new Bundle();
                        b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                        noCompletadaFragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                .replace(android.R.id.content, noCompletadaFragment, "no_completada").addToBackStack(null).commit();
                    }
                    break;
            }
        } else {
            switch (indice) {
                case 0:

                    negociacionOEntrega(oAsignacionVisita, TipoVisita.ENTREGA);

                    break;
                case 1:

                    negociacionOEntrega(oAsignacionVisita, TipoVisita.NEGOCIACION);

                    break;
                case 2:

                    Fragment fragment3 = getFragmentManager().findFragmentByTag("no_completada");
                    if (fragment3 == null) {
                        NoCompletadaFragment noCompletadaFragment = new NoCompletadaFragment();
                        Bundle b = new Bundle();
                        b.putInt("idAsignacionVisita", oAsignacionVisita.idAsignacionVisita);
                        noCompletadaFragment.setArguments(b);
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                                .replace(android.R.id.content, noCompletadaFragment, "no_completada").addToBackStack(null).commit();
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment = getFragmentManager().findFragmentByTag("asignacion");

        switch (item.getItemId()) {

            case R.id.action_home:
                if (fragment != null) {
                    getFragmentManager().beginTransaction()
                            .remove(fragment).setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top).commit();
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
                return false;

            case R.id.action_menu_presenter:
                //new AsyncTaskObtenerPedido().execute(oPreferences.getInt(SharedPreferenceHelper.KEY_ID_USUARIO));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class ListAdapter extends ArrayAdapter {

        @InjectView(R.id.txtIdVisita)
        TextView id;

        @InjectView(R.id.txtCliente)
        TextView cliente;

        @InjectView(R.id.txtDireccion)
        TextView direccion;

        @InjectView(R.id.txtEstado)
        TextView estado;

        Context mContext;

        public ListAdapter(Context context, List objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_item, null);
            }

            ButterKnife.inject(this, convertView);

            AsignacionVisita item = (AsignacionVisita) getItem(position);

            id.setText(item.idVisita == -1 ? "" : String.format("Id: %s", item.idVisita));
            cliente.setText(String.format("%s", item.cliente));
            direccion.setText(item.idVisita == -1 ? "Visita Inicial" : String.format("%s, \n%s", item.direccion, item.comuna));
            estado.setText(String.format("Estado: %s", EstadoVisita.GetEstadoVisita(item.idVisita == -1 ? 2 : item.idEstadoVisita)));

            int[] colors = new int[]{Color.parseColor("#F0F0F0"), Color.parseColor("#D2E4FC")};
            int colorPos = position % colors.length;
            convertView.setBackgroundColor(colors[colorPos]);
            estado.setTextColor(getResources().getColor(EstadoVisita.GetColorEstadoVisita(item.idEstadoVisita)));

            return convertView;

        }
    }

    private class AsyncTaskCerrarVisitas extends AsyncTask<Integer, String, Boolean> {

        private ProgressDialog oProgressDialog;
        private String sError;

        public AsyncTaskCerrarVisitas() {

            this.oProgressDialog = new ProgressDialog(getActivity());
            this.oProgressDialog.setTitle("Cerrando Visitas del día");
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

                List<AsignacionVisita> aGestionados = oAsignacionVisitaDao.queryForAll(oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.GESTIONADA));
                int gestionadosTotal = aGestionados.size();

                List<AsignacionVisita> aNoGestionados = oAsignacionVisitaDao.queryForAll(oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.NO_GESTIONADA));
                int noGestionadosTotal = aNoGestionados.size();

                List<AsignacionVisita> aNoCompletados = oAsignacionVisitaDao.queryForAll(oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.NO_COMPLETADA));
                int noCompletadosTotal = aNoCompletados.size();

                double diferenciaKm = 0.0;
                double kilometros = 0.0;

                int idUsuario = oUsuarioDao.queryForAll().get(0).idUsuario;

                for (AsignacionVisita asignacionVisita : aNoGestionados) {
                    Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID, asignacionVisita.idAsignacionVisita);
                    oAsignacionVisitaDao.update(where, AsignacionVisita.ID_ESTADO_VISITA, EstadoVisita.NO_COMPLETADA);
                    oAsignacionVisitaDao.update(where, AsignacionVisita.TEMA_TRATADO, "No gestionado");
                }

                publishProgress("Cambiando estados");

                List<AsignacionVisita> aAsignacionVisita = oAsignacionVisitaDao.queryForAllOrderBy(AsignacionVisita.ORDEN_VISITA, true);

                int iCount = aAsignacionVisita.size();

                for (int i = 0; i < iCount; i++) {

                    AsignacionVisita oAsignacionVisita = aAsignacionVisita.get(i);

                    Where where = oAsignacionVisitaDao.getStatement().eq(AsignacionVisita.ID, oAsignacionVisita.idAsignacionVisita);
                    oAsignacionVisitaDao.update(where, AsignacionVisita.ID_VENDEDOR, idUsuario);

                    AsignacionVisita oAsignacionVisitaVerify = (AsignacionVisita) oAsignacionVisitaDao.queryForFirst(where);
                    if (oAsignacionVisitaVerify.idVisita != -1) {


                        if (!oAsignacionVisitaVerify.sincronizaTemporalEjecutado) {
                            oAsignacionVisitaBss.sincronizarTemporal(getResources().getString(R.string.conexion), oAsignacionVisita);
                            oAsignacionVisitaDao.update(where, AsignacionVisita.SINCRONIZA_TEMPORAL_EJECUTADO, true);
                        }
                        if (!oAsignacionVisitaVerify.sincronizaVisitaEjecutado) {
                            oAsignacionVisitaBss.sincronizarVisitas(getResources().getString(R.string.conexion), oAsignacionVisita);
                            oAsignacionVisitaDao.update(where, AsignacionVisita.SINCRONIZA_VISITA_EJECUTADO, true);
                        }
                    }
                    if (i == (iCount - 1)) {
                        continue;
                    }
                    AsignacionVisita oAsignacionVisitaSig = aAsignacionVisita.get(i + 1);

                    Location locationA = new Location("");
                    if (oAsignacionVisita.latitudVisita.equals("") || oAsignacionVisita.longitudVisita.equals("")) {
                        continue;
                    }
                    locationA.setLatitude(Double.parseDouble(oAsignacionVisita.latitudVisita));
                    locationA.setLongitude(Double.parseDouble(oAsignacionVisita.longitudVisita));

                    Location locationB = new Location("");
                    if (oAsignacionVisitaSig.latitudVisita.equals("") || oAsignacionVisitaSig.longitudVisita.equals("")) {
                        continue;
                    }
                    locationB.setLatitude(Double.parseDouble(oAsignacionVisitaSig.latitudVisita));
                    locationB.setLongitude(Double.parseDouble(oAsignacionVisitaSig.longitudVisita));

                    diferenciaKm = diferenciaKm + locationA.distanceTo(locationB);
                }

                kilometros = (diferenciaKm / 1000);

                DecimalFormat df = new DecimalFormat("0.00");
                df.setMaximumFractionDigits(2);
                kilometros = Double.parseDouble(df.format(kilometros).replace(",", "."));

                publishProgress("Enviando reporte diario");

                oAsignacionVisitaBss.insertaReporteDiario(getResources().getString(R.string.conexion), idUsuario, gestionadosTotal, noGestionadosTotal, noCompletadosTotal,
                        kilometros);

                publishProgress("Eliminando base de datos local");

                oAsignacionVisitaDao.deleteAll();
                oComunaDao.deleteAll();
                oCultivoDao.deleteAll();
                oUsuarioDao.deleteAll();

                return false;

            } catch (Exception e) {
                this.sError = "Error al intentar cerrar las visitas, reintente.";
                return true;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.oProgressDialog.setTitle(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean bFallo) {
            try {

                if (bFallo) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(this.sError);
                    builder.setIcon(R.drawable.icon_error);
                    builder.setTitle("Error!");
                    builder.setPositiveButton("Aceptar", null);
                    builder.setCancelable(false);
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {


                    oSharedPreference.setString(SharedPreferencesManager.KEY_DATE_VISITA_PROCESSED, new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    final Fragment fragment = getFragmentManager().findFragmentByTag("asignacion");

                    if (fragment != null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(this.sError);
                        builder.setIcon(R.drawable.icon_ok);
                        builder.setTitle("Éxito!");
                        builder.setMessage("Visitas del día cerradas exitosamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                getFragmentManager().beginTransaction()
                                        .remove(fragment).setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top).commit();
                                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            } finally {
                this.oProgressDialog.dismiss();
            }
            super.onPostExecute(bFallo);
        }
    }
}
