package com.bpa.pentaho.plugins;

import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class AdvancedHTTP extends BaseStep implements StepInterface
{
	private AdvancedHTTPMeta meta;
	private AdvancedHTTPData data;
	
	public AdvancedHTTP(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans)
	{
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}
	
	private Object[] execHttp(RowMetaInterface rowMeta, Object[] row) throws KettleException
	{
        if (first)
		{
			first=false;
			data.argnrs=new int[meta.getArgumentField().length];
			
			for (int i=0;i<meta.getArgumentField().length;i++)
			{
				data.argnrs[i]=rowMeta.indexOfValue(meta.getArgumentField()[i]);
				if (data.argnrs[i]<0)
				{
					logError(Messages.getString("AdvancedHTTP.Log.ErrorFindingField")+meta.getArgumentField()[i]+"]"); //$NON-NLS-1$ //$NON-NLS-2$
					throw new KettleStepException(Messages.getString("AdvancedHTTP.Exception.CouldnotFindField",meta.getArgumentField()[i])); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

        return callHttpService(rowMeta, row);
	}
	
	private Object[] callHttpService(RowMetaInterface rowMeta, Object[] rowData) throws KettleException
    {
        String url = initUrl(rowMeta, rowData);
        try
        {
            if(log.isDetailed()) logDetailed(Messages.getString("AdvancedHTTP.Log.Connecting",url));
            
            // Prepare HTTP get
            // 
            HttpClient httpclient = new HttpClient();

            // Basic auth
            if (meta.isUseBasicAuth()) {
            	AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            	Credentials credentials = new UsernamePasswordCredentials(meta.getBasicAuthLogin(), meta.getBasicAuthPassword());
            	httpclient.getState().setCredentials(authScope, credentials);
            }
            
            // ssl force check
            if (!meta.isStrictSSLCheck()) {
            	SSL.disableSSLCheck();
            }
         
            HttpMethod method = null;
            if (AdvancedHTTPMeta.HTTP_CALL_TYPE_GET.equals(meta.getHttpCallType())) {
            	String suffix = getParams(rowMeta, rowData);
            	String getUrl = url;
            	if (getUrl.indexOf("?")<0)
	            {
            		getUrl += "?" + suffix;
	            }
	            else
	            {
	            	getUrl += "&" + suffix;
	            }
            	if(log.isDetailed()) logDetailed(Messages.getString("AdvancedHTTP.Log.GetUrl",getUrl));
            	method = new GetMethod(getUrl);
            }
            else if (AdvancedHTTPMeta.HTTP_CALL_TYPE_POST_FORM.equals(meta.getHttpCallType())) {
            	PostMethod postMethod = new PostMethod(url);
            	if(log.isDetailed()) logDetailed(Messages.getString("AdvancedHTTP.Log.PostUrl",url));
            	String body = getParams(rowMeta, rowData);
            	if(log.isDetailed()) logDetailed(Messages.getString("AdvancedHTTP.Log.PostBody",body));
            	postMethod.addRequestHeader(new Header("Content-Type", "application/x-www-form-urlencoded"));
            	postMethod.setRequestEntity(new StringRequestEntity(body));
            	method = postMethod;

            }
            else {
            	throw new KettleException("Unknown http call type " + meta.getHttpCallType());
            }

            // Execute request
            // 
            try
            {
                int result = httpclient.executeMethod(method);
                
                // The status code
                if (log.isDebug()) log.logDebug(toString(), Messages.getString("AdvancedHTTP.Log.ResponseStatusCode", ""+result));
                
                // the response
                InputStream inputStream = method.getResponseBodyAsStream();
                StringBuffer bodyBuffer = new StringBuffer();
                int c;
                while ( (c=inputStream.read())!=-1) bodyBuffer.append((char)c);
                inputStream.close();
                
                String body = bodyBuffer.toString();
                if (log.isDebug()) log.logDebug(toString(), "Response body: "+body);
                
                return RowDataUtil.addValueData(RowDataUtil.addValueData(rowData, rowMeta.size(), body), rowMeta.size() + 1, new Long(result));
            }
            finally
            {
                // Release current connection to the connection pool once you are done
                method.releaseConnection();
            }
        }
        catch(Exception e)
        {
        	if (meta.isFailOnError()) {
        		throw new KettleException(Messages.getString("AdvancedHTTP.Log.UnableGetResult",url), e);
        	}
        	else {
        		return RowDataUtil.addValueData(RowDataUtil.addValueData(rowData, rowMeta.size(), e.getMessage()), rowMeta.size() + 1, new Long(-1));
        	}
        }
    }

	private String initUrl(RowMetaInterface outputRowMeta, Object[] row) throws KettleValueException, KettleException {
		try
    	{
    		if(meta.isUrlInField())
  	        {
    			// get dynamic url
  	        	data.realUrl=outputRowMeta.getString(row,data.indexOfUrlField);
  	        }
    		return data.realUrl;
    	}
		catch(Exception e)
	    {
	        throw new KettleException(Messages.getString("AdvancedHTTP.Log.UnableCreateUrl"), e);
	    }
	}
	
    private String getParams(RowMetaInterface outputRowMeta, Object[] row) throws KettleValueException, KettleException
    {
    	try
    	{
            StringBuffer url = new StringBuffer("");
	        
	        for (int i=0;i<data.argnrs.length;i++)
	        {
	        	if (i!=0)
	            {
	                url.append('&');
	            }
	
	        	url.append(URIUtil.encodeWithinQuery(meta.getArgumentParameter()[i]));
	        	url.append('=');
	            String s = null;
	            if (outputRowMeta.getValueMeta(data.argnrs[i]).getType() == ValueMeta.TYPE_STRING) {
	            	s = outputRowMeta.getString(row, data.argnrs[i]);
	            }
	            else if (outputRowMeta.getValueMeta(data.argnrs[i]).getType() == ValueMeta.TYPE_INTEGER) {
	            	s = outputRowMeta.getInteger(row, data.argnrs[i]).toString();
	            }
	            else {
	            	 throw new KettleException(Messages.getString("AdvancedHTTP.Log.NotManagedParameterType", outputRowMeta.getValueMeta(data.argnrs[i]).toString()));
	            }
	            s = URIUtil.encodeWithinQuery(s);
	            url.append(s);
	        }
	        
	        return url.toString();
	    }
	    catch(Exception e)
	    {
	        throw new KettleException(Messages.getString("AdvancedHTTP.Log.UnableCreateUrl"), e);
	    }
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{
		meta=(AdvancedHTTPMeta)smi;
		data=(AdvancedHTTPData)sdi;
		
		 boolean sendToErrorRow=false;
		 String errorMessage = null;

		Object[] r=getRow();     // Get row from input rowset & set row busy!
		if (r==null)  // no more input to be expected...
		{
			setOutputDone();
			return false;
		}
		
		if ( first )
		{
			data.outputRowMeta = getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			if(meta.isUrlInField())
			{
				if(Const.isEmpty(meta.getUrlField()))
				{
					logError(Messages.getString("AdvancedHTTP.Log.NoField"));
					throw new KettleException(Messages.getString("AdvancedHTTP.Log.NoField"));
				}
				
				// cache the position of the field			
				if (data.indexOfUrlField<0)
				{	
					String realUrlfieldName=environmentSubstitute(meta.getUrlField());
					data.indexOfUrlField =getInputRowMeta().indexOfValue(realUrlfieldName);
					if (data.indexOfUrlField<0)
					{
						// The field is unreachable !
						logError(Messages.getString("AdvancedHTTP.Log.ErrorFindingField",realUrlfieldName)); 
						throw new KettleException(Messages.getString("AdvancedHTTP.Exception.ErrorFindingField",realUrlfieldName)); 
					}
				}
			}else
			{
				data.realUrl=environmentSubstitute(meta.getUrl());
			}
		}
		    
		try
		{
			Object[] outputRowData = execHttp(getInputRowMeta(), r); // add new values to the row
			putRow(data.outputRowMeta, outputRowData);  // copy row to output rowset(s);
				
            if (checkFeedback(getLinesRead())) 
            {
            	if(log.isDetailed()) logDetailed(Messages.getString("AdvancedHTTP.LineNumber")+getLinesRead()); //$NON-NLS-1$
            }
		}
		catch(KettleException e)
		{
			if (getStepMeta().isDoingErrorHandling())
	        {
                sendToErrorRow = true;
                errorMessage = e.toString();
	        }
			else
			{
				logError(Messages.getString("AdvancedHTTP.ErrorInStepRunning")+e.getMessage()); //$NON-NLS-1$
				setErrors(1);
				stopAll();
				setOutputDone();  // signal end to receiver(s)
				return false;
			}
			if (sendToErrorRow)
	         {
				 // Simply add this row to the error row
				putError(getInputRowMeta(), r, 1, errorMessage, null, "HTTP001");
	         }
		}
			
		return true;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi)
	{
		meta=(AdvancedHTTPMeta)smi;
		data=(AdvancedHTTPData)sdi;

		if (super.init(smi, sdi))
		{
		    return true;
		}
		return false;
	}
		
	public void dispose(StepMetaInterface smi, StepDataInterface sdi)
	{
	    meta = (AdvancedHTTPMeta)smi;
	    data = (AdvancedHTTPData)sdi;
	    
	    super.dispose(smi, sdi);
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
	
	//
	// Run is were the action happens!
	public void run()
	{
    	BaseStep.runStepThread(this, meta, data);
	}
}
