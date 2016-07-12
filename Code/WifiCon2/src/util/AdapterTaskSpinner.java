package util;

import java.util.List;

import seo.extra.wifi_analyzor.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterTaskSpinner extends ArrayAdapter<String> {
	Context context;
	List<String> spinnerValues;
	Integer[] imageNumbers = new Integer[3];
	
	public AdapterTaskSpinner(Context ctx, int txtViewResourceId,
			List<String> objects) {
		super( ctx, txtViewResourceId, objects );
		this.context = ctx;
		this.spinnerValues = objects;
		this.imageNumbers[0] = android.R.drawable.checkbox_off_background;
		this.imageNumbers[1] = android.R.drawable.ic_delete;
		this.imageNumbers[2] = android.R.drawable.checkbox_on_background;
	}
	
	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
		return getCustomView( position, cnvtView, prnt );
	}
	
	@Override
	public View getView(int pos, View cnvtView, ViewGroup prnt) {
		return getCustomView( pos, cnvtView, prnt );
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View mySpinner = inflater.inflate( R.layout.spinner_layout, parent,
				false );
		TextView main_text = (TextView) mySpinner
				.findViewById( R.id.spinnerText );
		main_text.setText( spinnerValues.get( position ) );
		
		ImageView left_icon = (ImageView) mySpinner
				.findViewById( R.id.spinnerImage );
		left_icon.setImageResource( imageNumbers[position] );
		return mySpinner;
	}
}
