package org.greengin.sciencetoolkit.spotit.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.AbstractModelManager;
import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.model.events.NotificationListenerAggregator;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ProjectManager extends AbstractModelManager implements
		ModelNotificationListener {

	private static ProjectManager instance;

	public static void init(Context applicationContext) {
		instance = new ProjectManager(applicationContext);
	}

	public static ProjectManager get() {
		return instance;
	}

	NotificationListenerAggregator listeners;

	Model settings;
	Model appSettings;

	Comparator<String> projectIdComparator;

	private ProjectManager(Context applicationContext) {
		super(applicationContext, "projects.xml", 500);
		settings = SettingsManager.get().get("projects");
		appSettings = SettingsManager.get().get("app");
		listeners = new NotificationListenerAggregator(applicationContext,
				"projects:notifications");
		SettingsManager.get().registerDirectListener("projects", this);
		checkDataConsistency();

		projectIdComparator = new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				try {
					return Integer.parseInt(rhs) - Integer.parseInt(lhs);
				} catch (Exception e) {
					return 0;
				}
			}
		};
	}

	public boolean projectIdIsActive(String id) {
		return id != null && id.equals(getActiveProjectId());
	}

	public boolean projectIsActive(Model profile) {
		return profile != null && projectIdIsActive(profile.getString("id"));
	}

	private void checkDataConsistency() {
		for (Entry<String, Model> entry : items.entrySet()) {
			String id = entry.getKey();
			Model model = entry.getValue();

			if (id.length() == 0) {
				Log.d("stk profiles", "empty id");
			} else {
				if (!id.equals(model.getString("id"))) {
					Log.d("stk profiles", "conflicting ids: " + id + " "
							+ model.getString("id"));
				}
			}
		}
	}

	public void deleteProject(String projectId) {
		super.remove(projectId);
		listeners.fireEvent("list");
		DataManager.get().deleteData(projectId);
	}

	public void switchActiveProject(String projectId) {
		if (projectId != null
				&& !projectId.equals(settings.getString("current_project"))) {
			settings.setString("current_project", projectId);
		}
	}

	public Model get(String key) {
		return get(key, false);
	}

	public Model getActiveProject() {
		return get(getActiveProjectId());
	}

	public String getActiveProjectId() {
		return settings.getString("current_project");
	}

	public Vector<String> getProjectIds() {
		Vector<String> profiles = new Vector<String>();
		for (String id : items.keySet()) {
			profiles.add(id);
		}

		Collections.sort(profiles, projectIdComparator);
		return profiles;
	}

	public int getProjectCount() {
		return this.items.size();
	}

	@Override
	public void modelModified(Model model) {
		super.modelModified(model);

		if (model != null) {
			Model profile = model.getRootParent();
			String profileId = profile.getString("id", null);
			listeners.fireEvent(profileId);
		}
	}

	@Override
	public void modelNotificationReceived(String msg) {
		listeners.fireEvent("switch");
	}

	public void registerUIListener(ModelNotificationListener listener) {
		listeners.addUIListener(listener);
	}

	public void unregisterUIListener(ModelNotificationListener listener) {
		listeners.removeUIListener(listener);
	}

	public void registerDirectListener(ModelNotificationListener listener) {
		listeners.addDirectListener(listener);
	}

	public void unregisterDirectListener(ModelNotificationListener listener) {
		listeners.removeDirectListener(listener);
	}

	public void updateRemoteProjects(JSONObject remoteData) {
		try {
			Iterator<?> projectIt = remoteData.keys();
			while (projectIt.hasNext()) {

				String jsonProjectId = (String) projectIt.next();
				JSONObject jsonProjectObj = remoteData
						.getJSONObject(jsonProjectId);

				String jsonProjectTitle = jsonProjectObj.getString("title");
				String jsonProjectDescription = jsonProjectObj
						.getString("description");

				Model project = get(jsonProjectId);
				if (project == null) {
					project = new Model(this);
					items.put(jsonProjectId, project);
					project.setString("id", jsonProjectId, true);
				}

				project.setString("title", jsonProjectTitle, true);
				project.setString("description", jsonProjectDescription, true);
				project.setBool("requires_location",
						jsonProjectObj.getBoolean("geolocated"), true);

			}

			this.forceSave();
			listeners.fireEvent("list");

		} catch (JSONException e) {
			Log.d("stk remote update", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int getCurrentVersion() {
		return 0;
	}

	@Override
	public void updateRootModel(String key, Model model, int version) {
	}
}
