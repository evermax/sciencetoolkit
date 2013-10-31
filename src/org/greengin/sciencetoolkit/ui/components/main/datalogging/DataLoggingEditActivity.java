package org.greengin.sciencetoolkit.ui.components.main.datalogging;

import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.NotificationListener;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;

public class DataLoggingEditActivity extends FragmentActivity implements NotificationListener {

	Model profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		profile = ProfileManager.getInstance().getActiveProfile();

		setContentView(R.layout.activity_data_logging_edit);

		if (profile != null) {
			EditText edit = (EditText) getWindow().getDecorView().findViewById(R.id.current_profile_name);
			edit.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					profile.setString("title", s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

			});
		}

		setupActionBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTitle();
		updateList();
		ProfileManager.getInstance().registerDirectListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(this);
	}

	private void updateTitle() {
		View rootView = getWindow().getDecorView();
		EditText edit = (EditText) rootView.findViewById(R.id.current_profile_name);
		edit.setText(profile.getString("title"));
	}

	private void updateList() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments != null) {
			Log.d("stk updatelist", "fr " + fragments.size());
			for (Fragment fragment : fragments) {
				fragmentTransaction.remove(fragment);
			}
		}
		fragmentTransaction.commit();

		Vector<Model> profileSensors = profile.getModel("sensors", true).getModels("weight");
		Log.d("stk updatelist", "ps " + profileSensors .size());
		for (Model profileSensor : profileSensors) {
			ProfileSensorOrganizeFragment fragment = new ProfileSensorOrganizeFragment();
			Bundle args = new Bundle();
			args.putString("profile", profile.getString("id"));
			args.putString("sensor", profileSensor.getString("id"));
			fragment.setArguments(args);
			fragmentManager.beginTransaction().add(R.id.sensor_list, fragment).commit();
		}
	}

	public void actionAddSensor(View view) {
		FragmentManager fm = getSupportFragmentManager();
		AddSensorDialogFragment dialog = new AddSensorDialogFragment();
		dialog.show(fm, "add_sensor");
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_logging_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void notificationReveiced(String msg) {
		updateList();
	}

}