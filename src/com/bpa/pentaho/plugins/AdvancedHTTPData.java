package com.bpa.pentaho.plugins;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class AdvancedHTTPData extends BaseStepData implements StepDataInterface
{
	public int argnrs[];
	public RowMetaInterface outputRowMeta;
	public int indexOfUrlField;
	public String realUrl;

	/**
	 * Default constructor. 
	 */
	public AdvancedHTTPData()
	{
		super();
		indexOfUrlField=-1;
	}
}