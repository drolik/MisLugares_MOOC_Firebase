package com.example.mislugares;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VistaLugarFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private long id;
    private Lugar lugar;
    //private ImageView imageView;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;
    private View v;

    final int SOLICITUD_SUBIR_PUTSTREAM = 101;
    Boolean subiendoDatos =false;
    ImageView foto;
    //    final int SOLICITUD_SUBIR_PUTDATA = 102;
    //    final int SOLICITUD_SUBIR_PUTFILE = 103;


    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.vista_lugar,contenedor,false);
        setHasOptionsMenu(true);
        LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.barra_url);
        pUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pgWeb(null);
            }
        });
        LinearLayout pTlf = (LinearLayout) vista.findViewById(R.id.barra_telefono);
        pTlf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                llamadaTelefono(null);
            }
        });
        LinearLayout pMapa = (LinearLayout) vista.findViewById(R.id.barra_direccion);
        pMapa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verMapa(null);
            }
        });
        ImageView iconoFoto = (ImageView) vista.findViewById(R.id.camara);
        iconoFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               tomarFoto();
            }
        });
        ImageView iconoGaleria = (ImageView) vista.findViewById(R.id.galeria);
        iconoGaleria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                galeria();
            }
        });
        ImageView iconoBorra = (ImageView) vista.findViewById(R.id.eliminarFoto);
        iconoBorra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { eliminarFoto();
            }
        });
        ImageView iconoHora = (ImageView) vista.findViewById(R.id.icono_hora);
        iconoHora.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarHora();
            }
        });
        ImageView iconoFecha = (ImageView) vista.findViewById(R.id.icono_fecha);
        iconoFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFecha();
            }
        });

        return vista;
    }

    public void subirAFirebaseStorage(Integer opcion, Bitmap bitmap) {
        final ProgressDialog progresoSubida = ProgressDialog.show(getActivity(), "Espere ...", "Subiendo ...", true);
        UploadTask uploadTask = null;
        final String fichero = SelectorFragment.adaptador.getRef((int) id).getKey();;
        StorageReference imagenRef = Aplicacion.getStorageReference().child(fichero);
        foto = (ImageView)v.findViewById(R.id.foto);
        try {
            switch (opcion) {
                case SOLICITUD_SUBIR_PUTSTREAM:
                    File filesDir = getActivity().getApplicationContext().getFilesDir();
                    File imageFile = new File(filesDir, "test" + ".jpg");

                    OutputStream os;
                    try {
                        os = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                    }


                    InputStream stream = new FileInputStream(imageFile);
                    uploadTask = imagenRef.putStream(stream);
                    break;

            /*    case SOLICITUD_SUBIR_PUTDATA:
                    imgImagen.setDrawingCacheEnabled(true);
                    imgImagen.buildDrawingCache();
                    Bitmap bitmap = imgImagen.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    imagenRef = Aplicacion.getStorageReference().child(fichero);
                    uploadTask = imagenRef.putBytes(data);
                    Log.d("MIERROR", "AAAA");
                    break;*/
              /*  case SOLICITUD_SUBIR_PUTFILE:
                    Uri file = Uri.fromFile(new File(ficheroDispositivo));
                    uploadTask = imagenRef.putFile(file);
                    break;*/
            }
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Aplicacion.mostrarDialogo(getActivity().getApplicationContext(), "Ha ocurrido un error al subir la imagen.");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DatabaseReference myRef = Aplicacion.getReferenciaLugares().child(fichero);
                    DatabaseReference imagenRef = myRef.child("foto");
                    imagenRef.setValue(taskSnapshot.getDownloadUrl().toString());
                    new DownloadImageTask((ImageView) foto).execute(taskSnapshot.getDownloadUrl().toString());
                    progresoSubida.dismiss();
                    subiendoDatos = false;
                    Aplicacion.mostrarDialogo(getActivity().getApplicationContext(), "Imagen subida correctamente.");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    if (!subiendoDatos) {
                        progresoSubida.show();
                        subiendoDatos = true;
                    }
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    progresoSubida.dismiss();
                    subiendoDatos = true;
                }
            });
        } catch (IOException e) {
            Aplicacion.mostrarDialogo(getActivity().getApplicationContext(), e.toString());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mImagen = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mImagen = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImagen;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id", -1);
            if (id != -1) {
                actualizarVistas(id);
            }
        }
    }

    public void actualizarVistas(final long id) {
        this.id = id;
        lugar = SelectorFragment.adaptador.getItem((int) id);
        if (lugar != null) {

        TextView nombre = (TextView) v.findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipoEnum().getRecurso());
        TextView tipo = (TextView) v.findViewById(R.id.tipo);
        tipo.setText(lugar.getTipoEnum().getTexto());

        if (lugar.getDireccion().isEmpty()) {
            v.findViewById(R.id.barra_direccion).setVisibility(View.GONE);
        } else {
            TextView direccion = (TextView) v.findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
        }
        if (lugar.getTelefono() == 0) {
            v.findViewById(R.id.barra_telefono).setVisibility(View.GONE);
        } else {
            TextView telefono = (TextView) v.findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        if (lugar.getUrl().isEmpty()) {
            v.findViewById(R.id.barra_url).setVisibility(View.GONE);
        } else {
            TextView url = (TextView) v.findViewById(R.id.url);
            url.setText(lugar.getUrl());
        }
        if (lugar.getComentario().isEmpty()) {
            v.findViewById(R.id.barra_comentario).setVisibility(View.GONE);
        } else {
            TextView comentario = (TextView) v.findViewById(R.id.comentario);
            comentario.setText(lugar.getComentario());
        }
        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = (TextView) v.findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = (RatingBar) v.findViewById(R.id.valoracion);
        valoracion.setOnRatingBarChangeListener(null);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                        actualizaLugar();
                    }
                });
        ponerFoto((ImageView)v.findViewById(R.id.foto), lugar.getFoto());
    }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vista_lugar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                lanzarEdicionLugar(id);
                return true;
            case R.id.accion_borrar:
                borrarLugar((int) id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void borrarLugar(final int id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String _id = SelectorFragment.adaptador.getRef((int) id).getKey();
                        MainActivity.lugares.borrar(_id);


                        SelectorFragment.adaptador.notifyDataSetChanged();
                        SelectorFragment selectorFragment = (SelectorFragment) getActivity().
                                getSupportFragmentManager().findFragmentById(R.id.selector_fragment);
                        if (selectorFragment == null) {
                            getActivity().finish();
                        } else {
                            ((MainActivity) getActivity()).muestraLugar(0);
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public void lanzarEdicionLugar(final long id) {
        Intent i = new Intent(getActivity(), EdicionLugarActivity.class);
        i.putExtra("id", id);
        startActivityForResult(i, RESULTADO_EDITAR);
    }

    public void galeria() {
        Intent seleccionFotografiaIntent = new Intent(Intent.ACTION_PICK);
        seleccionFotografiaIntent.setType("image/*");
        seleccionFotografiaIntent.putExtra("id", id);
        startActivityForResult(seleccionFotografiaIntent, RESULTADO_GALERIA);

    }


    private static final int SOLICITUD_PERMISO_LECTURA = 0;

    ImageView lastImageView;
    String lastUri;

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            if (uri.startsWith("content://com.example.mislugares/") ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                        READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

               imageView.setImageBitmap(reduceBitmap(getActivity(), uri, 1024,   1024));
            } else  {
                    lastImageView=imageView; lastUri=uri;
                    PermisosUtilidades.solicitarPermisoFragment(Manifest.permission.
                          READ_EXTERNAL_STORAGE, "Sin permiso de lectura no es posible "+
                          "mostrar fotos de memoria externa", SOLICITUD_PERMISO_LECTURA, this);
            }
        } else {
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LECTURA) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ponerFoto(lastImageView, lastUri);
            } else {
                ponerFoto(lastImageView, null);
            }
        }
    }

    public static Bitmap reduceBitmap(Context contexto, String uri,
                                      int maxAncho, int maxAlto) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = null;
            try {
                 input = new URL(uri).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return  BitmapFactory.decodeStream(input, null, options);
    }


    public void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("id", id);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto() {
        final String fichero = SelectorFragment.adaptador.getRef((int) id).getKey();;
        StorageReference imagenRef = Aplicacion.getStorageReference().child(fichero);


        imagenRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference myRef = Aplicacion.getReferenciaLugares().child(fichero);
                        DatabaseReference imagenRef = myRef.child("foto");
                        imagenRef.setValue(null);
                        ponerFoto((ImageView)v.findViewById(R.id.foto), "");
                        Toast.makeText(getActivity(), "Imagen borrada correctamente", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                      public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Error al borrar la imagen", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id", -1);
            if (id != -1) {
                actualizarVistas(id);
            }
        }
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(id);
            //View s = v.findViewById(R.id.scrollView1);
            //if (s!=null) {
             //   s.invalidate();
            //}
            //v.findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA || requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK
                   && lugar != null
            ) {
                Bitmap bitmap = null;
                if(data.getData()==null){
                    bitmap = (Bitmap)data.getExtras().get("data");
                }else{
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bitmap == null) {
                    Toast.makeText(getActivity(), "Error capturando foto", Toast.LENGTH_LONG).show();
                }
                subirAFirebaseStorage(SOLICITUD_SUBIR_PUTSTREAM, bitmap);
            } else {
                Toast.makeText(getActivity(), "Error capturando foto", Toast.LENGTH_LONG).show();
            }

        }
    }

    void actualizaLugar(){
        String _id = SelectorFragment.adaptador.getRef((int) id).getKey();
        MainActivity.lugares.actualiza(_id, lugar);
    }

    public void cambiarHora() {
        DialogoSelectorHora dialogo = new DialogoSelectorHora();
        dialogo.setOnTimeSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogo.setArguments(args);
        dialogo.show(getActivity().getSupportFragmentManager(), "selectorHora");
    }

    @Override
    public void onTimeSet(TimePicker vista, int hora, int minuto) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.HOUR_OF_DAY, hora);
        calendario.set(Calendar.MINUTE, minuto);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tHora = (TextView) getView().findViewById(R.id.hora);
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm",
                java.util.Locale.getDefault());
        tHora.setText(formato.format(new Date(lugar.getFecha())));
    }

    public void cambiarFecha() {
        DialogoSelectorFecha dialogo = new DialogoSelectorFecha();
        dialogo.setOnDateSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", lugar.getFecha());
        dialogo.setArguments(args);
        dialogo.show(getActivity().getSupportFragmentManager(), "selectorFecha");
    }

    @Override
    public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(lugar.getFecha());
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        lugar.setFecha(calendario.getTimeInMillis());
        actualizaLugar();
        TextView tFecha = (TextView) getView().findViewById(R.id.fecha);
        DateFormat formato =  DateFormat.getDateInstance();
        tFecha.setText(formato.format(new Date(lugar.getFecha())));
    }
}