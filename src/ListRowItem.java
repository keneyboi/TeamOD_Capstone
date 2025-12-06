import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListRowItem extends JPanel {
    public ListRowItem(String title, String subtitle) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setBackground(UIManager.getColor("Panel.background"));


        JPanel textContentPanel = new JPanel();
        textContentPanel.setLayout(new BoxLayout(textContentPanel, BoxLayout.Y_AXIS));
        textContentPanel.setOpaque(false);
        textContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // event or student names
        JTextArea titleArea = new JTextArea(title);
        titleArea.setFont(new Font("Arial", Font.BOLD, 14));
        titleArea.setOpaque(false);
        titleArea.setEditable(false);
        titleArea.setFocusable(false);
        titleArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // late time or attendacne start or end
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // add the name and time to the jpanel
        textContentPanel.add(titleArea);
        textContentPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        textContentPanel.add(subLabel);

        add(textContentPanel, BorderLayout.CENTER);
        titleArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MouseEvent newEvent = SwingUtilities.convertMouseEvent(titleArea, e, ListRowItem.this);
                ListRowItem.this.dispatchEvent(newEvent);
            }
        });
    }
}