package blackbox;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.Border;

import blackbox.CarClasses.Car;

public class BlackBoxSystem {

	CarFacade thisCar = new CarFacade();
	Car car = thisCar.getCar();
	OBD2Port obd = new OBD2Port(thisCar);
	Sensors s = new Sensors(obd);
	RealTimeMonitor realTimeMonitor = new RealTimeMonitor(thisCar, s);

	private JFrame guiFrame;
	private CardLayout cards;
	private JPanel buttonPanel;
	private JPanel cardPanel;
	private JPanel DiagCard;
	private JTextField tfUsername;
	private JPasswordField pfPW;
	private JLabel lblUser; // show the user name on buttonPanel after login
	private JLabel lblFirst; // show welcome user after login
	private JLabel lblWelcome;
	private String name; // username to be displayed

	private JPanel RtmCard; // Card for real-time monitoring
	private JPanel firstCard;

	Connection connection1 = sqliteConnection.dbConnector();
	private JTextField textField;

	TiresCard tc;
	EngineCard ec;

	int x = 0, speed = 15;

	/**
	 * Create the application.
	 */
	public BlackBoxSystem() {

		JPanel welcomeCard; // Welcome page;
		JPanel RateCard; // Card for rating;

		guiFrame = new JFrame();

		guiFrame.setBounds(100, 100, 719, 510);
		// make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		guiFrame.setTitle("Welcome to BlackBox");
		guiFrame.setSize(650, 500);

		// This will center the JFrame in the middle of the screen
		guiFrame.setLocationRelativeTo(null);
		guiFrame.getContentPane().setLayout(new BorderLayout());

		// creating a border to highlight the JPanel areas
		Border outline = BorderFactory.createLineBorder(Color.black);

		welcomeCard = new JPanel();
		welcomeCard.setBackground(new Color(255, 255, 204));

		firstCard = new JPanel();
		/*
		 * firstCard.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseClicked(MouseEvent me) {
		 * cards.show(cardPanel, "Real-Time Monitor"); } });
		 */
		firstCard.setBackground(new Color(255, 255, 204));

		DiagCard = new JPanel();
		DiagCard.setBackground(Color.WHITE);
		DrawGraphics dg = new DrawGraphics();
		DiagCard.add(dg, "Graphics");
		dg.setLayout(null);

		JButton btnDiag = new JButton("Diagnose Fully");
		btnDiag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				FullDiagCard fdc = new FullDiagCard(car, s);
				cardPanel.add(fdc, "Full Diagnose");

				TransitionCard tsc = new TransitionCard();
				cardPanel.add(tsc, "Trans");

				Thread thread = new Thread() {

					public void run() {
						try {
							cards.show(cardPanel, "Trans");
							Thread.sleep(5000);
							fdc.diagnoseFully();
							cards.show(cardPanel, "Full Diagnose");

						} catch (InterruptedException ex) {
						}

					}
				};

				thread.start();
			}
		});
		dg.add(btnDiag);
		btnDiag.setBounds(497, 0, 123, 23);

		RtmCard = new JPanel();
		RtmCard.setBackground(new Color(230, 230, 250));

		JButton btnGo = new JButton("Go");
		dg.add(btnGo);
		btnGo.setBounds(557, 65, 55, 20);
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println(dg.getComboSelected());
				cards.show(cardPanel, dg.getComboSelected());
			}
		});

		RateCard = new JPanel();
		RateCard.setBackground(new Color(248, 248, 255));

		// Create Panels for switching
		cards = new CardLayout();
		cardPanel = new JPanel();
		// set the card layout
		cardPanel.setLayout(cards);
		cards.show(cardPanel, "Welcome");
		cardPanel.add(welcomeCard, "Welcome");
		welcomeCard.setLayout(null);

		lblWelcome = new JLabel("Welcome to BlackBox\u2122 ");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setBounds(48, 60, 530, 57);
		lblWelcome.setFont(new Font("AngsanaUPC", Font.BOLD | Font.ITALIC, 53));
		welcomeCard.add(lblWelcome);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(219, 166, 87, 14);
		welcomeCard.add(lblUsername);

		JLabel lblPW = new JLabel("Password");
		lblPW.setBounds(219, 197, 75, 14);
		welcomeCard.add(lblPW);

		tfUsername = new JTextField();
		tfUsername.setBounds(304, 163, 129, 20);
		welcomeCard.add(tfUsername);
		tfUsername.setColumns(10);

		pfPW = new JPasswordField();
		pfPW.setEchoChar('*');
		pfPW.setBounds(304, 194, 129, 20);
		welcomeCard.add(pfPW);

		cardPanel.add(welcomeCard, "Welcome");

		cardPanel.add(RtmCard, "Real-Time Monitor");
		RtmCard.setLayout(new BorderLayout(0, 0));

		cardPanel.add(DiagCard, "Diagnose");
		cardPanel.add(RateCard, "View Ratings");
		cardPanel.add(firstCard, "First Page");
		firstCard.setLayout(new BorderLayout());
		firstCard.setBackground(new Color(240, 240, 240));

		tc = new TiresCard(car, s);
		cardPanel.add(tc, "Tires");

		JButton tireTest = new JButton("Check");
		tireTest.setBounds(380, 10, 100, 24);
		tireTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TransitionCard tsc = new TransitionCard();
				cardPanel.add(tsc, "Trans");

				Thread thread = new Thread() {

					public void run() {
						try {
							cards.show(cardPanel, "Trans");
							Thread.sleep(1500);
							tc.diagnoseTires();
							cards.show(cardPanel, "Tires");

						} catch (InterruptedException ex) {
						}
					}
				};
				thread.start();
			}
		});
		tc.add(tireTest);

		ec = new EngineCard(car, s);
		cardPanel.add(ec, "Engine");

		JButton engTest = new JButton("Check");
		engTest.setBounds(380, 10, 100, 24);
		engTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TransitionCard tsc = new TransitionCard();
				cardPanel.add(tsc, "Trans");

				Thread thread = new Thread() {

					public void run() {
						try {
							cards.show(cardPanel, "Trans");
							Thread.sleep(1500);
							ec.diagnoseEngine();
							cards.show(cardPanel, "Engine");

						} catch (InterruptedException ex) {
						}
					}
				};

				thread.start();
			}
		});
		ec.add(engTest);

		// This panel will contain one button to enable you
		// to switch thro' the cards
		buttonPanel = new JPanel();
		buttonPanel.setBorder(outline);
		buttonPanel.setVisible(false); // Not visible before successful login

		// Add switch buttons for user to choose
		addSwitch("Real-Time Monitor");
		addSwitch("Diagnose");
		addSwitch("View Ratings");

		// Car starts upon clicking the button
		// It will automatically switch to Real-Time Monitor
		JButton startEngine = new JButton("");
		startEngine.setSize(200, 200);
		ImageIcon image = new ImageIcon("image/EngineStart.png");
		Image im = image.getImage();
		Image scaledImage = im.getScaledInstance(startEngine.getWidth(), startEngine.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(scaledImage);
		startEngine.setIcon(icon);
		startEngine.setBackground(new Color(240, 240, 240));
		firstCard.add(startEngine, BorderLayout.CENTER);

		startEngine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				thisCar.startCar();
				thisCar.start();

				cards.show(cardPanel, "Real-Time Monitor");

				realTimeMonitor = new RealTimeMonitor(thisCar, s);
				realTimeMonitor.startRun();
				RtmCard.add(realTimeMonitor);
			}

		});

		// Add exit button to close the application
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		btnExit.setActionCommand("close");
		buttonPanel.add(btnExit);

		// Add logout button for another user
		JButton btnLO = new JButton("Logout");
		btnLO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure to logout?", "Logout",
						JOptionPane.YES_NO_OPTION);

				if (confirmation == 0) {
					cards.show(cardPanel, "Welcome");
					buttonPanel.setVisible(false);
					lblUser.setText("");
					lblFirst.setText("");
					lblWelcome.setText("Login As Another User");

					thisCar.stopCar(); // stop the current thread;
					thisCar.stop();

					thisCar = new CarFacade();

					thisCar.setCarStopped(true);

					realTimeMonitor.stopRun();
				}
			}
		});
		btnLO.setActionCommand("Logout");
		buttonPanel.add(btnLO);

		guiFrame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
		guiFrame.getContentPane().add(cardPanel, BorderLayout.CENTER);
		guiFrame.setVisible(true);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					String user = tfUsername.getText().trim();
					String pw = pfPW.getText().trim();

					String query = "select username,password from loginInfo where username ='" + user
							+ "' and password = '" + pw + "'";
					PreparedStatement pst = connection1.prepareStatement(query);

					ResultSet rs = pst.executeQuery();

					// Username & password are unique in group
					if (rs.next()) {

						name = rs.getString("username");
						JOptionPane.showMessageDialog(null, "Login Successfully");

						// clear the fields for reentering
						clearUserInfo();

						// After login successfully, user can see the button
						// panel
						buttonPanel.setVisible(true);
						lblUser = new JLabel(name);
						buttonPanel.add(lblUser);
						cards.show(cardPanel, "First Page");

						lblFirst = new JLabel("");
						lblFirst.setText("Welcome, " + name + "!");
						lblFirst.setHorizontalAlignment(SwingConstants.CENTER);
						lblFirst.setBounds(48, 60, 530, 57);
						lblFirst.setFont(new Font("AngsanaUPC", Font.BOLD | Font.ITALIC, 53));
						firstCard.add(lblFirst, BorderLayout.NORTH);

					} else {
						JOptionPane.showMessageDialog(null,
								"Username and password are not matached. Please login again");
						// clear the fields for reentering
						clearUserInfo();
					}

					rs.close();
					pst.close();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex);
				}
			}
		});
		btnLogin.setBounds(261, 237, 98, 23);
		welcomeCard.add(btnLogin);

		// Button to exit
		JButton button = new JButton("Exit");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		button.setActionCommand("close");
		button.setBounds(369, 237, 64, 23);
		welcomeCard.add(button);

	}

	private void addSwitch(String command) {

		JButton btn = new JButton(command);
		btn.setActionCommand(command);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// navigate to the next component
				cards.show(cardPanel, command);

				if (command.equals("Diagnose")) { // Clear the fields for next
													// Diagnose
					ec.clearEC();
					tc.clearTC();
				}
			}
		});

		buttonPanel.add(btn);
		guiFrame.getContentPane().add(buttonPanel, BorderLayout.NORTH);

	}

	private void clearUserInfo() {
		tfUsername.setText("");
		pfPW.setText("");
	}

}