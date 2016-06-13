package es.esy.gopiappdeveloper.snf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.esy.gopiappdeveloper.snf.R;


public class LVAdapter extends BaseAdapter implements Filterable {

    private ItemFilter mFilter = new ItemFilter();
    private Context jContext;
    private JSONArray originalData;
    private JSONArray filteredData;

    private LayoutInflater mInflater;

    public LVAdapter(Context jContext, JSONArray jResult) {
        jContext = jContext ;
        this.originalData = jResult ;
        this.filteredData = jResult ;
        mInflater = LayoutInflater.from(jContext);
    }

    @Override
    public int getCount() {
        return filteredData.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lv_item, null);
            holder = new ViewHolder();
            holder.cmp_name = (TextView) convertView.findViewById(R.id.cmp_name);
            holder.cmp_id = (TextView) convertView.findViewById(R.id.cmp_id);
            holder.cmp_owner = (TextView) convertView.findViewById(R.id.cmp_owner);
            holder.cmp_cat = (TextView) convertView.findViewById(R.id.cmp_cat);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jNode = null;
        try {
            jNode = filteredData.getJSONObject(position);
            holder.cmp_name.setText(jNode.getString("companyName"));
            holder.cmp_id.setText(jNode.getString("companyID"));
            holder.cmp_owner.setText(jNode.getString("companyOwner"));
            holder.cmp_cat.setText(jNode.getString("companyDepartments"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView cmp_name;
        TextView cmp_id;
        TextView cmp_owner;
        TextView cmp_cat;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String key = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final JSONArray list = originalData;
            int count = list.length();
            final JSONArray nlist = new JSONArray();
            String match;
            for (int i = 0; i < count; i++) {
                JSONObject sub = null;
                try {
                    sub = list.getJSONObject(i);
                    match = sub.getString("companyDepartments").toLowerCase();
                    if(match.contains(key)){
                        nlist.put(sub);
                    }else if(key.contains("all")){
                        nlist.put(sub);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            results.values = nlist;
            results.count = nlist.length();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (JSONArray) results.values;
            notifyDataSetChanged();
        }
    }
}
