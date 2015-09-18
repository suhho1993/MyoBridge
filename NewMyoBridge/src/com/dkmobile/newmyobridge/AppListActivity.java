package com.dkmobile.newmyobridge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class AppListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_list);

		loadApps();
		loadListView();
		addClickListener();
	}

	public class AppDetail {
		CharSequence label;
		CharSequence name;
		Drawable icon;
	}

	private PackageManager manager;
	private List<AppDetail> apps;

	private void loadApps() {
		manager = getPackageManager();
		apps = new ArrayList<AppDetail>();

		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory(Intent.CATEGORY_LAUNCHER);

		// delete
		AppDetail delet = new AppDetail();
		delet.icon = null;
		delet.label = null;
		delet.name = null;
		apps.add(delet);

		List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
		for (ResolveInfo ri : availableActivities) {
			AppDetail app = new AppDetail();
			app.label = ri.loadLabel(manager);
			app.name = ri.activityInfo.packageName;
			app.icon = ri.activityInfo.loadIcon(manager);
			apps.add(app);
		}

	}

	private ListView list;

	private void loadListView() {
		list = (ListView) findViewById(R.id.apps_list);

		ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = getLayoutInflater().inflate(R.layout.list_item, null);
				}

				ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
				TextView appLabel = (TextView) convertView.findViewById(R.id.item_app_label);
				TextView appName = (TextView) convertView.findViewById(R.id.item_app_name);
				if (apps.get(position).name == null) {
					appName.setText("Delet App");
				} else {
					appIcon.setImageDrawable(apps.get(position).icon);
					appLabel.setText(apps.get(position).label);
					appName.setText(apps.get(position).name);
				}
				return convertView;

			}
		};

		list.setAdapter(adapter);
	}

	private void addClickListener() {
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				if (apps.get(pos).name == null) {

					Intent i = new Intent();
					setResult(RESULT_OK, i);
				}
				else {
					Intent i = new Intent();
					i.putExtra("App_Activity", apps.get(pos).name.toString());
					i.putExtra("App_name", apps.get(pos).label.toString());
					setResult(RESULT_OK, i);
				}
				finish();
				// AppListActivity.this.startActivity(i);
			}
		});
	}

}
