package org.greengin.sciencetoolkit.ui.main;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.ui.main.explore.ExploreFragment;
import org.greengin.sciencetoolkit.ui.main.record.RecordFragment;
import org.greengin.sciencetoolkit.ui.main.share.ShareFragment;

import android.support.v4.app.Fragment;

public class MainActivity extends SwipeActivity {
	private static int lastTab = -1;
	
	
	
	@Override
	public int getOnResumeTab() {
		return lastTab;
	}

	@Override
	public void setOnResumeTab(int position) {
		lastTab = position;		
	}

	@Override
	public int getContentViewLayoutId() {
		return R.layout.view_main;
	}

	@Override
	public int getViewPagerLayoutId() {
		return R.id.pager;
	}

	@Override
	public int getTabCount() {
		return 3;
	}

	@Override
	public Fragment createTabFragment(int position) {
		switch (position) {
		case 0:
			return new ExploreFragment();
		case 1:
			return new RecordFragment();
		case 2:
			return new ShareFragment();
		default:
			return null;
		}
	}

	@Override
	public CharSequence getTabTitle(int position) {
		switch (position) {
		case 0:
			return getString(R.string.main_activity_tab_1);
		case 1:
			return getString(R.string.main_activity_tab_2);
		case 2:
			return getString(R.string.main_activity_tab_3);
		default:
			return null;
		}
	}


}
