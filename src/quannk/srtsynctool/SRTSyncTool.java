package quannk.srtsynctool;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class SRTSyncTool extends ApplicationWindow {
	private Text enText[] = new Text[5];
	private Text viText[] = new Text[5];
	private Text indexText[] = new Text[5];
	private Text textEn0, textVi0, textVi1, textEn1, textVi2, textEn2, textVi3,
			textEn3, textVi4, textEn4, pathToViSub, pathToEnSub;
	private Text log;
	private Label lblQuickMoveTo;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Text quickMove;
	private Button btnInsertBefore;
	private Button btnDelete;
	private Button btnNextSpeech;
	private Button btnBackSpeech;

	private SRTFile enFile, viFile;
	private int iSpeech;
	private Text indexText1, indexText0, indexText2, indexText3, indexText4;
	private Button btnInsertAfter;

	/**
	 * Create the application window.
	 */
	public SRTSyncTool() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent e) {
				int count = e.count;
				if (count > 0) {
					backSpeech();
				} else if (count < 0)
					nextSpeech();
			}
		});
		createTexts(container);

		{
			Button btnLoadSubs = new Button(container, SWT.NONE);
			btnLoadSubs.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					loadSubs();
				}
			});
			btnLoadSubs.setBounds(10, 254, 75, 98);
			btnLoadSubs.setText("Load subs");
		}
		{
			log = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.CANCEL);
			log.setBounds(91, 256, 369, 222);
		}

		Button btnQuickMove = new Button(container, SWT.NONE);
		btnQuickMove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				quickMoveClicked();
			}
		});
		btnQuickMove.setBounds(624, 252, 75, 25);
		btnQuickMove.setText("Move");

		Button btnSave = new Button(container, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveSRTFile();
			}
		});
		btnSave.setBounds(10, 380, 75, 98);
		btnSave.setText("Save");
		{
			lblQuickMoveTo = new Label(container, SWT.NONE);
			lblQuickMoveTo.setBounds(473, 258, 87, 25);
			lblQuickMoveTo.setText("Quick move to");
		}

		quickMove = formToolkit.createText(container, "New Text", SWT.NONE);
		quickMove.setText("1");
		quickMove.setBounds(562, 254, 53, 21);
		{
			btnInsertBefore = new Button(container, SWT.NONE);
			btnInsertBefore.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					insertSpeechBefore();
				}
			});
			btnInsertBefore.setBounds(610, 357, 129, 60);
			formToolkit.adapt(btnInsertBefore, true, true);
			btnInsertBefore.setText("Insert Before");
		}
		{
			btnDelete = new Button(container, SWT.NONE);
			btnDelete.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					deleteSpeech();
				}
			});
			btnDelete.setBounds(745, 453, 129, 25);
			formToolkit.adapt(btnDelete, true, true);
			btnDelete.setText("Delete");
		}
		{
			btnNextSpeech = new Button(container, SWT.NONE);
			btnNextSpeech.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					nextSpeech();
				}
			});
			btnNextSpeech.setBounds(475, 357, 129, 60);
			formToolkit.adapt(btnNextSpeech, true, true);
			btnNextSpeech.setText("Next speech v");
		}
		{
			btnBackSpeech = new Button(container, SWT.NONE);
			btnBackSpeech.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					backSpeech();
				}
			});
			btnBackSpeech.setBounds(475, 292, 129, 60);
			formToolkit.adapt(btnBackSpeech, true, true);
			btnBackSpeech.setText("Back speech ^");
		}

		Button btnMoveToNewSpeech = new Button(container, SWT.NONE);
		btnMoveToNewSpeech.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveToNewSpeech();
			}
		});
		btnMoveToNewSpeech.setBounds(745, 292, 129, 60);
		formToolkit.adapt(btnMoveToNewSpeech, true, true);
		btnMoveToNewSpeech.setText("Move to new speech");
		{
			btnInsertAfter = new Button(container, SWT.NONE);
			btnInsertAfter.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					insertSpeechAfter();
				}
			});
			btnInsertAfter.setText("Insert After");
			btnInsertAfter.setBounds(745, 357, 129, 60);
			formToolkit.adapt(btnInsertAfter, true, true);
		}
		return container;
	}

	private void createTexts(Composite container) {
		{
			pathToViSub = new Text(container, SWT.BORDER | SWT.CENTER);
			pathToViSub.setText("test/HIMYM.S02E03.vi.srt");
			pathToViSub.setBounds(473, 225, 401, 21);
		}
		{
			pathToEnSub = new Text(container, SWT.BORDER | SWT.CENTER);
			pathToEnSub.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					Shell shell = Display.getCurrent().getActiveShell();
					// shell.open ();
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);

					String[] filterNames = new String[] { "SRT Files",
							"All Files (*)" };
					String[] filterExtensions = new String[] { "*.srt", "*" };
					String filterPath = "/";
					String platform = SWT.getPlatform();
					if (platform.equals("win32")) {
						filterNames = new String[] { "SRT Files",
								"All Files (*.*)" };
						filterExtensions = new String[] { "*.srt", "*.*" };
						filterPath = "E:\\";
					}
					dialog.setFilterNames(filterNames);
					dialog.setFilterExtensions(filterExtensions);
					dialog.setFilterPath(filterPath);
					dialog.setFileName("");
					String srtPath = dialog.open();
					if (srtPath == null)
						return;
					else {
						pathToEnSub.setText(srtPath);
						if (srtPath.endsWith(".en.srt"))
							pathToViSub.setText(srtPath.replace(".en.srt",
									".vi.srt"));
					}
				}
			});
			pathToEnSub.setText("test/HIMYM.S02E03.en.srt");
			pathToEnSub.setBounds(60, 225, 400, 21);
		}
		{
			enText[0] = textEn0 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textEn0.setBounds(60, 10, 401, 37);
		}
		{
			viText[0] = textVi0 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textVi0.setBounds(473, 10, 401, 37);
		}
		{
			viText[1] = textVi1 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textVi1.setBounds(473, 53, 401, 37);
		}
		{
			enText[1] = textEn1 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textEn1.setBounds(60, 53, 401, 37);
		}
		{
			viText[2] = textVi2 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textVi2.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (iSpeech < viFile.speechs.size()) {
						viFile.speechs.elementAt(iSpeech).content = viText[2]
								.getText();
					}
				}
			});
			textVi2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (iSpeech < viFile.speechs.size()) {
						viFile.speechs.elementAt(iSpeech).content = viText[2]
								.getText();
					}
				}
			});
			textVi2.setEditable(true);
			textVi2.setBounds(473, 96, 401, 37);
		}
		{
			enText[2] = textEn2 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textEn2.setBounds(60, 96, 401, 37);
		}
		{
			viText[3] = textVi3 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textVi3.setBounds(473, 139, 401, 37);
		}
		{
			enText[3] = textEn3 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textEn3.setBounds(60, 139, 401, 37);
		}
		{
			viText[4] = textVi4 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textVi4.setBounds(473, 182, 401, 37);
		}
		{
			enText[4] = textEn4 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.MULTI);
			textEn4.setBounds(60, 182, 401, 37);
		}
		{
			indexText[0] = indexText0 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.CENTER);
			indexText0.setBounds(10, 10, 44, 37);
			formToolkit.adapt(indexText0, true, true);
		}
		{
			indexText[1] = indexText1 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.CENTER);
			indexText1.setBounds(10, 53, 44, 37);
			formToolkit.adapt(indexText1, true, true);
		}
		{
			indexText[2] = indexText2 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.CENTER);
			indexText2.setBounds(10, 96, 44, 37);
			formToolkit.adapt(indexText2, true, true);
		}
		{
			indexText[3] = indexText3 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.CENTER);
			indexText3.setBounds(10, 139, 44, 37);
			formToolkit.adapt(indexText3, true, true);
		}
		{
			indexText[4] = indexText4 = new Text(container, SWT.BORDER
					| SWT.READ_ONLY | SWT.CENTER);
			indexText4.setBounds(10, 182, 44, 37);
			formToolkit.adapt(indexText4, true, true);
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			SRTSyncTool window = new SRTSyncTool();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("SRT Sync Tool @ HQ Production");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(900, 600);
	}

	private void quickMove(int newPost) {
		assert (newPost >= 0);
		iSpeech = newPost;
		quickMove.setText(Integer.toString(newPost));
		int i;
		for (int j = 0; j < 5; j++) {
			i = iSpeech - 2 + j;
			if (i >= 0 && i < enFile.speechs.size()) {
				enText[j].setText(enFile.speechs.elementAt(i).content);
				indexText[j].setText(Integer.toString(i));
			} else {
				enText[j].setText("");
				indexText[j].setText("");
			}

			if (i >= 0 && i < viFile.speechs.size()) {
				viText[j].setText(viFile.speechs.elementAt(i).content);
			} else {
				viText[j].setText("");
			}
		}
	}

	private void nextSpeech() {
		if (iSpeech < enFile.speechs.size() - 1) {
			quickMove(iSpeech + 1);
		}
	}

	private void backSpeech() {
		if (iSpeech > 0) {
			quickMove(iSpeech - 1);
		}
	}

	private void insertSpeechBefore() {
		Speech s = new Speech();
		viFile.speechs.insertElementAt(s, iSpeech);
		quickMove(iSpeech);
	}

	private void insertSpeechAfter() {
		Speech s = new Speech();
		viFile.speechs.insertElementAt(s, iSpeech + 1);
		quickMove(iSpeech + 1);
	}

	private void deleteSpeech() {
		if (iSpeech < viFile.speechs.size()) {
			viFile.speechs.removeElementAt(iSpeech);
			quickMove(iSpeech);
		}
	}

	private void saveSRTFile() {
		Shell shell = Display.getCurrent().getActiveShell();
		// shell.open ();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);

		String[] filterNames = new String[] { "SRT Files", "All Files (*)" };
		String[] filterExtensions = new String[] { "*.srt", "*" };
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32")) {
			filterNames = new String[] { "SRT Files", "All Files (*.*)" };
			filterExtensions = new String[] { "*.srt", "*.*" };
			filterPath = pathToViSub.getText();
		}
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(filterPath);
		dialog.setFileName("");
		String fileName = dialog.open();
		if (fileName == null)
			return;
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(
					fileName), "UTF-8");
			BufferedWriter out = new BufferedWriter(writer);

			out.write(SRTFile.UTF8_BOM);
			for (int i = 0; i < enFile.speechs.size(); i++) {
				Speech en = enFile.speechs.elementAt(i);
				Speech vi = null;
				if (i < viFile.speechs.size())
					vi = viFile.speechs.elementAt(i);
				out.write((i + 1) + "\n");
				out.write(en.begin.toString() + " --> " + en.end.toString()
						+ "\n");
				if (vi != null)
					out.write(vi.content + "\n\n");
				else
					out.write(en.content + "\n\n");
			}

			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void quickMoveClicked() {
		try {
			iSpeech = Integer.parseInt(quickMove.getText());
			if (iSpeech < 0) {
				iSpeech = 0;
			} else if (iSpeech > enFile.speechs.size() - 1) {
				iSpeech = enFile.speechs.size() - 1;
			}

			quickMove(iSpeech);
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Please input an integer.");
			quickMove.setText(Integer.toString(iSpeech));
			return;
		}
	}

	private void loadSubs() {
		enFile = SRTFile.parse(pathToEnSub.getText());
		log.append("En File has " + enFile.speechs.size() + " speechs\n");

		viFile = SRTFile.parse(pathToViSub.getText());
		log.append("Vi File has " + viFile.speechs.size() + " speechs\n");
		quickMove(0);
	}

	private void moveToNewSpeech() {
		Point p = viText[2].getSelection();
		if (p.x == p.y)
			return;
		String s = viText[2].getText();
		String cutString = s.substring(p.x, p.y);
		s = s.substring(0, p.x) + s.substring(p.y);
		viText[2].setText(s);
		insertSpeechAfter();
		viText[2].setText(cutString);
	}
}
