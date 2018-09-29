package com.clinic.myclinic.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.clinic.myclinic.Classes.Doctor;
import com.clinic.myclinic.R;
import com.clinic.myclinic.Utils.CircularTransformation;
import com.clinic.myclinic.Utils.PersistantStorageUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorsAdapter extends ArrayAdapter<Doctor>
                            implements Filterable{

    private LayoutInflater inflater;             // для загрузки разметки элемента
    private int layout;                          // идентфикатор файла разметки
    private ArrayList<Doctor> doctors;           // коллекция выводимых данных
    ArrayList<Doctor> mDocsFiltered;
    private ItemFilter mFilter = new ItemFilter(); // Объект фильтра

    public static String language;
    public static String textSize;

    TextView txtDocName, txtPos, txtPhone, txtCabinet;
    ImageView imgDocCard;
    RatingBar rbRating;
    Context ctx;

    public DoctorsAdapter(Context context, int res, ArrayList<Doctor> doctors){
        super(context, res, doctors);

        this.inflater = LayoutInflater.from(context);
        this.layout = res;
        this.doctors = doctors;
        mDocsFiltered = doctors;
        ctx = context;
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Doctor getItem(int i) {
        return doctors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = inflater.inflate(this.layout, parent, false);

        //Ссылки элементов
        txtDocName = view.findViewById(R.id.txtDoctorFullName);
        txtPos = view.findViewById(R.id.txtPos);
        rbRating = view.findViewById(R.id.rbRatingCard);
        txtPhone = view.findViewById(R.id.txtPhoneCard);
        txtCabinet = view.findViewById(R.id.txtCabinet);
        imgDocCard = view.findViewById(R.id.imgDocCard);

        language = PersistantStorageUtils.getLanguagePreferences(ctx);

        //ссылка на объект записей
        final Doctor item = doctors.get(position);
        

        txtDocName.setText(item.getName());
        txtPos.setText(item.getPosition());
        txtPhone.setText(item.getPhone());
        txtCabinet.setText(item.getParlor());
        rbRating.setRating((float)item.getRating());

        Picasso.get()
                .load(item.getPhotoURL())
                .resize(150, 150)
                .centerCrop()
                .transform(new CircularTransformation())
                .into(imgDocCard);

        //Получаем актуальный размер шрифта
        textSize = PersistantStorageUtils.getTextSizePreferences(ctx);
        setTextSize();

        //ссылка на готовый элемент
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    private void setTextSize() {
        txtDocName.setTextSize(Integer.parseInt(textSize));
        txtPos.setTextSize(Integer.parseInt(textSize));
        txtPhone.setTextSize(Integer.parseInt(textSize));
        txtCabinet.setTextSize(Integer.parseInt(textSize));
    }

    //Кастомный фильтр для поиска
    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Doctor> ndocs = new ArrayList<Doctor>();


                for (int i = 0; i < mDocsFiltered.size(); i++) {
                    if (mDocsFiltered.get(i).getName().toLowerCase().contains(filterString)) {
                        ndocs.add(mDocsFiltered.get(i));
                    }//if
                }//for

                results.values = ndocs;
                results.count = ndocs.size();


            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            doctors = (ArrayList<Doctor>) results.values;
            notifyDataSetChanged();
        }
    }

}
