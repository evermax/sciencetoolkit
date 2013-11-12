package org.greengin.sciencetoolkit.ui.components.main.datalogging.switchprofile;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListActivity;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.CreateProfileDialogFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.Fragment;

public class SwitchProfileActivity extends ParentListActivity implements ModelNotificationListener, DataLoggerStatusListener {

	public SwitchProfileActivity() {
		super(R.id.profile_list);
	}

	String selectedForChange;
	Button ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_profile);
		ok = (Button) getWindow().getDecorView().findViewById(R.id.ok);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.switch_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_data_logging_new:
			CreateProfileDialogFragment.showCreateProfileDialog(getSupportFragmentManager(), false);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();
		DataLogger.getInstance().registerStatusListener(this);
		ProfileManager.getInstance().registerDirectListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		DataLogger.getInstance().unregisterStatusListener(this);
		ProfileManager.getInstance().unregisterDirectListener(this);
	}

	@Override
	public void dataLoggerStatusModified() {
		updateSwitchButton();
	}
	
	public void onClickOkButton(View view) {
		if (this.selectedForChange != null) {
			ProfileManager.getInstance().switchActiveProfile(this.selectedForChange);
		}
	}
	
	private void updateView() {
		selectedForChange = null;
		updateSwitchButton();
		updateChildrenList();
	}


	private void updateSwitchButton() {
		boolean enabled = !DataLogger.getInstance().isRunning() && selectedForChange != null && !selectedForChange.equals(ProfileManager.getInstance().getActiveProfileId());
		ok.setEnabled(enabled);
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if ("list".equals(msg)) {
			updateView();
		}
	}

	public void requestSelectedForChange(String profileId) {
		if (!profileId.equals(this.selectedForChange)) {
			this.selectedForChange = profileId;
			
			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			if (fragments != null) {
				for (Fragment fragment : fragments) {
					if (fragment instanceof SwitchProfileFragment) {
						((SwitchProfileFragment)fragment).setSelectedForChangeProfile(this.selectedForChange);
					}
				}
			}
			updateSwitchButton();
		}
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();
		Set<String> profileIds = ProfileManager.getInstance().getProfileIds();
		for (String profileId : profileIds) {
			SwitchProfileFragment fragment = new SwitchProfileFragment();
			Bundle args = new Bundle();
			args.putString(Arguments.ARG_PROFILE, profileId);
			fragment.setArguments(args);
			fragments.add(fragment);
		}
		
		return fragments;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof SwitchProfileFragment;
	}

}
