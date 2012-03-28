package edu.mit.media.funf.probe.builtin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;

import com.google.gson.JsonObject;

import edu.mit.media.funf.DecimalTimeUnit;

public abstract class DatedContentProviderProbe extends ContentProviderProbe {

	@Configurable
	protected BigDecimal afterDate = null;
	
	@Override
	protected Cursor getCursor(String[] projection) {
		String dateColumn = getDateColumnName();
		// Used the code below when we specified projection exactly
		List<String> projectionList = Arrays.asList(projection);
		if (!Arrays.asList(projection).contains(dateColumn)) {
			projectionList = new ArrayList<String>(projectionList);
			projectionList.add(dateColumn);
			projection = new String[projectionList.size()];
			projectionList.toArray(projection);
		}
		String dateFilter = null;
		String[] dateFilterParams = null;
		if (afterDate != null) {
			dateFilter = dateColumn + " > ?";
			dateFilterParams = new String[] {String.valueOf(getDateColumnTimeUnit().convert(afterDate, DecimalTimeUnit.SECONDS))};
		}
		return getContext().getContentResolver().query(
				getContentProviderUri(),
				projection, // TODO: different platforms have different fields supported for content providers, need to resolve this
				dateFilter, 
				dateFilterParams,
                dateColumn + " DESC");
	}
	
	protected abstract Uri getContentProviderUri();
	
	protected abstract String getDateColumnName();

	protected DecimalTimeUnit getDateColumnTimeUnit() {
		return DecimalTimeUnit.MILLISECONDS;
	}
	
	@Override
	protected BigDecimal getTimestamp(JsonObject data) {
		return getDateColumnTimeUnit().toSeconds(data.get(getDateColumnName()).getAsLong());
	}
	
}
