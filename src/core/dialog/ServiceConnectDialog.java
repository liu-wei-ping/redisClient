package core.dialog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import core.RedisClientWindow;
import core.action.RedisHostAction;
import core.message.RedisConnectMsg;
import core.utils.CacheConstant;
import core.utils.PropertiesUtil;
import core.utils.RedisPoolUtil;

/**
 * @author liu_wp
 * @date 2018年1月6日
 * @see
 */
public class ServiceConnectDialog extends Dialog {
	private Text hostNameText;
	private Text portText;
	private Text hostTest;
	private Text passwordText;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public ServiceConnectDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {// 如果单击确定按钮，
			Map<String, String> map = new HashMap<>();
			map.put(CacheConstant.REDIS_HOST, hostTest.getText());
			map.put(CacheConstant.REDIS_PORT, portText.getText());
			map.put(CacheConstant.REDIS_PASSWORD, passwordText.getText());
			String message = "redis服务切换失败！";
			if (PropertiesUtil.writeProperties(map, CacheConstant.REDIS_HOST_FILE, false)) {
				message = "redis服务切换成功！";
				// TODO 服务器
				// RedisConnectPo redisConnectPo = new RedisConnectPo();
				// CacheConstant.redisPoMap.put(key, redisConnectPo);
				boolean bool = RedisPoolUtil.init() && RedisPoolUtil.testRedis();
				RedisConnectMsg redisConnectMsg = CacheConstant.redisConnectMsgMap.get(hostTest.getText());
				if (bool) {
					RedisClientWindow.getRedisClientWindow().getServer().add(new RedisHostAction(hostTest.getText()));
					String msg = redisConnectMsg.getResultMsg() + "【" + hostTest.getText() + ":" + portText.getText()
							+ "】";
					RedisClientWindow.getRedisClientWindow().getStatusLineManager().setMessage(msg);
					MessageDialog.openInformation(this.getShell(), "提示", redisConnectMsg.getResultMsg());
					okPressed();
				} else {
					MessageDialog.openError(RedisClientWindow.getRedisClientWindow().getShell(), null,
							redisConnectMsg.getResultMsg());
				}
			} else {
				MessageDialog.openError(RedisClientWindow.getRedisClientWindow().getShell(), null, message + " 连接失败！");
			}

		} else {
			cancelPressed();
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("添加服务器");
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确认", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setText("名称");

		hostNameText = new Text(container, SWT.BORDER);
		GridData gd_hostNameText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_hostNameText.widthHint = 296;
		hostNameText.setLayoutData(gd_hostNameText);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setText("主机");

		hostTest = new Text(container, SWT.BORDER);
		GridData gd_hostTest = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_hostTest.widthHint = 285;
		hostTest.setLayoutData(gd_hostTest);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("端口");

		portText = new Text(container, SWT.BORDER);
		portText.setText("6379");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		lblNewLabel_3.setText("密码");

		passwordText = new Text(container, SWT.BORDER);
		GridData gd_passwordText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_passwordText.widthHint = 271;
		passwordText.setLayoutData(gd_passwordText);

		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(525, 300);
	}

}
