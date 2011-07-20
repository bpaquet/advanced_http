package com.bpa.pentaho.plugins;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class AdvancedHTTPMeta extends BaseStepMeta implements StepMetaInterface
{
	public static String HTTP_CALL_TYPE_GET = "GET";
	public static String HTTP_CALL_TYPE_POST_FORM = "POST x-www-form-urlencoded";
		
    /** URL / service to be called */
    private String  url;

    /** function arguments : fieldname*/
    private String  argumentField[];

    /** IN / OUT / INOUT */
    private String  argumentParameter[];

    /** function bodyFieldName: new value name */
    private String httpBodyFieldName;
    
    /** function bodyFieldName: new value name */
    private String httpReturnCodeFieldName;
    
    private boolean failOnError;
    
    private boolean urlInField;
    
    private String urlField;
    
    private boolean useBasicAuth;
    
    private String basicAuthLogin;
    
    private String basicAuthPassword;
    
    private String httpCallType;

    public AdvancedHTTPMeta()
    {
        super(); // allocate BaseStepMeta
    }

	/**
     * @return Returns the argument.
     */
    public String[] getArgumentField()
    {
        return argumentField;
    }

    /**
     * @param argument The argument to set.
     */
    public void setArgumentField(String[] argument)
    {
        this.argumentField = argument;
    }

    /**
     * @return Returns the argumentDirection.
     */
    public String[] getArgumentParameter()
    {
        return argumentParameter;
    }

    /**
     * @param argumentDirection The argumentDirection to set.
     */
    public void setArgumentParameter(String[] argumentDirection)
    {
        this.argumentParameter = argumentDirection;
    }

    /**
     * @return Returns the procedure.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param procedure The procedure to set.
     */
    public void setUrl(String procedure)
    {
        this.url = procedure;
    }

    /**
     * @return Returns the http body field name.
     */
    public String getHttpBodyFieldName()
    {
        return httpBodyFieldName;
    }

    /**
     * @param httpBodyFieldName The http body field name to set.
     */
    public void setHttpBodyFieldName(String httpBodyFieldName)
    {
        this.httpBodyFieldName = httpBodyFieldName;
    }
    
    /**
     * @return Returns the http return code field name.
     */
    public String getHttpReturnCodeFieldName()
    {
        return httpReturnCodeFieldName;
    }

    /**
     * @param httpReturnCodeFieldName The http return code field name to set.
     */
    public void setHttpReturnCodeFieldName(String httpReturnCodeFieldName)
    {
        this.httpReturnCodeFieldName = httpReturnCodeFieldName;
    }
    
    /**
     * @return Returns fail on error
     */
    public boolean isFailOnError() {
		return failOnError;
	}

    /**
     * @param failOnError Fail on error
     */
    public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

    /**
     * @return Is the url coded in a field?
     */
	public boolean isUrlInField() {
		return urlInField;
	}
	
	/**
     * @param urlInField Is the url coded in a field?
     */
	public void setUrlInField(boolean urlInField) {
		this.urlInField = urlInField;
	}
	
    /**
     * @return The field name that contains the url.
     */
	public String getUrlField() {
		return urlField;
	}
	
	/**
     * @param urlField name of the field that contains the url
     */
	public void setUrlField(String urlField) {
		this.urlField = urlField;
	}
	
    /**
	 * @return the useBasicAuth
	 */
	public boolean isUseBasicAuth() {
		return useBasicAuth;
	}

	/**
	 * @param useBasicAuth the useBasicAuth to set
	 */
	public void setUseBasicAuth(boolean useBasicAuth) {
		this.useBasicAuth = useBasicAuth;
	}

	/**
	 * @return the basicAuthLogin
	 */
	public String getBasicAuthLogin() {
		return basicAuthLogin;
	}

	/**
	 * @param basicAuthLogin the basicAuthLogin to set
	 */
	public void setBasicAuthLogin(String basicAuthLogin) {
		this.basicAuthLogin = basicAuthLogin;
	}

	/**
	 * @return the basicAuthPassword
	 */
	public String getBasicAuthPassword() {
		return basicAuthPassword;
	}

	/**
	 * @param basicAuthPassword the basicAuthPassword to set
	 */
	public void setBasicAuthPassword(String basicAuthPassword) {
		this.basicAuthPassword = basicAuthPassword;
	}

	/**
	 * @return the httpCallType
	 */
	public String getHttpCallType() {
		return httpCallType;
	}

	/**
	 * @param httpCallType the httpCallType to set
	 */
	public void setHttpCallType(String httpCallType) {
		this.httpCallType = httpCallType;
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException
    {
        readData(stepnode, databases);
    }

    public void allocate(int nrargs)
    {
        argumentField = new String[nrargs];
        argumentParameter = new String[nrargs];
    }

    public Object clone()
    {
        AdvancedHTTPMeta retval = (AdvancedHTTPMeta) super.clone();
        int nrargs = argumentField.length;

        retval.allocate(nrargs);

        for (int i = 0; i < nrargs; i++)
        {
            retval.argumentField[i] = argumentField[i];
            retval.argumentParameter[i] = argumentParameter[i];
        }

        return retval;
    }

    public void setDefault()
    {
        int i;
        int nrargs;

        nrargs = 0;

        allocate(nrargs);

        for (i = 0; i < nrargs; i++)
        {
            argumentField[i] = "arg" + i; //$NON-NLS-1$
            argumentParameter[i] = "arg"; //$NON-NLS-1$
        }

        httpBodyFieldName = "http_body"; //$NON-NLS-1$
        httpReturnCodeFieldName = "http_return_code"; //$NON-NLS-1$
        
        failOnError = true;
        
        useBasicAuth = false;
        basicAuthLogin = "";
        basicAuthPassword = "";
        
        httpCallType = HTTP_CALL_TYPE_GET;
    }

    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) throws KettleStepException    
    {
        if (!Const.isEmpty(httpBodyFieldName))
        {
            ValueMetaInterface v = new ValueMeta(httpBodyFieldName, ValueMeta.TYPE_STRING);
            inputRowMeta.addValueMeta(v);
        }
        if (!Const.isEmpty(httpReturnCodeFieldName))
        {
            ValueMetaInterface v = new ValueMeta(httpReturnCodeFieldName, ValueMeta.TYPE_INTEGER);
            v.setLength(ValueMetaInterface.DEFAULT_INTEGER_LENGTH, 0);
            v.setPrecision(0);
            inputRowMeta.addValueMeta(v);
        }
    }
    
    public String getXML()
    {
        StringBuffer retval = new StringBuffer(300);

        retval.append("    ").append(XMLHandler.addTagValue("url", url)); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append("    "+XMLHandler.addTagValue("httpCallType",  httpCallType));
        retval.append("    "+XMLHandler.addTagValue("useBasicAuth",  useBasicAuth));
        retval.append("    "+XMLHandler.addTagValue("basicAuthLogin",  basicAuthLogin));
        retval.append("    "+XMLHandler.addTagValue("basicAuthPassword",  basicAuthPassword));
        retval.append("    "+XMLHandler.addTagValue("urlInField",  urlInField));
        retval.append("    "+XMLHandler.addTagValue("failOnError",  failOnError));
        retval.append("    "+XMLHandler.addTagValue("urlField",  urlField));
        retval.append("    <lookup>").append(Const.CR); //$NON-NLS-1$

        for (int i = 0; i < argumentField.length; i++)
        {
            retval.append("      <arg>").append(Const.CR); //$NON-NLS-1$
            retval.append("        ").append(XMLHandler.addTagValue("name", argumentField[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("parameter", argumentParameter[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("      </arg>").append(Const.CR); //$NON-NLS-1$
        }

        retval.append("    </lookup>").append(Const.CR); //$NON-NLS-1$

        retval.append("    <http_body_field>").append(Const.CR); //$NON-NLS-1$
        retval.append("      ").append(XMLHandler.addTagValue("name", httpBodyFieldName)); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append("    </http_body_field>").append(Const.CR); //$NON-NLS-1$

        retval.append("    <http_return_code_field>").append(Const.CR); //$NON-NLS-1$
        retval.append("      ").append(XMLHandler.addTagValue("name", httpReturnCodeFieldName)); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append("    </http_return_code_field>").append(Const.CR); //$NON-NLS-1$

        return retval.toString();
    }

    private void readData(Node stepnode, List<? extends SharedObjectInterface> databases) throws KettleXMLException
    {
        try
        {
            int nrargs;

            url = XMLHandler.getTagValue(stepnode, "url"); //$NON-NLS-1$
            httpCallType = XMLHandler.getTagValue(stepnode, "httpCallType");
            useBasicAuth = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "useBasicAuth"));
            basicAuthLogin = XMLHandler.getTagValue(stepnode, "basicAuthLogin");
            basicAuthPassword = XMLHandler.getTagValue(stepnode, "basicAuthPassword");
            failOnError ="Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "failOnError"));
            urlInField="Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "urlInField"));
            urlField       = XMLHandler.getTagValue(stepnode, "urlField");
			
            Node lookup = XMLHandler.getSubNode(stepnode, "lookup"); //$NON-NLS-1$
            nrargs = XMLHandler.countNodes(lookup, "arg"); //$NON-NLS-1$

            allocate(nrargs);

            for (int i = 0; i < nrargs; i++)
            {
                Node anode = XMLHandler.getSubNodeByNr(lookup, "arg", i); //$NON-NLS-1$

                argumentField[i] = XMLHandler.getTagValue(anode, "name"); //$NON-NLS-1$
                argumentParameter[i] = XMLHandler.getTagValue(anode, "parameter"); //$NON-NLS-1$
            }

            httpBodyFieldName = XMLHandler.getTagValue(stepnode, "http_body_field", "name"); // Optional, can be null //$NON-NLS-1$
            httpReturnCodeFieldName = XMLHandler.getTagValue(stepnode, "http_return_code_field", "name"); // Optional, can be null //$NON-NLS-1$
        }
        catch (Exception e)
        {
            throw new KettleXMLException(Messages.getString("AdvancedHTTPMeta.Exception.UnableToReadStepInfo"), e); //$NON-NLS-1$
        }
    }

    public void readRep(Repository rep, long id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException
    {
        try
        {
            url = rep.getStepAttributeString(id_step, "url"); //$NON-NLS-1$
            httpCallType	=	   rep.getStepAttributeString (id_step, "httpCallType");
            useBasicAuth =      rep.getStepAttributeBoolean (id_step, "useBasicAuth");
            basicAuthLogin	=	   rep.getStepAttributeString (id_step, "basicAuthLogin");
            basicAuthPassword	=	   rep.getStepAttributeString (id_step, "basicAuthPassword");
            failOnError =      rep.getStepAttributeBoolean (id_step, "failOnError");
            urlInField =      rep.getStepAttributeBoolean (id_step, "urlInField");
            urlField	=	   rep.getStepAttributeString (id_step, "urlField");
			
            int nrargs = rep.countNrStepAttributes(id_step, "arg_name"); //$NON-NLS-1$
            allocate(nrargs);

            for (int i = 0; i < nrargs; i++)
            {
                argumentField[i] = rep.getStepAttributeString(id_step, i, "arg_name"); //$NON-NLS-1$
                argumentParameter[i] = rep.getStepAttributeString(id_step, i, "arg_parameter"); //$NON-NLS-1$
            }

            httpBodyFieldName = rep.getStepAttributeString(id_step, "http_body_field_name"); //$NON-NLS-1$
            httpReturnCodeFieldName = rep.getStepAttributeString(id_step, "http_return_code_field_name"); //$NON-NLS-1$
        }
        catch (Exception e)
        {
            throw new KettleException(Messages.getString("AdvancedHTTPMeta.Exception.UnexpectedErrorReadingStepInfo"), e); //$NON-NLS-1$
        }
    }

    public void saveRep(Repository rep, long id_transformation, long id_step) throws KettleException
    {
        try
        {
            rep.saveStepAttribute(id_transformation, id_step, "url", url); //$NON-NLS-1$
            rep.saveStepAttribute(id_transformation, id_step, "httpCallType",   httpCallType);
            rep.saveStepAttribute(id_transformation, id_step, "useBasicAuth",   useBasicAuth);
            rep.saveStepAttribute(id_transformation, id_step, "basicAuthLogin",   basicAuthLogin);
            rep.saveStepAttribute(id_transformation, id_step, "basicAuthPassword",   basicAuthPassword);
			rep.saveStepAttribute(id_transformation, id_step, "failOnError",   failOnError);
			rep.saveStepAttribute(id_transformation, id_step, "urlInField",   urlInField);
			rep.saveStepAttribute(id_transformation, id_step, "urlField",   urlField);
			
            for (int i = 0; i < argumentField.length; i++)
            {
                rep.saveStepAttribute(id_transformation, id_step, i, "arg_name", argumentField[i]); //$NON-NLS-1$
                rep.saveStepAttribute(id_transformation, id_step, i, "arg_parameter", argumentParameter[i]); //$NON-NLS-1$
            }

            rep.saveStepAttribute(id_transformation, id_step, "http_body_field_name", httpBodyFieldName); //$NON-NLS-1$
            rep.saveStepAttribute(id_transformation, id_step, "http_return_code_field_name", httpReturnCodeFieldName); //$NON-NLS-1$
        }
        catch (Exception e)
        {
            throw new KettleException(Messages.getString("AdvancedHTTPMeta.Exception.UnableToSaveStepInfo") + id_step, e); //$NON-NLS-1$
        }
    }

    public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info)
    {
        CheckResult cr;

        // See if we have input streams leading to this step!
        if (input.length > 0)
        {
            cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, Messages.getString("AdvancedHTTPMeta.CheckResult.ReceivingInfoFromOtherSteps"), stepMeta); //$NON-NLS-1$
            remarks.add(cr);
        }
        else
        {
            cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.NoInpuReceived"), stepMeta); //$NON-NLS-1$
            remarks.add(cr);
        }
        // check Url
        if(urlInField)
        {
        	if(Const.isEmpty(urlField))
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.UrlfieldMissing"), stepMeta);	
        	else
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.UrlfieldOk"), stepMeta);	
        	
        }else
        {
        	if(Const.isEmpty(url))
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.UrlMissing"), stepMeta);
        	else
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, Messages.getString("AdvancedHTTPMeta.CheckResult.UrlOk"), stepMeta);
        }
        remarks.add(cr);
        if (useBasicAuth) {
        	if(Const.isEmpty(basicAuthLogin)) {
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.BasicAuthLoginMissing"), stepMeta);
        		remarks.add(cr);
        	}
        	if(Const.isEmpty(basicAuthPassword)) {
        		cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, Messages.getString("AdvancedHTTPMeta.CheckResult.BasicAuthPasswordMissing"), stepMeta);
        		remarks.add(cr);
        	}
        }
        
    }

    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans trans)
    {
        return new AdvancedHTTP(stepMeta, stepDataInterface, cnr, transMeta, trans);
    }

    public StepDataInterface getStepData()
    {
        return new AdvancedHTTPData();
    }

    public boolean supportsErrorHandling()
    {
        return true;
    }
}
