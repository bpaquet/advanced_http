package com.bpa.pentaho.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


public class AdvancedHTTPDialog extends BaseStepDialog implements StepDialogInterface
{
	
	private Label        wlHttpCallType;
	private CCombo       wHttpCallType;
	private FormData     fdlHttpCallType, fdHttpCallType;
	    
	private Label        wlUrl;
	private TextVar      wUrl;
	private FormData     fdlUrl, fdUrl;

	private Label        wlHttpBodyFieldName;
	private Text         wHttpBodyFieldName;
	private FormData     fdlHttpBodyFieldName, fdHttpBodyFieldName;

	private Label        wlHttpReturnCodeFieldName;
	private Text         wHttpReturnCodeFieldName;
	private FormData     fdlHttpReturnCodeFieldName, fdHttpReturnCodeFieldName;

	private Label        wlHttpStartTimeFieldName;
	private Text         wHttpStartTimeFieldName;
	private FormData     fdlHttpStartTimeFieldName, fdHttpStartTimeFieldName;

	private Label        wlHttpRequestTimeFieldName;
	private Text         wHttpRequestTimeFieldName;
	private FormData     fdlHttpRequestTimeFieldName, fdHttpRequestTimeFieldName;
	
	private Label        wlFields;
	private TableView    wFields;
	private FormData     fdlFields, fdFields;
	
	private Label        wlUrlInField;
    private Button       wUrlInField;
    private FormData     fdlUrlInField, fdUrlInField;
	
    private Label        wlFailOnError;
    private Button       wFailOnError;
    private FormData     fdlFailOnError, fdFailOnError;
	
    private Label        wlStrictSSLCheck;
    private Button       wStrictSSLCheck;
    private FormData     fdlStrictSSLCheck, fdStrictSSLCheck;
	
    private Label        wlOnlySSLv3;
    private Button       wOnlySSLv3;
    private FormData     fdlOnlySSLv3, fdOnlySSLv3;
	
	private Label        wlUrlField;
	private ComboVar     wUrlField;
	private FormData     fdlUrlField, fdUrlField;
	
	private Label        wlUseBasicAuth;
    private Button       wUseBasicAuth;
    private FormData     fdlUseBasicAuth, fdUseBasicAuth;
	
	private Label        wlBasicAuthLogin;
	private Text         wBasicAuthLogin;
	private FormData     fdlBasicAuthLogin, fdBasicAuthLogin;

	private Label        wlBasicAuthPassword;
	private Text         wBasicAuthPassword;
	private FormData     fdlBasicAuthPassword, fdBasicAuthPassword;

	private Button wGet;
	private Listener lsGet;

	private AdvancedHTTPMeta input;
	
	private ColumnInfo[] colinf;
	
    private Map<String, Integer> inputFields;

