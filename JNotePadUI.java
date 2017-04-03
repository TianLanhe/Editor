import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class JNotePadUI extends JFrame{

	private JTextArea textArea;
	private JLabel stateBar;
	private JMenuItem menuOpen;
	private JMenuItem menuClose;
	private JMenuItem menuSave;
	private JMenuItem menuSaveAs;
	private JMenuItem menuCut;
	private JMenuItem menuCopy;
	private JMenuItem menuPaste;
	private JMenuItem menuAbout;

	private JMenu editMenu;
	private JPopupMenu popupMenu;

	public JNotePadUI(){
		super("�½��ı��ļ�");
		setUpUICompenont();
		setUpEventListener();
		setVisible(true);
	}

	private void setUpUICompenont(){
		setSize(640,480);

		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("�ļ�");
		editMenu = new JMenu("�༭");
		JMenu aboutMenu = new JMenu("����");

		menuOpen = new JMenuItem("��");
		menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		menuSave = new JMenuItem("����");
		menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		menuSaveAs = new JMenuItem("���Ϊ");
		menuClose = new JMenuItem("�ر�");
		menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_MASK));
	
		fileMenu.add(menuOpen);
		fileMenu.addSeparator();
		fileMenu.add(menuSave);
		fileMenu.add(menuSaveAs);
		fileMenu.add(menuClose);

		menuCut = new JMenuItem("����");
		menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		menuCopy = new JMenuItem("����");
		menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		menuPaste = new JMenuItem("ճ��");
		menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));

		editMenu.add(menuCut);
		editMenu.add(menuCopy);
		editMenu.add(menuPaste);

		popupMenu = editMenu.getPopupMenu();

		menuAbout = new JMenuItem("���� JNotePad");
		aboutMenu.add(menuAbout);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);

		setJMenuBar(menuBar);

		textArea = new JTextArea();
		textArea.setFont(new Font("����",Font.PLAIN,16));
		textArea.setLineWrap(true);
		add(new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	
		stateBar = new JLabel("δ�޸�");
		stateBar.setHorizontalAlignment(JLabel.LEFT);
		stateBar.setBorder(BorderFactory.createEtchedBorder());
		add(stateBar,BorderLayout.SOUTH);
	}

	private void setUpEventListener(){
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				closeFile();
			}
		});

		menuOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();
			}
		});

		menuSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFile();
			}
		});

		menuSaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFileAs();
			}
		});

		menuClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeFile();
			}
		});

		menuCut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				textArea.cut();
			}
		});

		menuCopy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				textArea.copy();
			}
		});

		menuPaste.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				textArea.paste();
			}
		});

		menuAbout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAbout();
			}
		});

		textArea.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e){
				processTextArea();
			}
		});

		textArea.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				switch(e.getButton()){
					case MouseEvent.BUTTON1:
						disposePopupMenu();
						break;
					case MouseEvent.BUTTON3:
						showPopupMenu(e);
					break;
				}
			}
		});
	}

	private void openFile(){
		if(isCurrentFileSaved()){
			open();
		}else{
			int option = JOptionPane.showConfirmDialog(null,"�ļ����޸ģ��Ƿ񱣴棿","�����ļ���",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null);

			switch(option){
				case JOptionPane.YES_OPTION:
					saveFile();
				case JOptionPane.NO_OPTION:
					open();			
					break;
			}
		}
	}

	private void open(){
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showDialog(null,null);
		if(option == JFileChooser.APPROVE_OPTION){
			try{
				setTitle(fileChooser.getSelectedFile().toString());

				BufferedReader buf = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
				textArea.setText("");
				stateBar.setText("δ�޸�");
				
				String text;
				while((text=buf.readLine())!=null)
					textArea.append(text+"\n");

				buf.close();
			}catch(IOException e){
				JOptionPane.showMessageDialog(null,e.toString(),"�����ļ�ʧ��",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveFile(){
		File file = new File(getTitle());
		if(!file.exists())
			saveFileAs();
		else{
			try{
				BufferedWriter buf = new BufferedWriter(new FileWriter(file));
				buf.write(textArea.getText());
				stateBar.setText("δ�޸�");
				buf.close();
			}catch(IOException e){
				JOptionPane.showMessageDialog(null,e.toString(),"�����ļ�ʧ��",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveFileAs(){
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showDialog(null,null);
		if(option == JFileChooser.APPROVE_OPTION){
			try{
				File file = fileChooser.getSelectedFile();
				setTitle(file.toString());

				file.createNewFile();
				saveFile();
			}catch(IOException e){
				JOptionPane.showMessageDialog(null,e.toString(),"�޷��������ļ�",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void closeFile(){
		if(isCurrentFileSaved()){
			System.exit(0);
		}else{
			int option = JOptionPane.showConfirmDialog(null,"�ļ����޸ģ��Ƿ񱣴棿","�����ļ���",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null);

			switch(option){
				case JOptionPane.YES_OPTION:
					saveFile();
				case JOptionPane.NO_OPTION:
					System.exit(0);				
					break;
			}
		}
	}

	private Boolean isCurrentFileSaved(){
		return stateBar.getText().equals("δ�޸�") ? true : false;
	}

	private void showAbout(){
		JOptionPane.showOptionDialog(null,
			"�������ƣ�\n 	JNotePad\n" +
			"������ƣ�\n 	XXX\n" +
			"��飺\n 	һ���򵥵����ֱ༭��\n" +
			"	����Ϊ���� Java ��ʵ�ֶ���\n",
			"���� JNotePad",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE,null,null,null);
	}

	private void processTextArea(){
		stateBar.setText("���޸�");
	}

	private void showPopupMenu(MouseEvent e){
		popupMenu.show(editMenu,e.getX(),e.getY());
	}

	private void disposePopupMenu(){
		popupMenu.setVisible(false);
	}
}