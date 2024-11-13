package cobot;

import javax.swing.*;
import java.awt.*;

public class RobotPanel extends JPanel {

    private final JLabel DYNAMIC_STATUS_LABEL;
	private final RobotPanelHandler CONTROLLER;

	public RobotPanel(RobotPanelHandler controller) {
		this.CONTROLLER = controller;

        JLabel staticStatusLabel = new JLabel("Status: ");
		staticStatusLabel.setFont(new Font("Arial", Font.BOLD, 20));
		staticStatusLabel.setForeground(Color.BLACK);

		DYNAMIC_STATUS_LABEL = new JLabel("Idle");
		DYNAMIC_STATUS_LABEL.setFont(new Font("Arial", Font.BOLD, 20));
		DYNAMIC_STATUS_LABEL.setForeground(Color.RED);

		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(staticStatusLabel);
		add(DYNAMIC_STATUS_LABEL);

		Blackboard.getInstance().addPropertyChangeListener(evt -> {
			if ("ProgressUpdated".equals(evt.getPropertyName())) {
				setRunningStatus();
			} else if ("AnglesAdded".equals(evt.getPropertyName())) {
				setIdleStatus();
			}
			repaint();
		});
	}

	public void setRunningStatus() {
		DYNAMIC_STATUS_LABEL.setText("Running");
		DYNAMIC_STATUS_LABEL.setForeground(Color.decode("#008000"));
	}

	public void setIdleStatus() {
		DYNAMIC_STATUS_LABEL.setText("Idle");
		DYNAMIC_STATUS_LABEL.setForeground(Color.RED);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawArm(g);
	}

	private void drawArm(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(6));

		int x = 300, y = 450;
		int length = 50;

		Color[] colors = {Color.decode("#6667ab"), Color.decode("#f18aad"), Color.decode("#ea6759"),
				Color.decode("#f88f58"), Color.decode("#f3c65f"), Color.decode("#8bc28c")};

		int[] currentAngles = CONTROLLER.getCurrentAngles();
		Point[] jointPositions = new Point[currentAngles.length + 1];
		jointPositions[0] = new Point(x, y);

		for (int i = 0; i < currentAngles.length; i++) {
			g2d.setColor(colors[i]);

			Point newEndPoint = drawSegment(g2d, x, y, length, currentAngles[i]);
			x = newEndPoint.x;
			y = newEndPoint.y;

			jointPositions[i + 1] = newEndPoint;
		}

		for (Point joint : jointPositions) {
			drawJoint(g2d, joint.x, joint.y);
		}
	}

	private Point drawSegment(Graphics2D g2d, int x1, int y1, int length, int angle) {
		int x2 = x1 + (int) (length * Math.cos(Math.toRadians(angle)));
		int y2 = y1 - (int) (length * Math.sin(Math.toRadians(angle)));
		g2d.drawLine(x1, y1, x2, y2);
		return new Point(x2, y2);
	}

	private void drawJoint(Graphics2D g2d, int x, int y) {
		g2d.setColor(Color.WHITE);
		g2d.fillOval(x - 5, y - 5, 10, 10);
	}
}
