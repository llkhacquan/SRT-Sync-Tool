package quannk.srtsynctool;

import quannk.srtsynctool.listeners.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javafx.scene.input.KeyCharacterCombination;

import javax.swing.JOptionPane;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SRTSyncTool extends ApplicationWindow {
  public String defaultPathEn = "";
  public String defaultPathVi = "";
  public Text enText[] = new Text[5];
  public Text viText[] = new Text[5];
  public Text indexText[] = new Text[5];
  public Text textEn0, textVi0, textVi1, textEn1, textVi2, textEn2, textVi3, textEn3, textVi4, textEn4, pathToViSub, pathToEnSub;
  public Text log;
  public Label lblQuickMoveTo;
  public final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
  public Text quickMove;
  public Button btnInsertBefore;
  public Button btnDelete;
  public Button btnNextSpeech;
  public Button btnBackSpeech;

  public SRTFile enFile;
  public SRTFile viFile;
  public int iSpeech;
  public Text indexText1, indexText0, indexText2, indexText3, indexText4;
  public Button btnInsertAfter;
  public Text textStatus;
  private Button btnMergeWithFollowing;

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
    container.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.PAGE_DOWN) {
          quickMove(iSpeech + 4);
        } else if (e.keyCode == SWT.PAGE_DOWN) {
          quickMove(iSpeech - 4);
        }
      }
    });
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
      btnLoadSubs.setBounds(10, 354, 75, 98);
      btnLoadSubs.setText("Load subs");
    }
    {
      log = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
      log.setBounds(91, 356, 369, 222);
    }

    Button btnQuickMove = new Button(container, SWT.NONE);
    btnQuickMove.addSelectionListener(new QuickMoveSelectedAdapter(this));
    btnQuickMove.setBounds(624, 382, 75, 25);
    btnQuickMove.setText("Move");

    Button btnSave = new Button(container, SWT.NONE);
    btnSave.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSRTFile();
      }
    });
    btnSave.setBounds(10, 480, 75, 98);
    btnSave.setText("Save");
    {
      lblQuickMoveTo = new Label(container, SWT.NONE);
      lblQuickMoveTo.setBounds(473, 387, 87, 21);
      lblQuickMoveTo.setText("Quick move to");
    }

    quickMove = formToolkit.createText(container, "New Text", SWT.NONE);
    quickMove.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          btnQuickMove.notifyListeners(SWT.Selection, null);
        }
      }
    });

    quickMove.setText("1");
    quickMove.setBounds(562, 384, 53, 21);
    {
      btnInsertBefore = new Button(container, SWT.NONE);
      btnInsertBefore.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          insertSpeechBefore();
        }
      });
      btnInsertBefore.setBounds(610, 422, 129, 60);
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
      btnDelete.setBounds(745, 553, 129, 25);
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
      btnNextSpeech.setBounds(475, 487, 129, 60);
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
      btnBackSpeech.setBounds(475, 422, 129, 60);
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
    btnMoveToNewSpeech.setBounds(745, 422, 129, 60);
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
      btnInsertAfter.setBounds(610, 487, 129, 60);
      formToolkit.adapt(btnInsertAfter, true, true);
    }

    textStatus = new Text(container, SWT.BORDER | SWT.CENTER);
    textStatus.setEnabled(false);
    textStatus.setEditable(false);
    textStatus.setBounds(473, 352, 401, 21);
    formToolkit.adapt(textStatus, true, true);
    {
      btnMergeWithFollowing = new Button(container, SWT.NONE | SWT.MULTI);
      btnMergeWithFollowing.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          mergeWithFollowing();
        }
      });
      btnMergeWithFollowing.setText("Append next speech");
      btnMergeWithFollowing.setBounds(745, 487, 129, 60);
      formToolkit.adapt(btnMergeWithFollowing, true, true);
    }
    return container;
  }

  private void createTexts(Composite container) {
    {
      pathToViSub = new Text(container, SWT.BORDER | SWT.CENTER);
      pathToViSub.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
          pathToViSubDoubleClicked();
        }
      });
      pathToViSub.setText(defaultPathEn);
      pathToViSub.setBounds(473, 325, 401, 21);
    }
    {
      pathToEnSub = new Text(container, SWT.BORDER | SWT.CENTER);
      pathToEnSub.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
          pathToEnSubDoubleClicked();
        }
      });
      pathToEnSub.setText(defaultPathVi);
      pathToEnSub.setBounds(60, 325, 400, 21);
    }
    {
      enText[0] = textEn0 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textEn0.setBounds(60, 10, 401, 52);
    }
    {
      viText[0] = textVi0 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textVi0.setBounds(473, 10, 401, 52);
    }
    {
      viText[1] = textVi1 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textVi1.setBounds(473, 67, 401, 52);
    }
    {
      enText[1] = textEn1 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textEn1.setBounds(60, 67, 401, 52);
    }
    {
      viText[2] = textVi2 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textVi2.addMouseWheelListener(new MouseWheelListener() {
        public void mouseScrolled(MouseEvent e) {
          int count = e.count;
          if (count > 0) {
            backSpeech();
          } else if (count < 0)
            nextSpeech();
        }
      });
      textVi2.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          if (iSpeech < viFile.speechs.size()) {
            viFile.speechs.elementAt(iSpeech).content = viText[2].getText();
          }
        }
      });
      textVi2.addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          if (iSpeech < viFile.speechs.size()) {
            viFile.speechs.elementAt(iSpeech).content = viText[2].getText();
          }
        }
      });
      textVi2.setEditable(true);
      textVi2.setBounds(473, 125, 401, 52);
    }
    {
      enText[2] = textEn2 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textEn2.setBounds(60, 125, 401, 52);
    }
    {
      viText[3] = textVi3 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textVi3.setBounds(473, 183, 401, 52);
    }
    {
      enText[3] = textEn3 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textEn3.setBounds(60, 183, 401, 52);
    }
    {
      viText[4] = textVi4 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textVi4.setBounds(473, 241, 401, 52);
    }
    {
      enText[4] = textEn4 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
      textEn4.setBounds(60, 241, 401, 52);
    }
    {
      indexText[0] = indexText0 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
      indexText0.setEnabled(false);
      indexText0.setBounds(10, 10, 44, 52);
      formToolkit.adapt(indexText0, true, true);
    }
    {
      indexText[1] = indexText1 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
      indexText1.setEnabled(false);
      indexText1.setBounds(10, 67, 44, 52);
      formToolkit.adapt(indexText1, true, true);
    }
    {
      indexText[2] = indexText2 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
      indexText2.setEnabled(false);
      indexText2.setBounds(10, 125, 44, 52);
      formToolkit.adapt(indexText2, true, true);
    }
    {
      indexText[3] = indexText3 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
      indexText3.setEnabled(false);
      indexText3.setBounds(10, 183, 44, 52);
      formToolkit.adapt(indexText3, true, true);
    }
    {
      indexText[4] = indexText4 = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
      indexText4.setEnabled(false);
      indexText4.setBounds(10, 241, 44, 52);
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
      SRTFile.gui = window;
      if (args.length >= 2) {
        window.defaultPathEn = args[0];
        window.defaultPathVi = args[1];
      }
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
    return new Point(900, 700);
  }

  public void quickMove(int newPost) {
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

  private void updateSpeechNumbers() {
    if (enFile != null && viFile != null)
      textStatus.setText("En-" + enFile.speechs.size() + "   Vi-" + viFile.speechs.size());
    else
      textStatus.setText("");
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
    updateSpeechNumbers();
  }

  private void insertSpeechAfter() {
    Speech s = new Speech();
    viFile.speechs.insertElementAt(s, iSpeech + 1);
    quickMove(iSpeech + 1);
    updateSpeechNumbers();
  }

  private void deleteSpeech() {
    if (iSpeech < viFile.speechs.size()) {
      viFile.speechs.removeElementAt(iSpeech);
      quickMove(iSpeech);
    }
    updateSpeechNumbers();
  }

  private void saveSRTFile() {
    Shell shell = Display.getCurrent().getActiveShell();
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

    String fileName = pathToViSub.getText();
    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);

    dialog.setFileName(fileName);
    fileName = dialog.open();
    if (fileName == null)
      return;
    try {
      Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
      BufferedWriter out = new BufferedWriter(writer);

      out.write(SRTFile.UTF8_BOM);
      for (int i = 0; i < enFile.speechs.size(); i++) {
        Speech en = enFile.speechs.elementAt(i);
        Speech vi = null;
        if (i < viFile.speechs.size())
          vi = viFile.speechs.elementAt(i);
        out.write((i + 1) + "\n");
        out.write(en.begin.toString() + " --> " + en.end.toString() + "\n");
        if (vi != null)
          out.write(vi.content + "\n\n");
        else
          out.write(en.content + "\n\n");
      }

      out.close();
    } catch (IOException e1) {
      JOptionPane.showMessageDialog(null, "Error!");
      e1.printStackTrace();
      return;
    }
    JOptionPane.showMessageDialog(null, "Done");
  }

  private void loadSubs() {
    if (pathToEnSub.getText() == "") {
      pathToEnSubDoubleClicked();
    }
    if (pathToEnSub.getText() == "")
      return;
    if (pathToViSub.getText() == "") {
      pathToViSubDoubleClicked();
    }
    if (pathToViSub.getText() == "")
      return;
    enFile = SRTFile.parse(pathToEnSub.getText());
    log.append("En File has " + enFile.speechs.size() + " speechs\n");

    viFile = SRTFile.parse(pathToViSub.getText());
    log.append("Vi File has " + viFile.speechs.size() + " speechs\n");
    quickMove(0);
    updateSpeechNumbers();
  }

  private void moveToNewSpeech() {
    Point p = viText[2].getSelection();
    if (p.x == p.y)
      return;
    String s = viText[2].getText();
    String cutString = s.substring(p.x, p.y);
    s = s.substring(0, p.x) + s.substring(p.y);
    viText[2].setText(s.trim());
    insertSpeechAfter();
    viText[2].setText(cutString.trim());
    updateSpeechNumbers();
  }

  private void mergeWithFollowing() {
    if (iSpeech >= viFile.speechs.size() - 1)
      return;
    String t = viText[3].getText().trim();
    String t2 = viText[2].getText().trim();
    viText[2].setText(t2 + "\n" + t);
    quickMove(iSpeech + 1);
    deleteSpeech();
    quickMove(iSpeech - 1);
    updateSpeechNumbers();
  }

  private void pathToEnSubDoubleClicked() {
    Shell shell = Display.getCurrent().getActiveShell();
    // shell.open ();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);

    String[] filterNames = new String[] { "SRT Files", "All Files (*)" };
    String[] filterExtensions = new String[] { "*.srt", "*" };
    String platform = SWT.getPlatform();
    if (platform.equals("win32")) {
      filterNames = new String[] { "SRT Files", "All Files (*.*)" };
      filterExtensions = new String[] { "*.srt", "*.*" };
    }
    dialog.setFilterNames(filterNames);
    dialog.setFilterExtensions(filterExtensions);
    dialog.setFileName("");
    String srtPath = dialog.open();
    if (srtPath == null)
      return;
    else {
      pathToEnSub.setText(srtPath);
      String srtPath2 = srtPath.replace("en.srt", "vi.srt").replace("En.srt", "Vi.srt");
      if (srtPath.endsWith(".en.srt"))
        if (new File(srtPath2).exists())
          pathToViSub.setText(srtPath2);
    }
  }

  public MessageBox createMessageBox(Object content) {
    MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_INFORMATION | SWT.OK);
    messageBox.setText("Info");
    messageBox.setMessage(content.toString());
    messageBox.open();
    return messageBox;
  }

  private void pathToViSubDoubleClicked() {
    Shell shell = Display.getCurrent().getActiveShell();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);

    String[] filterNames = new String[] { "SRT Files", "All Files (*)" };
    String[] filterExtensions = new String[] { "*.srt", "*" };
    String platform = SWT.getPlatform();
    if (platform.equals("win32")) {
      filterNames = new String[] { "SRT Files", "All Files (*.*)" };
      filterExtensions = new String[] { "*.srt", "*.*" };
    }
    dialog.setFilterNames(filterNames);
    dialog.setFilterExtensions(filterExtensions);
    dialog.setFileName("");
    String srtPath = dialog.open();
    if (srtPath == null)
      return;
    else {
      pathToViSub.setText(srtPath);
      String srtPath2 = srtPath.replace("vi.srt", "en.srt").replace("Vi.srt", "En.srt");
      if (srtPath.endsWith(".vi.srt"))
        if (new File(srtPath2).exists())
          pathToEnSub.setText(srtPath2);
    }
  }
}
