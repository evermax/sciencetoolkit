package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;

import java.io.File;

import org.greengin.sciencetoolkit.model.Model;

public interface SeriesActionListener {
	void seriesDeleted(Model profile, File series);
}