	public AdvancedHTTPDialog(Shell parent, Object in, TransMeta transMeta, String sname)
	{
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input=(AdvancedHTTPMeta)in;
        inputFields =new HashMap<String, Integer>();
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
        
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("AdvancedHTTPDialog.Shell.Title")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin=Const.MARGIN;

		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("AdvancedHTTPDialog.Stepname.Label")); //$NON-NLS-1$
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, -margin);
		fdlStepname.top  = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		
		 // HttpCallType
        wlHttpCallType=new Label(shell, SWT.RIGHT);
        wlHttpCallType.setText(Messages.getString("AdvancedHTTPDialog.HttpCallType.Label"));
        props.setLook(wlHttpCallType);
        fdlHttpCallType=new FormData();
        fdlHttpCallType.left = new FormAttachment(0, 0);
        fdlHttpCallType.top  = new FormAttachment(wStepname, 2*margin);
        fdlHttpCallType.right= new FormAttachment(middle, -margin);
        wlHttpCallType.setLayoutData(fdlHttpCallType);
        wHttpCallType=new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
        wHttpCallType.setEditable(true);
        props.setLook(wHttpCallType);
        wHttpCallType.addModifyListener(lsMod);
        fdHttpCallType=new FormData();
        fdHttpCallType.left = new FormAttachment(middle, margin);
        fdHttpCallType.top  = new FormAttachment(wStepname,2*margin);
        fdHttpCallType.right= new FormAttachment(100, 0);
        wHttpCallType.setLayoutData(fdHttpCallType);
        wHttpCallType.add(AdvancedHTTPMeta.HTTP_CALL_TYPE_GET);
        wHttpCallType.add(AdvancedHTTPMeta.HTTP_CALL_TYPE_POST_FORM);
        
		wlUrl=new Label(shell, SWT.RIGHT);
		wlUrl.setText(Messages.getString("AdvancedHTTPDialog.URL.Label")); //$NON-NLS-1$
 		props.setLook(wlUrl);
		fdlUrl=new FormData();
		fdlUrl.left = new FormAttachment(0, 0);
		fdlUrl.right= new FormAttachment(middle, -margin);
		fdlUrl.top  = new FormAttachment(wHttpCallType, margin*2);
		wlUrl.setLayoutData(fdlUrl);

		wUrl=new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wUrl);
		wUrl.addModifyListener(lsMod);
		fdUrl=new FormData();
		fdUrl.left = new FormAttachment(middle, 0);
		fdUrl.top  = new FormAttachment(wHttpCallType, margin*2);
		fdUrl.right= new FormAttachment(100, 0);
		wUrl.setLayoutData(fdUrl);
		
		// Fail on error line
        wlFailOnError=new Label(shell, SWT.RIGHT);
        wlFailOnError.setText(Messages.getString("AdvancedHTTPDialog.FailOnError.Label"));
        props.setLook(wlFailOnError);
        fdlFailOnError=new FormData();
        fdlFailOnError.left = new FormAttachment(0, 0);
        fdlFailOnError.top  = new FormAttachment(wUrl, margin);
        fdlFailOnError.right= new FormAttachment(middle, -margin);
        wlFailOnError.setLayoutData(fdlFailOnError);
        wFailOnError=new Button(shell, SWT.CHECK );
        props.setLook(wFailOnError);
        fdFailOnError=new FormData();
        fdFailOnError.left = new FormAttachment(middle, 0);
        fdFailOnError.top  = new FormAttachment(wUrl, margin);
        fdFailOnError.right= new FormAttachment(100, 0);
        wFailOnError.setLayoutData(fdFailOnError);
        wFailOnError.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
            	input.setChanged();
            }
        }
    );
        
		// Stric ssl check line
        wlStrictSSLCheck=new Label(shell, SWT.RIGHT);
        wlStrictSSLCheck.setText(Messages.getString("AdvancedHTTPDialog.StrictSSLCheck.Label"));
        props.setLook(wlStrictSSLCheck);
        fdlStrictSSLCheck=new FormData();
        fdlStrictSSLCheck.left = new FormAttachment(0, 0);
        fdlStrictSSLCheck.top  = new FormAttachment(wFailOnError, margin);
        fdlStrictSSLCheck.right= new FormAttachment(middle, -margin);
        wlStrictSSLCheck.setLayoutData(fdlStrictSSLCheck);
        wStrictSSLCheck=new Button(shell, SWT.CHECK );
        props.setLook(wStrictSSLCheck);
        fdStrictSSLCheck=new FormData();
        fdStrictSSLCheck.left = new FormAttachment(middle, 0);
        fdStrictSSLCheck.top  = new FormAttachment(wFailOnError, margin);
        fdStrictSSLCheck.right= new FormAttachment(100, 0);
        wStrictSSLCheck.setLayoutData(fdStrictSSLCheck);
        wStrictSSLCheck.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
            	input.setChanged();
            }
        }
    );
    
    	// Stric ssl check line
        wlOnlySSLv3=new Label(shell, SWT.RIGHT);
        wlOnlySSLv3.setText(Messages.getString("AdvancedHTTPDialog.OnlySSLv3.Label"));
        props.setLook(wlOnlySSLv3);
        fdlOnlySSLv3=new FormData();
        fdlOnlySSLv3.left = new FormAttachment(0, 0);
        fdlOnlySSLv3.top  = new FormAttachment(wlStrictSSLCheck, margin);
        fdlOnlySSLv3.right= new FormAttachment(middle, -margin);
        wlOnlySSLv3.setLayoutData(fdlOnlySSLv3);
        wOnlySSLv3=new Button(shell, SWT.CHECK );
        props.setLook(wOnlySSLv3);
        fdOnlySSLv3=new FormData();
        fdOnlySSLv3.left = new FormAttachment(middle, 0);
        fdOnlySSLv3.top  = new FormAttachment(wlStrictSSLCheck, margin);
        fdOnlySSLv3.right= new FormAttachment(100, 0);
        wOnlySSLv3.setLayoutData(fdOnlySSLv3);
        wOnlySSLv3.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
            	input.setChanged();
            }
        }
    );
    
		// UrlInField line
        wlUrlInField=new Label(shell, SWT.RIGHT);
        wlUrlInField.setText(Messages.getString("AdvancedHTTPDialog.UrlInField.Label"));
        props.setLook(wlUrlInField);
        fdlUrlInField=new FormData();
        fdlUrlInField.left = new FormAttachment(0, 0);
        fdlUrlInField.top  = new FormAttachment(wlOnlySSLv3, margin);
        fdlUrlInField.right= new FormAttachment(middle, -margin);
        wlUrlInField.setLayoutData(fdlUrlInField);
        wUrlInField=new Button(shell, SWT.CHECK );
        props.setLook(wUrlInField);
        fdUrlInField=new FormData();
        fdUrlInField.left = new FormAttachment(middle, 0);
        fdUrlInField.top  = new FormAttachment(wlOnlySSLv3, margin);
        fdUrlInField.right= new FormAttachment(100, 0);
        wUrlInField.setLayoutData(fdUrlInField);
        wUrlInField.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	input.setChanged();
                	activeUrlInfield();
                }
            }
        );

		// UrlField Line
		wlUrlField=new Label(shell, SWT.RIGHT);
		wlUrlField.setText(Messages.getString("AdvancedHTTPDialog.UrlField.Label")); //$NON-NLS-1$
 		props.setLook(wlUrlField);
		fdlUrlField=new FormData();
		fdlUrlField.left = new FormAttachment(0, 0);
		fdlUrlField.right= new FormAttachment(middle, -margin);
		fdlUrlField.top  = new FormAttachment(wUrlInField, margin);
		wlUrlField.setLayoutData(fdlUrlField);

    	wUrlField=new ComboVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    	wUrlField.setToolTipText(Messages.getString("AdvancedHTTPDialog.UrlField.Tooltip"));
		props.setLook(wUrlField);
		wUrlField.addModifyListener(lsMod);
		fdUrlField=new FormData();
		fdUrlField.left = new FormAttachment(middle, 0);
		fdUrlField.top  = new FormAttachment(wUrlInField, margin);
		fdUrlField.right= new FormAttachment(100, 0);
		wUrlField.setLayoutData(fdUrlField);
		wUrlField.setEnabled(false);
		wUrlField.addFocusListener(new FocusListener()
         {
            public void focusLost(org.eclipse.swt.events.FocusEvent e)
             {
             }
             public void focusGained(org.eclipse.swt.events.FocusEvent e)
             {
                 Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                 shell.setCursor(busy);
                 BaseStepDialog.getFieldsFromPrevious(wUrlField, transMeta, stepMeta);
                 shell.setCursor(null);
                 busy.dispose();
             }
         }
     );        
		
		
		// HttpBodyFieldName line...
		wlHttpBodyFieldName=new Label(shell, SWT.RIGHT);
		wlHttpBodyFieldName.setText(Messages.getString("AdvancedHTTPDialog.HttpBodyFieldName.Label")); //$NON-NLS-1$
 		props.setLook(wlHttpBodyFieldName);
		fdlHttpBodyFieldName=new FormData();
		fdlHttpBodyFieldName.left = new FormAttachment(0, 0);
		fdlHttpBodyFieldName.right= new FormAttachment(middle, -margin);
		fdlHttpBodyFieldName.top  = new FormAttachment(wUrlField, margin*2);
		wlHttpBodyFieldName.setLayoutData(fdlHttpBodyFieldName);
		wHttpBodyFieldName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wHttpBodyFieldName);
		wHttpBodyFieldName.addModifyListener(lsMod);
		fdHttpBodyFieldName=new FormData();
		fdHttpBodyFieldName.left = new FormAttachment(middle, 0);
		fdHttpBodyFieldName.top  = new FormAttachment(wUrlField, margin*2);
		fdHttpBodyFieldName.right= new FormAttachment(100, 0);
		wHttpBodyFieldName.setLayoutData(fdHttpBodyFieldName);

		// HttpReturnCodeFieldName line...
		wlHttpReturnCodeFieldName=new Label(shell, SWT.RIGHT);
		wlHttpReturnCodeFieldName.setText(Messages.getString("AdvancedHTTPDialog.HttpReturnCodeFieldName.Label")); //$NON-NLS-1$
 		props.setLook(wlHttpReturnCodeFieldName);
		fdlHttpReturnCodeFieldName=new FormData();
		fdlHttpReturnCodeFieldName.left = new FormAttachment(0, 0);
		fdlHttpReturnCodeFieldName.right= new FormAttachment(middle, -margin);
		fdlHttpReturnCodeFieldName.top  = new FormAttachment(wHttpBodyFieldName, margin*2);
		wlHttpReturnCodeFieldName.setLayoutData(fdlHttpReturnCodeFieldName);
		wHttpReturnCodeFieldName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wHttpReturnCodeFieldName);
		wHttpReturnCodeFieldName.addModifyListener(lsMod);
		fdHttpReturnCodeFieldName=new FormData();
		fdHttpReturnCodeFieldName.left = new FormAttachment(middle, 0);
		fdHttpReturnCodeFieldName.top  = new FormAttachment(wHttpBodyFieldName, margin*2);
		fdHttpReturnCodeFieldName.right= new FormAttachment(100, 0);
		wHttpReturnCodeFieldName.setLayoutData(fdHttpReturnCodeFieldName);
		
		// HttpStartTimeFieldName line...
		wlHttpStartTimeFieldName=new Label(shell, SWT.RIGHT);
		wlHttpStartTimeFieldName.setText(Messages.getString("AdvancedHTTPDialog.HttpStartTimeFieldName.Label")); //$NON-NLS-1$
 		props.setLook(wlHttpStartTimeFieldName);
		fdlHttpStartTimeFieldName=new FormData();
		fdlHttpStartTimeFieldName.left = new FormAttachment(0, 0);
		fdlHttpStartTimeFieldName.right= new FormAttachment(middle, -margin);
		fdlHttpStartTimeFieldName.top  = new FormAttachment(wHttpReturnCodeFieldName, margin*2);
		wlHttpStartTimeFieldName.setLayoutData(fdlHttpStartTimeFieldName);
		wHttpStartTimeFieldName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wHttpStartTimeFieldName);
		wHttpStartTimeFieldName.addModifyListener(lsMod);
		fdHttpStartTimeFieldName=new FormData();
		fdHttpStartTimeFieldName.left = new FormAttachment(middle, 0);
		fdHttpStartTimeFieldName.top  = new FormAttachment(wHttpReturnCodeFieldName, margin*2);
		fdHttpStartTimeFieldName.right= new FormAttachment(100, 0);
		wHttpStartTimeFieldName.setLayoutData(fdHttpStartTimeFieldName);
		
		// HttpRequestTimeFieldName line...
		wlHttpRequestTimeFieldName=new Label(shell, SWT.RIGHT);
		wlHttpRequestTimeFieldName.setText(Messages.getString("AdvancedHTTPDialog.HttpRequestTimeFieldName.Label")); //$NON-NLS-1$
 		props.setLook(wlHttpRequestTimeFieldName);
		fdlHttpRequestTimeFieldName=new FormData();
		fdlHttpRequestTimeFieldName.left = new FormAttachment(0, 0);
		fdlHttpRequestTimeFieldName.right= new FormAttachment(middle, -margin);
		fdlHttpRequestTimeFieldName.top  = new FormAttachment(wHttpStartTimeFieldName, margin*2);
		wlHttpRequestTimeFieldName.setLayoutData(fdlHttpRequestTimeFieldName);
		wHttpRequestTimeFieldName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wHttpRequestTimeFieldName);
		wHttpRequestTimeFieldName.addModifyListener(lsMod);
		fdHttpRequestTimeFieldName=new FormData();
		fdHttpRequestTimeFieldName.left = new FormAttachment(middle, 0);
		fdHttpRequestTimeFieldName.top  = new FormAttachment(wHttpStartTimeFieldName, margin*2);
		fdHttpRequestTimeFieldName.right= new FormAttachment(100, 0);
		wHttpRequestTimeFieldName.setLayoutData(fdHttpRequestTimeFieldName);
		
		// Use basic auth line
        wlUseBasicAuth=new Label(shell, SWT.RIGHT);
        wlUseBasicAuth.setText(Messages.getString("AdvancedHTTPDialog.UseBasicAuth.Label"));
        props.setLook(wlUseBasicAuth);
        fdlUseBasicAuth=new FormData();
        fdlUseBasicAuth.left = new FormAttachment(0, 0);
        fdlUseBasicAuth.top  = new FormAttachment(wHttpRequestTimeFieldName, margin);
        fdlUseBasicAuth.right= new FormAttachment(middle, -margin);
        wlUseBasicAuth.setLayoutData(fdlUseBasicAuth);
        wUseBasicAuth=new Button(shell, SWT.CHECK );
        props.setLook(wUseBasicAuth);
        fdUseBasicAuth=new FormData();
        fdUseBasicAuth.left = new FormAttachment(middle, 0);
        fdUseBasicAuth.top  = new FormAttachment(wHttpRequestTimeFieldName);
        fdUseBasicAuth.right= new FormAttachment(100, 0);
        wUseBasicAuth.setLayoutData(fdUseBasicAuth);
        wUseBasicAuth.addSelectionListener(new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e) 
            {
            	input.setChanged();
            	activeBasicAuthFields();
            }
        }
    );
        
        // BasicAuthLogin line...
		wlBasicAuthLogin=new Label(shell, SWT.RIGHT);
		wlBasicAuthLogin.setText(Messages.getString("AdvancedHTTPDialog.BasicAuthLogin.Label")); //$NON-NLS-1$
 		props.setLook(wlBasicAuthLogin);
		fdlBasicAuthLogin=new FormData();
		fdlBasicAuthLogin.left = new FormAttachment(0, 0);
		fdlBasicAuthLogin.right= new FormAttachment(middle, -margin);
		fdlBasicAuthLogin.top  = new FormAttachment(wUseBasicAuth, margin*2);
		wlBasicAuthLogin.setLayoutData(fdlBasicAuthLogin);
		wBasicAuthLogin=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wBasicAuthLogin);
		wBasicAuthLogin.addModifyListener(lsMod);
		fdBasicAuthLogin=new FormData();
		fdBasicAuthLogin.left = new FormAttachment(middle, 0);
		fdBasicAuthLogin.top  = new FormAttachment(wUseBasicAuth, margin*2);
		fdBasicAuthLogin.right= new FormAttachment(100, 0);
		wBasicAuthLogin.setLayoutData(fdBasicAuthLogin);
		
		 // BasicAuthPassword line...
		wlBasicAuthPassword=new Label(shell, SWT.RIGHT);
		wlBasicAuthPassword.setText(Messages.getString("AdvancedHTTPDialog.BasicAuthPassword.Label")); //$NON-NLS-1$
 		props.setLook(wlBasicAuthPassword);
		fdlBasicAuthPassword=new FormData();
		fdlBasicAuthPassword.left = new FormAttachment(0, 0);
		fdlBasicAuthPassword.right= new FormAttachment(middle, -margin);
		fdlBasicAuthPassword.top  = new FormAttachment(wBasicAuthLogin, margin*2);
		wlBasicAuthPassword.setLayoutData(fdlBasicAuthPassword);
		wBasicAuthPassword=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wBasicAuthPassword);
		wBasicAuthPassword.addModifyListener(lsMod);
		fdBasicAuthPassword=new FormData();
		fdBasicAuthPassword.left = new FormAttachment(middle, 0);
		fdBasicAuthPassword.top  = new FormAttachment(wBasicAuthLogin, margin*2);
		fdBasicAuthPassword.right= new FormAttachment(100, 0);
		wBasicAuthPassword.setLayoutData(fdBasicAuthPassword);
		
		wlFields=new Label(shell, SWT.NONE);
		wlFields.setText(Messages.getString("AdvancedHTTPDialog.Parameters.Label")); //$NON-NLS-1$
 		props.setLook(wlFields);
		fdlFields=new FormData();
		fdlFields.left = new FormAttachment(0, 0);
		fdlFields.top  = new FormAttachment(wlBasicAuthPassword, margin);
		wlFields.setLayoutData(fdlFields);
		
		final int FieldsRows=input.getArgumentField().length;
		
		 colinf=new ColumnInfo[] { 
		  new ColumnInfo(Messages.getString("AdvancedHTTPDialog.ColumnInfo.Name"),      ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false),
		  new ColumnInfo(Messages.getString("AdvancedHTTPDialog.ColumnInfo.Parameter"),  ColumnInfo.COLUMN_TYPE_TEXT,   false), //$NON-NLS-1$
        };
		
		wFields=new TableView(transMeta, shell, 
							  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
							  colinf, 
							  FieldsRows,  
							  lsMod,
							  props
							  );

		fdFields=new FormData();
		fdFields.left  = new FormAttachment(0, 0);
		fdFields.top   = new FormAttachment(wlFields, margin);
		fdFields.right = new FormAttachment(100, 0);
		fdFields.bottom= new FormAttachment(100, -50);
		wFields.setLayoutData(fdFields);

		  // 
        // Search the fields in the background
		
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                StepMeta stepMeta = transMeta.findStep(stepname);
                if (stepMeta!=null)
                {
                    try
                    {
                    	RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);
                       
                        // Remember these fields...
                        for (int i=0;i<row.size();i++)
                        {
                            inputFields.put(row.getValueMeta(i).getName(), Integer.valueOf(i));
                        }
                        setComboBoxes();
                    }
                    catch(KettleException e)
                    {
                    	log.logError(toString(), Messages.getString("System.Dialog.GetFieldsFailed.Message"));
                    }
                }
            }
        };
        new Thread(runnable).start();

		// THE BUTTONS
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK")); //$NON-NLS-1$
		wGet=new Button(shell, SWT.PUSH);
		wGet.setText(Messages.getString("AdvancedHTTPDialog.GetFields.Button")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel")); //$NON-NLS-1$

		setButtonPositions(new Button[] { wOK, wCancel , wGet }, margin, wFields);

		// Add listeners
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();        } };
		lsGet      = new Listener() { public void handleEvent(Event e) { get();        } };
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel();    } };
		
		wOK.addListener    (SWT.Selection, lsOK    );
		wGet.addListener   (SWT.Selection, lsGet   );
		wCancel.addListener(SWT.Selection, lsCancel);
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
        wUrl.addSelectionListener( lsDef );
        wHttpBodyFieldName.addSelectionListener( lsDef );
        wHttpReturnCodeFieldName.addSelectionListener( lsDef );
        wHttpStartTimeFieldName.addSelectionListener( lsDef );
        wHttpRequestTimeFieldName.addSelectionListener( lsDef );
        
        wBasicAuthLogin.addSelectionListener( lsDef );
        wBasicAuthPassword.addSelectionListener( lsDef );
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		lsResize = new Listener() 
		{
			public void handleEvent(Event event) 
			{
				Point size = shell.getSize();
				wFields.setSize(size.x-10, size.y-50);
				wFields.table.setSize(size.x-10, size.y-50);
				wFields.redraw();
			}
		};
		shell.addListener(SWT.Resize, lsResize);

		// Set the shell size, based upon previous time...
		setSize();
		
		getData();
		activeUrlInfield();
		activeBasicAuthFields();
		input.setChanged(changed);

		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}
	protected void setComboBoxes()
    {
        // Something was changed in the row.
        //
        final Map<String, Integer> fields = new HashMap<String, Integer>();
        
        // Add the currentMeta fields...
        fields.putAll(inputFields);
        
        Set<String> keySet = fields.keySet();
        List<String> entries = new ArrayList<String>(keySet);

        String fieldNames[] = (String[]) entries.toArray(new String[entries.size()]);

        Const.sortStrings(fieldNames);
        colinf[0].setComboValues(fieldNames);
    }
	private void activeUrlInfield()
	{
		wlUrlField.setEnabled(wUrlInField.getSelection());
		wUrlField.setEnabled(wUrlInField.getSelection());
		wlUrl.setEnabled(!wUrlInField.getSelection());
		wUrl.setEnabled(!wUrlInField.getSelection());    
	}
	
	private void activeBasicAuthFields() {
		wBasicAuthLogin.setEnabled(wUseBasicAuth.getSelection());
		wBasicAuthPassword.setEnabled(wUseBasicAuth.getSelection());
	}
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
		int i;
		log.logDebug(toString(), Messages.getString("AdvancedHTTPDialog.Log.GettingKeyInfo")); //$NON-NLS-1$
		
		if (input.getArgumentField()!=null)
		for (i=0;i<input.getArgumentField().length;i++)
		{
			TableItem item = wFields.table.getItem(i);
			if (input.getArgumentField()[i]      !=null) item.setText(1, input.getArgumentField()[i]);
			if (input.getArgumentParameter()[i]  !=null) item.setText(2, input.getArgumentParameter()[i]);
		}
		
		if (input.getUrl() !=null)      wUrl.setText(input.getUrl());
        wUrlInField.setSelection(input.isUrlInField());
        if (input.getUrlField() !=null) wUrlField.setText(input.getUrlField());
        
        wFailOnError.setSelection(input.isFailOnError());
        wStrictSSLCheck.setSelection(input.isStrictSSLCheck());
        wOnlySSLv3.setSelection(input.isOnlySSLv3());
        
        wHttpCallType.setText(input.getHttpCallType());
        wUseBasicAuth.setSelection(input.isUseBasicAuth());
        if (input.getBasicAuthLogin() != null) wBasicAuthLogin.setText(input.getBasicAuthLogin());
        if (input.getBasicAuthPassword() != null) wBasicAuthPassword.setText(input.getBasicAuthPassword());
        
		if (input.getHttpBodyFieldName()!=null) wHttpBodyFieldName.setText(input.getHttpBodyFieldName());
		if (input.getHttpReturnCodeFieldName()!=null) wHttpReturnCodeFieldName.setText(input.getHttpReturnCodeFieldName());
		if (input.getHttpStartTimeFieldName()!=null) wHttpStartTimeFieldName.setText(input.getHttpStartTimeFieldName());
		if (input.getHttpRequestTimeFieldName()!=null) wHttpRequestTimeFieldName.setText(input.getHttpRequestTimeFieldName());

		wFields.setRowNums();
		wFields.optWidth(true);
		wStepname.selectAll();
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(changed);
		dispose();
	}
	
	private void ok()
	{
		if (Const.isEmpty(wStepname.getText())) return;

		int nrargs = wFields.nrNonEmpty();

		input.allocate(nrargs);

		log.logDebug(toString(), Messages.getString("AdvancedHTTPDialog.Log.FoundArguments",String.valueOf(nrargs))); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i=0;i<nrargs;i++)
		{
			TableItem item = wFields.getNonEmpty(i);
			input.getArgumentField()[i]       = item.getText(1);
			input.getArgumentParameter()[i]    = item.getText(2);
		}

		input.setHttpCallType(wHttpCallType.getText());
		input.setUseBasicAuth(wUseBasicAuth.getSelection());
		input.setBasicAuthLogin(wBasicAuthLogin.getText());
		input.setBasicAuthPassword(wBasicAuthPassword.getText());
		
		input.setUrl( wUrl.getText() );
		input.setUrlField(wUrlField.getText() );
		input.setUrlInField(wUrlInField.getSelection() );
		input.setFailOnError( wFailOnError.getSelection() );
		input.setStrictSSLCheck( wStrictSSLCheck.getSelection() );
		input.setOnlySSLv3( wOnlySSLv3.getSelection() );
		input.setHttpBodyFieldName( wHttpBodyFieldName.getText() );
		input.setHttpReturnCodeFieldName( wHttpReturnCodeFieldName.getText() );
		input.setHttpStartTimeFieldName( wHttpStartTimeFieldName.getText() );
		input.setHttpRequestTimeFieldName( wHttpRequestTimeFieldName.getText() );
		stepname = wStepname.getText(); // return value

		dispose();
	}

	private void get()
	{
		try
		{
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r!=null && !r.isEmpty())
			{
                BaseStepDialog.getFieldsFromPrevious(r, wFields, 1, new int[] { 1, 2 }, new int[] { 3 }, -1, -1, null);
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, Messages.getString("AdvancedHTTPDialog.FailedToGetFields.DialogTitle"), Messages.getString("AdvancedHTTPDialog.FailedToGetFields.DialogMessage"), ke); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public String toString()
	{
		return this.getClass().getName();
	}
}