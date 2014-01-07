package org.greengin.sciencetoolkit.ui.remote;

import java.io.File;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UploadRemoteAction implements RemoteAction {

	public UploadRemoteAction(Model profile, File series) {
		
	}
	
	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		return new HttpRequestBase[] { new HttpGet(urlBase + "subscriptions?action=myprofiles") };
	}

	@Override
	public void result(int request, String result) {
		Log.d("stk remote test", String.valueOf(request));
		Log.d("stk remote test", result);
		Log.d("stk remote test", "");

		try {
			JSONObject jobj = new JSONObject(result);
			ProfileManager.get().updateRemoteProfiles(jobj);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
	}

}